package org.knuth.mgf;

/**
 * This event will be triggered by the {@code GameLoop}, to check any kind of
 *  collusion with another object on the {@code Map}.</p>
 * As of the current implementation-state, this class will only be used to
 *  check for collusion's with objects on a {@code Map}, not for collusion's
 *  with another player/figure!
 * @author Lukas Knuth
 * @version 1.0
 */
public interface CollisionEvent {

    /**
     * This method will be triggered by the {@code GameLoop} to check if there
     *  was a collusion with an object on the current {@code Map}.
     * @param tester the object to run the collusion-tests on.
     */
    public void detectCollusion(CollisionTest tester);
}
