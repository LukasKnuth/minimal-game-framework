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
     * <p>This method is used to draw the current state of this object
     *  on the given {@code Graphics2D}-object.</p>
     * <p>For smoother animations, use the provided interpolation-value.</p>
     * @param g the object to draw on.
     * @param interpolation the interpolation for drawing.
     */
    public void render(Graphics2D g, float interpolation);
    // TODO Add tutorial for interpolation usage. See http://www.koonsolo.com/news/dewitters-gameloop/

}
