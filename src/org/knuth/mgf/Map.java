package org.knuth.mgf;

/**
 * This interface is used to represent a Map, on which the Game takes
 *  place.</p>
 * The map is also responsible for offering a mechanism which allows to
 *  check for collusion's with Objects on this Map. This normally excludes
 *  other players/figures!
 * @author Lukas Knuth
 * @version 1.0
 */
public interface Map {

    /**
     * This method will return an {@code CollisionTest}-object, which can
     *  then be used to check for a collusion with an object on this
     *  {@code Map}-instance.
     * @return the object to run the collusion-tests on.
     */
    public CollisionTest getCollusionTest();
}
