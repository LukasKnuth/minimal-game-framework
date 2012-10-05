package org.knuth.mgf;

/**
 * <p>This interface is used to represent a Map, on which the Game takes
 *  place.</p>
 * <p>The map is also responsible for offering a mechanism which allows to
 *  check for collision's with Objects on this Map. This might include other
 *  players/figures, too, but does not need to.</p>
 * @author Lukas Knuth
 * @version 1.0
 */
public interface Map {

    /**
     * <p>This method should return an implementation of the
     *  {@link CollisionTest}-interface, which can then be used to check
     *  for a collision with an object on this {@code Map}-instance.</p>
     * @return the {@code CollisionTest}-implementation that checks for
     *  collisions on this {@code Map}.
     */
    public CollisionTest getCollisionTest();
}
