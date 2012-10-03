package org.knuth.mgf;

import org.knuth.mgf.input.InputDevice;
import org.knuth.mgf.input.Mouse;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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
        isFrozen = false;
        isPaused = false;
        game_loop_executor = Executors.newSingleThreadScheduledExecutor();
        Viewport = new Viewport();
        scenes = new HashMap<String, Scene>();
        // Input devices:
        inputDevices = new HashMap<Class<? extends InputDevice>, InputDevice>();
        addInputDevice(new Mouse());
    }

    /**
     * The {@code Runnable} used for the {@code Executor}, executing
     * all defined methods of the registered Events.
     */
    private class GameRunnable implements Runnable {

        private String scene_id;

        public GameRunnable(){
            startScene(scenes.get(current_scene));
        }

        private void startScene(Scene new_scene){
            Viewport.loadFromScene(new_scene);
            if (new_scene.current_state == Scene.State.PENDING)
                new_scene.onStartCall();
            new_scene.onResumeCall();
            // Update to the new scene:
            scene_id = current_scene;
        }

        @Override
        public void run() {
            try {
                // Check if scene changed and pause it:
                if (!scene_id.equals(current_scene)){
                    scenes.get(scene_id).onPauseCall();
                    // Start the new scene:
                    Scene new_scene = scenes.get(current_scene);
                    startScene(new_scene);
                }
                // Get the current scene (ensure all events):
                if (!isFrozen() && !isPaused()){
                    // Update the input devices:
                    for (InputDevice device : inputDevices.values())
                            device.update();
                    // Do events:
                    Scene scene = scenes.get(scene_id);
                    // Collusion-events:
                    for (CollisionEvent event : scene.collisionEvents)
                        event.detectCollusion(scene.game_field.getCollusionTest());
                    // Calculate the current game-time:
                    TimeSpan total_game_time = new TimeSpan(
                            System.nanoTime() - start_stamp - excluded_time
                    );
                    // Movement-events:
                    for (MovementEvent event : scene.movementEvents)
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
        GameRunnable game_loop = new GameRunnable();
        // Start the new game executor:
        game_loop_handler = game_loop_executor.scheduleAtFixedRate(
                game_loop, 0L, 16L, TimeUnit.MILLISECONDS
        );
    }

    /**
     * <p>Add a new {@link InputDevice} to the list of registered input-devices.</p>
     * @param device the new device to add.
     * @see #getInputDevice(Class)
     */
    public void addInputDevice(InputDevice device){
        if (device == null)
            throw new NullPointerException("device can't be null!");
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
     * <p>Calling this method will cause the game to switch to another {@code Scene}.</p>
     * <p>When switching scenes, it is guaranteed that the live-cycle of the last scene
     *  will be completely executed.</p>
     * @param scene_id the ID or Name of the scene to switch to.
     */
    public void switchScene(String scene_id){
        if (!scenes.containsKey(scene_id))
            throw new NoSuchElementException("The scene '"+scene_id+"' does not exist!");
        this.current_scene = scene_id;
    }

    /**
     * <p>This method will add a new {@code Scene} to the game. By default, the game
     *  will start with the scene that was added last.</p>
     * <p>After the game has been started by the {@code startLoop()}-method,
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
     * <p>Gracefully stop the game-loop, allowing all pending operations
     *  to finish first.</p>
     * <p>This will also cause all previously started scenes to be stopped.
     *  The {@link org.knuth.mgf.Scene#onStop()}-method will not be called
     *  otherwise!</p>
     */
    public void stopLoop(){
        // Stop the game-loop:
        game_loop_handler.cancel(true);
        try {
            game_loop_executor.shutdown();
        } catch (SecurityException e){
            e.printStackTrace();
        }
        // Stop all scenes if they're running:
        for (Scene scene : scenes.values())
            if (scene.current_state != Scene.State.PENDING)
                scene.onStopCall();
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