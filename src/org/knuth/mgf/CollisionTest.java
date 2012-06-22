package org.knuth.mgf;

/**
 * This object will check if a given {@code Point} has collided with an object
 *  on the current {@code Map}.</p>
 * @author Lukas Knuth
 * @version 1.0
 */
public interface CollisionTest {

    /**
     * This method will check if the object {@code you}, at it's given
     *  coordinates, has collided with <u>any kind</u> of object on the
     *  current {@code Map}-instance.</p>
     * If you need to know the kind of object it collided with too, use
     *  the {@code checkCollusion()}-method!
     * @param you_x the X-position of the figure, checking for collusion.
     * @param you_y the Y-position of the figure, checking for collusion.
     * @return whether if there was a collusion or not.
     * @see CollisionTest#checkCollusion(int, int, Object)
     */
    public boolean checkAnyCollusion(int you_x, int you_y);

    /**
     * This method will check if the object {@code you}, at it's given
     *  coordinates, has collided with <u>the given type</u> of object
     *  on the current {@code Map}-instance.</p>
     * If you just want to know if there was any kind of collusion, you
     *  can also use the {@code checkAnyCollusion()}-method.
     * @param you_x the X-position of the figure, checking for collusion.
     * @param you_y the Y-position of the figure, checking for collusion.
     * @param object the object to to check for collusion with
     *           {@code you}.
     * @param <T> the type of object you want to check for collusion with
     *           {@code you}.
     * @return whether there was a collusion with the given object-type
     *  or not.
     */
    public <T> boolean checkCollusion(int you_x, int you_y, T object);

    /**
     * Possible directions for the {@code checkNextCollusion()}-method.
     * @see CollisionTest#checkNextCollusion(int, int, Object, CollisionTest.NextDirection)
     */
    public enum NextDirection{
        UP, RIGHT, DOWN, LEFT;

        /**
         * This will return the opposite direction of this direction.
         * @return the opposite direction.
         */
        public NextDirection opposite(){
            switch (this){
                case DOWN:
                    return UP;
                case RIGHT:
                    return LEFT;
                case LEFT:
                    return RIGHT;
                case UP:
                    return DOWN;
                default:
                    throw new IllegalStateException("No opposite of "+this+" available");
            }
        }
    }

    /**
     * Will check for a collusion with a specified type of object on the next
     *  possible collusion-event on the current {@code Map}-instance.
     * @param you_x the X-position of the figure, checking for collusion.
     * @param you_y the Y-position of the figure, checking for collusion.
     * @param object the object to to check for collusion with
     *           {@code you}.
     * @param next the direction to which the collusion should be checked.
     * @param <T> the type of object you want to check for collusion with
     *           {@code you}.
     * @return whether there was a collusion with the given object-type
     *  or not.
     */
    public <T> boolean checkNextCollusion(int you_x, int you_y, T object, NextDirection next);

}
