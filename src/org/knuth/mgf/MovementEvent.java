package org.knuth.mgf;

/**
 * <p>Describes an object which will move around on the screen.</p>
 * <p>When the game is paused or frozen, this event will not be called.</p>
 * @author Lukas Knuth
 * @version 1.0
 */
public interface MovementEvent {

    /**
     * Called by the {@code GameLoop} to indicate that this object should now
     *  move.
     * @param total_game_time the total time which has elapsed since the
     *  game was first started. This can be used to implement time-based
     *  movements and events.
     * <p>The span does <b>not</b> include the time when the game was paused/
     *  frozen!</p>
     */
    public void move(TimeSpan total_game_time);
}
