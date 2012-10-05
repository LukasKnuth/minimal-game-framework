package org.knuth.mgf;

/**
 * <p>This event will be triggered by the {@link GameLoop}, to check for any
 *  kind of collision with another object on the current {@link Map}.</p>
 * <p>When the game is paused or frozen, this event will not be called.</p>
 * @author Lukas Knuth
 * @version 1.0
 */
public interface CollisionEvent {

    /**
     * This method will be called by the {@link GameLoop} to check if there
     *  was a collision with an object on the current {@link Map}.
     * @param tester the object to run the collision-tests on.
     */
    public void detectCollision(CollisionTest tester);
}
