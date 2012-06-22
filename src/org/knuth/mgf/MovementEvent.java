package org.knuth.mgf;

/**
 * Describes an object which will move.</p>
 * When the game is paused/frozen, this event will not be called.
 * @author Lukas Knuth
 * @version 1.0
 */
public interface MovementEvent {

    /**
     * Called when the AI is to think about what step to do
     *  next.
     */
    public void move();
}
