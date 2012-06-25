package org.knuth.mgf;

/**
 * Describes an object which will move.</p>
 * When the game is paused/frozen, this event will not be called.
 * @author Lukas Knuth
 * @version 1.0
 */
public interface MovementEvent {

    /**
     * Called when the movable object should move.
     * @param total_game_time the total time which has elapsed since the
     *  game was first started.</p>
     * The span does <b>not</b> include the time when the game was paused/
     *  frozen!
     */
    public void move(TimeSpan total_game_time);
}
