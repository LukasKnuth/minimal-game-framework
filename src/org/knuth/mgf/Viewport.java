package org.knuth.mgf;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * This class is the Viewport of the game, the place where everything is drawn
 *  on.</p>
 * The class offers basic functionality which you'll most likely want to use
 *  at some point when creating your game. The underlying drawing takes place
 *  on a Swing component, which can be retrieved with the {@link #getView()}-
 *  method. Any other methods, exposed by the {@code JComponent}-class might
 *  be used on the returned object, although it's not advised.</p>
 *
 * To add the drawable Viewport to your games Swing UI, use the
 *  {@link #getView()}-method and add the returned {@code JComponent}-object
 *  to the UI.</p>
 *
 * <b>Attention:</b> Don't manually call {@code repaint()}! The {@code GameLoop}
 *  will take care of that!
 * @author Lukas Knuth
 * @version 1.1
 */
public class Viewport {

    /** All rendered elements */
    private List<RenderContainer> renderEvents;

    /** The accual canvas to draw on. */
    final GameCanvas canvas;

    /**
     * Package-private constructor. Only to be initialized
     *  by {@code GameLoop}.
     */
    Viewport(){
        canvas = new GameCanvas();
    }

    /**
     * Set the background-color for the Viewport.
     * @param color the background color.
     */
    public void setBackground(Color color){
        canvas.setBackground(color);
    }

    /**
     * Set the size of the Viewport.
     * @param width the new width in pixels.
     * @param height the new height in pixels.
     */
    public void setSize(int width, int height){
        canvas.setSize(width, height);
    }

    /**
     * Set the size of the Viewport.
     * @param dimension the new width and height, bot in pixels.
     */
    public void setSize(Dimension dimension){
        Viewport.this.setSize(dimension.width, dimension.height);
    }

    /**
     * Get the view which holds the drawn state of the game.
     * @return the view of the Game.
     */
    public JComponent getView(){
        return canvas;
    }

    /**
     * Sets the {@code RenderEvent}s (packed in a {@code RenderContainer}
     *  with their desired Z-index), which should be rendered on this
     *  Canvas.
     * @param renderEvents the events to be painted on this Viewport.
     */
    void setRenderEvents(List<RenderContainer> renderEvents){
        this.renderEvents = renderEvents;
        Collections.sort(this.renderEvents);
    }

    /*
       ####################################################################
     */

    /**
     * The actual canvas on which the game is drawn. The most common methods
     *  have a proxy in the outer {@code Viewport}-class.
     */
    class GameCanvas extends JPanel{

        /** The dimensions of the cavas */
        private Dimension dimension;

        /**
         * Create a new canvas to draw the game on.
         */
        private GameCanvas(){
            this.setDoubleBuffered(true);
            // Set a default Viewport size
            dimension = new Dimension(600, 400);
        }

        /**
         * This method is implemented here, but it's user-documentation might
         *  be found at it's wrapper in the {@link GameLoop}-class.
         * @see GameLoop#putKeyBinding(int, int, boolean, javax.swing.Action)
         */
        public void putKeyBinding(int key_code, int modifiers, boolean released, Action action){
            KeyStroke stroke = KeyStroke.getKeyStroke(key_code, modifiers, released);
            String action_name = ""+key_code+released+modifiers;
            // Check if add or remove:
            if (action == null){
                // Remove binding:
                this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(stroke);
                this.getActionMap().remove(action_name);
            } else {
                // Add binding
                this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, action_name);
                this.getActionMap().put(action_name, action);
            }
        }

        @Override
        public void paintComponent(Graphics g){
            if (renderEvents == null || renderEvents.size() < 1) return;
            // Start painting:
            super.paintComponent(g);
            for (RenderContainer event : renderEvents){
                event.getEvent().render(g);
            }
        }

        @Override
        public void setSize(int width, int height){
            /*
               We only need to override this method (not setSize(Dimension)),
               because it is internally called by the former one.
            */
            super.setSize(width, height);
            dimension = new Dimension(width, height);
        }

        @Override
        public Dimension getPreferredSize(){
            return dimension;
        }

        @Override
        public Dimension getMaximumSize(){
            return dimension;
        }

        @Override
        public Dimension getMinimumSize(){
            return dimension;
        }

    }

}
