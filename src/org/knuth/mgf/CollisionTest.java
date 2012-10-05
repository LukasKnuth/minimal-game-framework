package org.knuth.mgf;

/**
 * <p>This object is used to check if a point on the given X|Y coordinate has
 *  collided with an object on the current {@link Map}.</p>
 * @author Lukas Knuth
 * @version 1.0
 */
public interface CollisionTest {

    /**
     * <p>This method will check if the point ({@code you_x,}|{@code you_y}),
     *  has collided with <u>any kind</u> of object on the current {@link Map}
     *  -instance.</p>
     * <p>If you need to know the kind of object it collided with, too, use
     *  the {@link #checkCollision}-method!</p>
     * @param you_x the X-position of the figure, checking for collision.
     * @param you_y the Y-position of the figure, checking for collision.
     * @return whether there was a collision or not.
     * @see CollisionTest#checkCollision(int, int, Object)
     */
    public boolean checkAnyCollision(int you_x, int you_y);

    /**
     * <p>This method will check if the point ({@code you_x,}|{@code you_y}),
     *  has collided with <u>the given type</u> of object on the current
     *  {@link Map}-instance.</p>
     * <p>If you just want to know if there was any kind of collision, you
     *  can also use the {@link #checkAnyCollision}-method.</p>
     * @param you_x the X-position of the figure, checking for collision.
     * @param you_y the Y-position of the figure, checking for collision.
     * @param object the object to to check for collision with
     *           ({@code you_x,}|{@code you_y}).
     * @param <T> the type of object you want to check for collision with
     *           ({@code you_x,}|{@code you_y}).
     * @return whether there was a collision with the given object-type
     *  or not.
     * @see #checkAnyCollision(int, int)
     */
    public <T> boolean checkCollision(int you_x, int you_y, T object);

    /**
     * Possible directions for the {@code checkNextCollision()}-method.
     * @see CollisionTest#checkNextCollision
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
     * Will check for a collision with a specified type of object on the next
     *  possible {@link CollisionEvent} on the current {@link Map}-instance.
     * @param you_x the X-position of the figure, checking for collision.
     * @param you_y the Y-position of the figure, checking for collision.
     * @param object the object to to check for collision with
     *           ({@code you_x,}|{@code you_y}).
     * @param next the direction to which the collision should be checked.
     * @param <T> the type of object you want to check for collision with
     *           ({@code you_x,}|{@code you_y}).
     * @return whether there was a collision with the given object-type
     *  or not.
     */
    public <T> boolean checkNextCollision(int you_x, int you_y, T object, NextDirection next);

}
