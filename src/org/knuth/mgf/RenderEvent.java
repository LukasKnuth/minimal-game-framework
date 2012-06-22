package org.knuth.mgf;

import java.awt.*;

/**
 * Describes an object that can be rendered.
 * @author Lukas Knuth
 * @version 1.0
 */
public interface RenderEvent {


    /**
     * This method is used to draw the current state on the
     *  given {@code Graphics}-object.
     * @param g the object to draw an.
     * @return the drawn object.
     */
    public void render(Graphics g);


}
