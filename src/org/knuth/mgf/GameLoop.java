package org.knuth.mgf;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The main game-loop, calling all registered events.</p>
 * Use the given methods to add your events to the game-loop. The
 *  loop will try to keep the frame-rate at 60FPS, if possible.</p>
 *
 * The order in which the event-types are called is given in the
 *  following list:
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
    /** Weather if the game is currently frozen */
    private boolean isFrozen;
    /** Weather the game is currently paused */
    private boolean isPaused;

    /** The time-stamp (in microseconds) when the game-loop was started */
    private long start_stamp;
    /** The time-stamp of the moment the game was last paused/frozen */
    private long pause_stamp;
    /** The combined amount of time (in microseconds) that the game was paused/frozen */
    private long excluded_time;

    /** The executor-service running the main game-loop */
    private ScheduledExecutorService game_loop_executor;
    /** The handler fot the main-game-thread, used to stop it */
    private ScheduledFuture game_loop_handler;
    
    /** The Viewport to draw all game-elements on */
    public final Viewport Viewport;

    /** All registered {@code MovementEvent}s */
    private List<MovementEvent> movementEvents;
    /** All registered {@code RenderEvent}s */
    private List<RenderContainer> renderEvents;
    /** All registered {@code CollisionEvent}s */
    private List<CollisionEvent> collisionEvents;
    
    /** The {@code Map} the game takes place on */
    private Map game_field;

    /**
     * Singleton. Private constructor!
     */
    private GameLoop(){
        movementEvents = new ArrayList<MovementEvent>();
        renderEvents = new ArrayList<RenderContainer>();
        collisionEvents = new ArrayList<CollisionEvent>();
        isRunning = false;
        isFrozen = false;
        isPaused = false;
        game_loop_executor = Executors.newSingleThreadScheduledExecutor();
        Viewport = new Viewport();
    }

    /**
     * The {@code Runnable} used for the {@code Executor}, executing
     * all defined methods of the registered Events.
     */
    private Runnable game_loop = new Runnable() {
        @Override
        public void run() {
            try {
                if (!isFrozen() && !isPaused()){
                    // Collusion-events:
                    for (CollisionEvent event : collisionEvents)
                        event.detectCollusion(game_field.getCollusionTest());
                    // Calculate the current game-time:
                    TimeSpan total_game_time = new TimeSpan(
                            System.nanoTime() - start_stamp - excluded_time
                    );
                    // Movement-events:
                    for (MovementEvent event : movementEvents)
                        event.move(total_game_time);
                }
                // Render-events:
                Viewport.canvas.repaint();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    };

    /**
     * Add the {@code Runnable} for the main-loop, set it for schedule and
     *  begin executing it.
     */
    private void createMainLoop(){
        // Check if we have a Map:
        if (this.game_field == null)
            throw new IllegalStateException("The game can't start without a Map!");
        // Give the Canvas all Elements to paint:
        Viewport.setRenderEvents(this.renderEvents);
        // Start the new game executor:
        game_loop_handler = game_loop_executor.scheduleAtFixedRate(
                game_loop, 0L, 16L, TimeUnit.MILLISECONDS
        );
    }

    /**
     * Checks if the game-loop is already running. If so, the state of the
     *  events, added to their corresponding lists is considered "locked".
     * </p>
     * This is to prevent any writing-access to the list's, while another
     *  thread is using them, which would cause an
     *  {@code ConcurrentModificationException}
     * @return whether if the main game-loop is currently running or not.
     */
    private boolean isLocked(){
        return isRunning;
    }

    /**
     * Binds a given key-code with the specified modifiers to a given {@link Action}.</p>
     * Possible key-codes can be retrieved from the {@link java.awt.event.KeyEvent}-class.</p>
     * To remove a key-binding, give {@code null} as the action.
     * @param key_code the key-code to bind the action to. See the {@link java.awt.event.KeyEvent}-class.
     * @param modifiers a combination of possible modifiers or {@code 0} for no modifiers.
     *  See {@link KeyStroke#getKeyStroke(int, int)}
     * @param released {@code true} if the action is bind to a key-release event,
     *  {@code false} otherwise (key-press event).
     * @param action the {@link Action} to be executed when the given key was pressed. If
     *  value is {@code null}, the key-binding will be removed.
     */
    public void putKeyBinding(int key_code, int modifiers, boolean released, Action action){
        Viewport.canvas.putKeyBinding(key_code, modifiers, released, action);
    }

    /**
     * Add a new {@code MovementEvent} to the schedule.</p>
     * This method <u>will not have any effect</u>, after the {@code startLoop()}-
     *  method has already been called!
     * @param event the new element to add.
     */
    public void addMovementEvent(MovementEvent event){
        // Check if locked:
        if (!isLocked())
            this.movementEvents.add(event);
    }

    /**
     * Add a new {@code RenderEvent} to the schedule.
     * This method <u>will not have any effect</u>, after the {@code startLoop()}-
     *  method has already been called!
     * @param event the new element to add.
     * @param zIndex the z-index this element should be drawn at.
     */
    public void addRenderEvent(RenderEvent event, int zIndex){
        // Check if locked:
        if (!isLocked()){
            RenderContainer re = new RenderContainer(zIndex,event);
            this.renderEvents.add(re);
        }
    }

    /**
     * Add a new {@code CollisionEvent} to the schedule.
     * This method <u>will not have any effect</u>, after the {@code startLoop()}-
     *  method has already been called!
     * @param event the new element to add.
     */
    public void addCollisionEvent(CollisionEvent event){
        if (!isLocked())
            this.collisionEvents.add(event);
    }

    /**
     * Set the {@code Map}, on which the game is played.
     * @param map the map to use.
     * @see Map
     */
    public void setMap(Map map){
        if (!isLocked())
            this.game_field = map;
    }

    /**
     * Start the game-loop.
     */
    public void startLoop(){
        if (!isRunning){
            createMainLoop();
            isRunning = true;
            // Store the current time:
            start_stamp = System.nanoTime();
        }
    }

    /**
     * Gracefully stop the game-loop, allowing all pending operations
     *  to finish first.
     */
    public void stopLoop(){
        game_loop_handler.cancel(true);
        try {
            game_loop_executor.shutdown();
        } catch (SecurityException e){
            e.printStackTrace();
        }
        isRunning = false;
    }

    /**
     * This method will un-pause or un-freeze the game.</p>
     * Calling this method when the game was not paused/frozen
     *  will not have any effect.
     * @see GameLoop#pause()
     * @see GameLoop#freeze()
     */
    public void play(){
        this.isFrozen = false;
        this.isPaused = false;
        // Add the paused time to the excluded time:
        excluded_time += System.nanoTime() - pause_stamp;
    }

    /**
     * This method will cause the game to freeze.</p>
     * Calling this method will result in all characters not moving
     *  anymore, still painting the game normally.</p>
     * This method will not print any "pause"-message on screen and
     *  should only be used to literally freeze the game.</p>
     * Use the {@code play()}-method to un-freeze the game.
     * @see GameLoop#pause()
     * @see GameLoop#play()
     */
    public void freeze(){
        this.isFrozen = true;
        pause_stamp = System.nanoTime();
    }

    /**
     * This method is used to pause the Game.</p>
     * This will cause the game-characters (player character and AI
     *  characters) to not move anymore, but the game will continue
     *  to be painted. Also, pausing the game will show up a "paused"
     *  message on-screen.</p>
     * Use the {@code play()}-method to un-pause the game.
     * @see GameLoop#freeze()
     * @see GameLoop#play()
     */
    public void pause(){
        this.isPaused = true;
        pause_stamp = System.nanoTime();
    }

    /**
     * Weather the game is currently paused or not.
     * @return weather the game is currently paused.
     */
    public boolean isPaused(){
        return this.isPaused;
    }

    /**
     * Weather the game is currently frozen or not.
     * @return weather the game is currently frozen.
     */
    public boolean isFrozen(){
        return this.isFrozen;
    }

}