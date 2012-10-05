package org.knuth.mgf;

import java.awt.*;

/**
 * <p>Describes an object which should be drawn on the screen.</p>
 * <p>When the game is frozen, this event will not be called. If it is
 *  "only" paused, this event will still be called.</p>
 * @author Lukas Knuth
 * @version 1.0
 */
public interface RenderEvent {

    /**
     * This method is used to draw the current state of this object
     *  on the given {@code Graphics}-object.
     * @param g the object to draw on.
     */
    public void render(Graphics g);

}
