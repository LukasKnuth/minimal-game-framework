package org.knuth.mgf;

import org.knuth.mgf.input.InputDevice;
import org.knuth.mgf.input.Keyboard;
import org.knuth.mgf.input.Mouse;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>The game-loop which manages the games {@code Scene}s and calls events
 *  of the current scene. The loop will try to keep the frame-rate at 60FPS,
 *  if possible.</p>
 *
 * <p>The basic workflow is as follows:</p>
 * <ol>
 *     <li>Create a subclass of {@link Scene} which holds all events for that
 *      part of the game.</li>
 *     <li>Add the scene to the {@code GameLoop} by using the
 *      {@link #addScene(String, Scene)}-method</li>
 *     <li>Start the loop with the {@link #startLoop()}-method.</li>
 * </ol>
 * <p>When the game is closed, you should call the {@link #stopLoop()}-method
 *  to ensure a graceful termination of the loop and the rest of the game.</p>
 *
 * <p>The order in which the events registered in the current {@code Scene}
 *  are called in the following order:</p>
 * <ol>
 *     <li>{@code CollisionEvent}</li>
 *     <li>{@code MovementEvent}</li>
 *     <li>{@code RenderEvent}</li>
 * </ol>
 * 
 * @author Lukas Knuth
 * @author Fabain Bottler
 * @version 1.0
 */
public enum GameLoop{

    /** The instance to work with */
    INSTANCE;
    
    /** Indicates if the game-loop is currently running */
    private boolean isRunning;

    /** The executor-service running the main game-loop */
    private ExecutorService game_loop_executor;
    /** Times the game is updated, per second */
    private static final int UPDATES_PER_SECOND = 25;
    /** Time to wait until the next game update */
    private static final int WAIT_TICKS = 1000 / UPDATES_PER_SECOND;
    /** The maximum repaint-count (frames) that can be skipped if the game-update takes too long */
    private static final int MAX_FRAMESKIP = 5;
    /** Maximum FPS */
    private static final int MAX_FPS = 120; // TODO Make configurable
    private static final int FPS_WAIT_TICKS = 1000 / MAX_FPS;
    
    /** The Viewport to draw all game-elements on */
    public final Viewport Viewport;

    /** The {@code Scene}-library of this game */
    private final Map<String, Scene> scenes;
    /** The currently playing scene */
    private String current_scene;

    /** The list of registerted input-devices */
    private Map<Class<? extends InputDevice>, InputDevice> inputDevices;

    /**
     * Singleton. Private constructor!
     */
    private GameLoop(){
        isRunning = false;
        game_loop_executor = Executors.newSingleThreadExecutor();
        Viewport = new Viewport();
        scenes = new HashMap<String, Scene>();
        // Input devices:
        inputDevices = new HashMap<Class<? extends InputDevice>, InputDevice>();
        addInputDevice(new Mouse());
        addInputDevice(new Keyboard());
    }

    /**
     * The {@code Runnable} used for the {@code Executor}, executing
     * all defined methods of the registered Events.
     */
    private class GameRunnable implements Runnable {

        /** The ID of the current scene */
        private String scene_id;

        public GameRunnable(){
            startScene(getCurrentScene());
        }

        /**
         * <p>This method should be called to switch to another {@code Scene}.
         *  It will take care of all initial work.</p>
         * <p><b>Caution:</b> This method is for the internals of this class
         *  only!</p>
         * @param new_scene the new scene to switch to.
         */
        private void startScene(Scene new_scene){
            Viewport.loadFromScene(new_scene);
            if (new_scene.getSceneState() == Scene.State.PENDING)
                new_scene.onStartCall();
            new_scene.onResumeCall();
            // Update to the new scene:
            scene_id = current_scene;
        }

        private void updateGame(Scene scene){
            // Update the input devices:
            for (InputDevice device : inputDevices.values())
                device.update();
            // Check scheduled Callbacks:
            scene.checkCallbacks();
            // Update the game:
            if (!isFrozen()){
                // Collision-events:
                for (CollisionEvent event : scene.collisionEvents)
                    event.detectCollision(scene.game_field.getCollisionTest());
                // Movement-events:
                for (MovementEvent event : scene.movementEvents)
                    event.move(scene.getSceneTime());
            }
        }

        @Override
        public void run() {
            long next_update = System.currentTimeMillis();
            long last_update = System.currentTimeMillis();
            int frames_skipped;
            float interpolation;
            long sleep_time;

            try {
                // Initialize the input devices:
                for (InputDevice device : inputDevices.values())
                    device.initialize();
                // Start the loop:
                while (isRunning){
                    // Enforce Max FPS boundary:
                    sleep_time = (last_update + FPS_WAIT_TICKS) - System.currentTimeMillis();
                    if (sleep_time > 0) Thread.sleep(sleep_time);
                    last_update = System.currentTimeMillis();
                    // Update game:
                    frames_skipped = 0;
                    while (System.currentTimeMillis() > next_update
                            && frames_skipped < MAX_FRAMESKIP){
                        // Check if scene changed and pause it:
                        if (!scene_id.equals(current_scene)){
                            scenes.get(scene_id).onPauseCall();
                            // Start the new scene:
                            Scene new_scene = scenes.get(current_scene);
                            startScene(new_scene);
                        }
                        // Update events of current scene:
                        updateGame( scenes.get(scene_id) );
                        // Schedule next update:
                        next_update += WAIT_TICKS;
                        frames_skipped++;
                    }
                    // Repaint game!
                    // Calculate interpolation for smooth animation between states:
                    interpolation = ((float)(System.currentTimeMillis() + WAIT_TICKS - next_update)) / ((float)WAIT_TICKS);
                    // Render-events:
                    Viewport.canvas.redraw(interpolation);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            } finally {
                // Release all InputDevices:
                for (InputDevice device : inputDevices.values())
                    device.release();
            }
        }
    };

    /**
     * Add the {@code Runnable} for the main-loop, set it for schedule and
     *  begin executing it.
     */
    private void createMainLoop(){
        GameRunnable game_loop = new GameRunnable();
        // Start the new game executor:
        game_loop_executor.execute(game_loop);
    }

    /**
     * <p>Add a new {@link InputDevice} to the list of registered input-devices.</p>
     * <p>After the game has been started by the {@link #startLoop()}-method,
     *  this method will return without doing anything.</p>
     * @param device the new device to add.
     * @see #getInputDevice(Class)
     */
    public void addInputDevice(InputDevice device){
        if (device == null)
            throw new NullPointerException("device can't be null!");
        if (!isLocked())
            inputDevices.put(device.getClass(), device);
    }

    /**
     * <p>Get the input-device of the specified class, as it's already casted type.</p>
     * <p>An example of calling this method might be:</p>
     * <code>
     *     Mouse mouse = GameLoop.INSTANCE.getInputDevice(Mouse.class);
     * </code>
     *
     * @param device the input-device you desire.
     * @param <T> the type of the device you desire.
     * @return the device you supplied.
     * @throws NoSuchElementException if the specified device is not registered.
     */
    @SuppressWarnings("unchecked")
    public <T> T getInputDevice(Class<? extends InputDevice> device){
        if (device == null)
            throw new NullPointerException("device can't be null!");
        if (!inputDevices.containsKey(device))
            throw new NoSuchElementException("Can't find device of class: "+device.getClass().getName());
        return (T) inputDevices.get(device);
    }

    /**
     * <p>Switch to another (previously added) {@code Scene}.</p>
     * <p>Calling this method will cause the current scenes {@link org.knuth.mgf.Scene#onPause()}-
     *  and the new scenes {@link org.knuth.mgf.Scene#onResume()}-method to be called. If the
     *  new {@code Scene} has not yet been started, it's
     *  {@link Scene#onStart(org.knuth.mgf.Scene.SceneBuilder)}-method will be called first.</p>
     * @param scene_id the ID or Name of the scene to switch to.
     */
    public void switchScene(String scene_id){
        if (!scenes.containsKey(scene_id))
            throw new NoSuchElementException("The scene '"+scene_id+"' does not exist!");
        this.current_scene = scene_id;
    }

    /**
     * <p>This method will add a new {@code Scene} to the game. By default, the game
     *  will start with the scene that was added <b>last</b>.</p>
     * <p>After the game has been started by the {@link #startLoop()}-method,
     *  this method will return without doing anything.</p>
     * @param scene_id the ID or Name for the new {@code Scene}.
     * @param scene the new scene to add.
     */
    public void addScene(String scene_id, Scene scene){
        if (!isLocked()){
            scenes.put(scene_id, scene);
            current_scene = scene_id;
        }
    }

    /**
     * <p>Checks if the game-loop is already running. If so, the state of the
     *  events, added to their corresponding lists is considered "locked".</p>
     * <p>This is to prevent any writing-access to the list's, while another
     *  thread is using them, which would cause an
     *  {@code ConcurrentModificationException}</p>
     * @return whether if the main game-loop is currently running or not.
     */
    private boolean isLocked(){
        return isRunning;
    }

    /**
     * <p>Starts the game-loop with the last added {@code Scene}. This also shows
     *  the game-window on the screen.</p>
     * <p><b>Note:</b> After this method has been called, you can't add any new
     *  {@code Scene}s or {@code InputDevice}s.</p>
     */
    public void startLoop(){
        if (!isRunning){
            isRunning = true;
            createMainLoop();
            // Show the window:
            Viewport.setWindowVisibility(true);
        }
    }

    /**
     * <p>Gracefully stop the game-loop, allowing all pending operations
     *  to finish first. This also hides the game-window.</p>
     * <p>This will also cause all previously started scenes to be stopped.
     *  The {@link org.knuth.mgf.Scene#onStop()}-method will <b>not be called
     *  otherwise!</b></p>
     */
    public void stopLoop(){
        isRunning = false;
        // Stop the game-loop:
        try {
            game_loop_executor.shutdown();
            game_loop_executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (SecurityException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Stop all scenes if they're running:
        for (Scene scene : scenes.values())
            if (scene.getSceneState() != Scene.State.PENDING)
                scene.onStopCall();
        // Hide the game-window:
        Viewport.setWindowVisibility(false);
    }

    /**
     * Get the currently playing scene.
     * @throws java.lang.IllegalStateException if there is no scene currently playing or
     *  no scene added yet.
     */
    private Scene getCurrentScene(){
        if (!this.scenes.containsKey(current_scene)){
            throw new IllegalStateException("No scene is currently playing. There are" +
                    +this.scenes.size()+" Scenes available.");
        }
        return this.scenes.get(current_scene);
    }

    /**
     * <p>Schedules a callback to be executed in the given {@code wait_time} from
     *  now.</p>
     * <p>The time until the callback is triggered is counted as scene time and will
     *  be paused if the scene gets paused (by switching to another scene). It will
     *  however <b>not</b> be paused, when the scene is frozen using {@link #freeze()}.</p>
     * @param callback the callback to be executed once the time is up.
     * @param wait_time the time to wait until the {@code callback} should be executed.
     * @return a {@link org.knuth.mgf.ScheduledCallback} to control the now scheduled
     *  callback.
     */
    public ScheduledCallback scheduleCallback(Callback callback, TimeSpan wait_time){
        return getCurrentScene().scheduleCallback(callback, wait_time);
    }

    /**
     * <p>Re-schedules a previously scheduled and <u>either executed or canceled</u> callback
     *  (using {@link #scheduleCallback(Callback, TimeSpan)}).</p>
     * @param callback the callback that was previously scheduled.
     * @return the re-scheduled callback.
     * @throws java.lang.IllegalStateException if {@code callback} is currently already scheduled.
     * @see #scheduleCallback(Callback, TimeSpan)
     */
    public void rescheduleCallback(ScheduledCallback callback){
        getCurrentScene().rescheduleCallback(callback);
    }

    /**
     * <p>This method will un-freeze the current {@code Scene}.</p>
     * <p>Calling this method when the {@code Scene} was not frozen
     *  will not have any effect.</p>
     * @see GameLoop#freeze()
     */
    public void unfreeze(){
        getCurrentScene().unfreeze();
    }

    /**
     * <p>This method will freeze the current {@code Scene}. This will result in
     *  {@link CollisionEvent}s and {@link MovementEvent}s not being called as long
     *  as the {@code Scene} is frozen, but the game is still rendered.</p>
     * <p>Use the {@code unfreeze()}-method to un-freeze the {@code Scene}. A frozen
     *  {@code Scene} <b>will stay frozen, even if the {@code Scene} is changed</b>.
     *  It can only be unfrozen with the {@code unfreeze()}-method!</p>
     * @see GameLoop#unfreeze()
     */
    public void freeze(){
        getCurrentScene().freeze();
    }

    /**
     * Checks whether the current {@code Scene} is currently frozen or not.
     */
    public boolean isFrozen(){
        return getCurrentScene().isFrozen();
    }

}