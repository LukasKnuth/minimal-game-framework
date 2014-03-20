package org.knuth.mgf;

import javax.swing.*;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * <p>A {@code Scene} encapsulates multiple event-objects which shall only be active
 *  in this very {@code Scene}.</p>
 * <p>This is a very easy way to separate multiple "scenes" in the game from one
 *  another. An example might be the menu and the game itself.</p>
 *
 * <h3>Building Scenes</h3>
 * <p>Scenes are build by extending the {@code Scene}-class as overwriting one or
 *  multiple of the live-cycle hooks.</p>
 * <p>A scene must be added to the {@code GameLoop} (for example in the game-
 *  bootstrap) and then be started from that class.</p>
 *
 *  <h3>Time in scenes</h3>
 *  <p>Time is local to scenes, meaning that events scheduled with
 *   {@link org.knuth.mgf.GameLoop#scheduleCallback(Callback, TimeSpan)} are scheduled
 *   on the current scene. If the scene is paused (due to a switch to another scene),
 *   time will effectively stop until the scene becomes active again.</p>
 *  <p>Therefor, the preferred way to pause a scene is, to switch to another scene.</p>
 *
 * <h3>Scene Live-Cycle</h3>
 * <p>Every scene has a live-cycle, in which you can hook in and execute custom
 *  code when each state of the cycle is entered or re-entered:</p>
 * <table>
 *     <tr>
 *         <th>Method</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>{@link #onStart}</td>
 *         <td>
 *             <p>This method will be called when the {@code Scene} is first created
 *              and will only be called <b>once</b>.</p>
 *             <p>Initial work for the {@code Scene} should be done in this method,
 *              including the use of the given {@link SceneBuilder}-object to register
 *              all necessary events that are used by the scene.</p>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>{@link #onResume}</td>
 *         <td>
 *             <p>Other than {@code onStart}, this method will be called every time
 *              a scene is started or restarted, including the first start right after
 *              {@code onStart} returns.</p>
 *             <p>After this method is called, the scene is considered initialized,
 *              started and playing.</p>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>{@link #onPause}</td>
 *         <td>
 *             <p>If another scene is coming to the front, this method is called to
 *              <i>pause</i> any running or pending actions, before the other scene
 *              takes over.</p>
 *             <p>If a scene has been paused, it's not guaranteed that it will be
 *              resumed. The only method which is guaranteed to be called is the
 *              {@link #onStop}-method.</p>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>{@link #onStop}</td>
 *         <td>
 *             <p>This Hook will be called, when the {@code GameLoop} (and therefor
 *              the whole game) is shutting down. It should be used to free any
 *              occupied memory and do final work (if needed).</p>
 *             <p>After this method has been called, the scene is considered "dead"
 *              and will never be restarted!</p>
 *         </td>
 *     </tr>
 * </table>
 *
 * <p>Scenes can be manually started and paused, but not stopped. The system will take
 *  care of initially starting the scene and terminating it when the game shuts down.</p>
 *
 * @author Lukas Knuth
 * @version 1.0
 */
public class Scene {

    // TODO Add scene-flags to re-draw the last state of the previous scene (store Graphics object?)

    /**
     * <p>The possible states of the current {@code Scene}.This is for internal
     *  purposes only!</p>
     * <ul>
     *     <li>{@code PENDING} - Scene has not yet started and been set up</li>
     *     <li>{@code PLAYING} - The scene is set-up and currently playing</li>
     *     <li>{@code PAUSED} - The scene is set-up but not currently playing</li>
     *     <li>{@code STOPPED} - The scene has been shut down and has cleaned up</li>
     * </ul>
     */
    enum State{
        PENDING, PLAYING, PAUSED, STOPPED
    }
    private State current_state = State.PENDING;
    private boolean is_frozen = false;

    /** The time-stamp (in microseconds) when the game-loop was started */
    private long start_stamp;
    /** The time-stamp of the moment the game was last paused/frozen */
    private long pause_stamp;
    /** The combined amount of time (in microseconds) that the game was paused/frozen */
    private long excluded_time;

    /**
     * <p>The {@code SceneBuilder} is used to add all events and necessary elements to the
     *  {@code Scene}.</p>
     * <p>The builder will only be functional in the
     *  {@link Scene#onStart(org.knuth.mgf.Scene.SceneBuilder)}-method!</p>
     */
    public final class SceneBuilder {

        private SceneBuilder(){}

        /**
         * Add a new {@link MovementEvent} to this scene.
         * @param event the new element to add.
         */
        public void addMovementEvent(MovementEvent event){
            // Check if locked:
            if (current_state == State.PENDING)
                movementEvents.add(event);
        }

        /**
         * Add a new {@link RenderEvent} to the scene.
         * @param event the new element to add.
         * @param zIndex the z-index this element should be drawn at. The higher the
         *  specified z-index, the higher is the "layer" on which the element is drawn.
         *  <p>E.g. a z-index of {@code 2} overlaps a z-index of {@code 1}.</p>
         */
        public void addRenderEvent(RenderEvent event, int zIndex){
            // Check if locked:
            if (current_state == State.PENDING){
                RenderContainer re = new RenderContainer(zIndex,event);
                renderEvents.add(re);
            }
        }

        /**
         * <p>Add a new {@link CollisionEvent} to the scene.</p>
         * <p>If you need to use any {@code CollisionEvent}s in this scene, you'll also
         *  need to add a {@link Map} to perform the collision checks on.</p>
         * @param event the new element to add.
         * @see #setMap(Map)
         */
        public void addCollisionEvent(CollisionEvent event){
            if (current_state == State.PENDING)
                collisionEvents.add(event);
        }

        /**
         * Set the {@link Map}, on which the game is played.
         * @param map the map to use.
         * @see Map
         */
        public void setMap(Map map){
            if (current_state == State.PENDING)
                game_field = map;
        }

        /**
         * <p>Binds a given key-code with the specified modifiers to a given {@link javax.swing.Action}.</p>
         * <p>Possible key-codes can be retrieved from the {@link java.awt.event.KeyEvent}-class.</p>
         * <p>To remove a key-binding, give {@code null} as the action.</p>
         * @param key_code the key-code to bind the action to. See the {@link java.awt.event.KeyEvent}-class.
         * @param modifiers a combination of possible modifiers or {@code 0} for no modifiers.
         *  See {@link javax.swing.KeyStroke#getKeyStroke(int, int)}
         * @param released {@code true} if the action is bind to a key-release event,
         *  {@code false} otherwise (key-press event).
         * @param action the {@link javax.swing.Action} to be executed when the given key was pressed. If
         *  value is {@code null}, the key-binding will be removed.
         */
        public void putKeyBinding(int key_code, int modifiers, boolean released, Action action){
            KeyStroke stroke = KeyStroke.getKeyStroke(key_code, modifiers, released);
            String action_name = ""+key_code+released+modifiers;
            // Check if add or remove:
            if (action == null){
                // Remove binding:
                inputMap.remove(stroke);
                actionMap.remove(action_name);
            } else {
                // Add binding
                inputMap.put(stroke, action_name);
                actionMap.put(action_name, action);
            }
        }
    }
    /** All registered {@link MovementEvent}s */
    final List<MovementEvent> movementEvents;
    /** All registered {@link RenderEvent}s */
    final List<RenderContainer> renderEvents;
    /** All registered {@link CollisionEvent}s */
    final List<CollisionEvent> collisionEvents;
    /** The {@link Map} the game takes place on */
    Map game_field;
    /** The {@link Map} with all key-bindings for this scene */
    final InputMap inputMap;
    final ActionMap actionMap;
    /** Scheduled callbacks on this scene */
    private final AbstractQueue<ScheduledCallback> schedule;

    /**
     * Force sub-classing to initialize new objects.
     */
    protected Scene(){
        movementEvents = new ArrayList<MovementEvent>();
        renderEvents = new ArrayList<RenderContainer>();
        collisionEvents = new ArrayList<CollisionEvent>();
        inputMap = new ComponentInputMap(GameLoop.INSTANCE.Viewport.getView());
        actionMap = new ActionMap();
        schedule = new PriorityQueue<ScheduledCallback>();
    }

    // ############### TIME #####################

    /**
     * @see org.knuth.mgf.GameLoop#scheduleCallback(Callback, TimeSpan)
     */
    ScheduledCallback scheduleCallback(Callback callback, TimeSpan wait_for){
        ScheduledCallback sch_call = new ScheduledCallback(
                callback, wait_for, this, getSceneTime().add(wait_for)
        );
        this.schedule.add(sch_call);
        return sch_call;
    }

    /**
     * @see org.knuth.mgf.GameLoop#rescheduleCallback(ScheduledCallback)
     */
    void rescheduleCallback(ScheduledCallback callback){
        // Check if this callback is already scheduled:
        if (this.schedule.contains(callback)){
            throw new IllegalStateException("This callback is already scheduled.");
        }
        // Update the time to execute:
        TimeSpan wait_for = callback.getTimeToWait();
        callback.reschedule(getSceneTime().add(wait_for));
        // add to queue:
        this.schedule.add(callback);
    }

    /**
     * @see ScheduledCallback#cancel()
     */
    public void cancelCallback(ScheduledCallback callback) {
        this.schedule.remove(callback);
    }

    /**
     * Check if any previously scheduled callbacks are to be called now.
     */
    void checkCallbacks(){
        TimeSpan scene_time = getSceneTime();
        for (int i = 0; i < this.schedule.size(); i++){
            if (scene_time.isGreaterThen(this.schedule.peek().when())){
                ScheduledCallback callback = this.schedule.poll();
                callback.execute();
            } else {
                /*
                    If the this element (scheduled for "earlier" execution than the rest)
                    doesn't fit yet, no others (scheduled for "later" execution) will. So,
                    we can stop here and return.
                 */
                return;
            }
        }
    }

    /**
     * Get the current time in this scene.
     */
    TimeSpan getSceneTime(){
        return new TimeSpan(
                System.nanoTime() - start_stamp - excluded_time
        );
    }

    /**
     * @see GameLoop#freeze()
     */
    void freeze(){
        this.is_frozen = true;
    }

    /**
     * @see GameLoop#unfreeze()
     */
    void unfreeze(){
        this.is_frozen = false;
    }

    /**
     * @see GameLoop#isFrozen()
     */
    boolean isFrozen(){
        return this.is_frozen;
    }

    // ############## LIVECYCLE ################

    State getSceneState(){
        return this.current_state;
    }

    /**
     * Called by the {@link GameLoop} to do basic work, then calls
     *  the {@link #onStart(SceneBuilder)}-hook to do custom work.
     * @throws IllegalStateException if the new scene added {@link CollisionEvent}s,
     *  but did not set a {@link Map}.
     */
    void onStartCall(){
        // Store the current time:
        start_stamp = System.nanoTime();
        // Call Hook:
        this.onStart(new SceneBuilder());
        // Check if we have a map when a CollisionEvent is presented:
        if (collisionEvents.size() > 0 && game_field == null)
            throw new IllegalStateException(
                    "Can't start the game without a map if you want CollisionEvents in this Scene!");
        current_state = State.PLAYING;
    }

    /**
     * Called by the {@link GameLoop} to do basic work, then calls
     *  the {@link #onPause()}-hook to do custom work.
     */
    void onPauseCall(){
        // Call Hook:
        this.onPause();
        current_state = State.PAUSED;
        pause_stamp = System.nanoTime();
    }

    /**
     * Called by the {@link GameLoop} to do basic work, then calls
     *  the {@link #onResume()}-hook to do custom work.
     */
    void onResumeCall(){
        this.onResume();
        if (current_state == State.PAUSED){
            // unpausing, add the paused time to the excluded time:
            excluded_time += System.nanoTime() - pause_stamp;
        }
        current_state = State.PLAYING;
    }

    /**
     * Called by the {@link GameLoop} to do basic work, then calls
     *  the {@link #onStop()}-hook to do custom work.
     */
    void onStopCall(){
        this.onStop();
        current_state = State.STOPPED;
    }

    // --------- The Hooks which can be overwritten -----------

    protected void onStart(SceneBuilder builder){
        // First started, initial work
    }

    protected void onPause(){
        // Paused
    }

    protected void onResume(){
        // Resumed
    }

    protected void onStop(){
        // Game closing, free memory
    }

}
