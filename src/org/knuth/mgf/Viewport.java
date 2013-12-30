package org.knuth.mgf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;

/**
 * <p>The Viewport bundles methods to manipulate the drawing-space of the game.
 *  It also allows you to perform simple actions on the game-canvas and offers
 *  access to the underlying {@code JComponent}.</p>
 *
 * <p>The underlying drawing takes place on a Swing component, which can be
 *  retrieved with the {@link #getView()}-method. Any other methods,
 *  exposed by the {@code JComponent}-class might be used on the returned object,
 *  although it's <b>not advised</b>. The {@code Viewport}-class gives convenient
 *  access to the most recently used methods.</p>
 *
 * <p>To add the canvas to your games Swing UI, use the {@link #getView()}-method
 *  and add the returned {@code JComponent}-object to the UI.</p>
 *
 * <p><b>Attention:</b> Don't manually call {@code repaint()}! The {@code GameLoop}
 *  will take care of that!</p>
 * @author Lukas Knuth
 * @version 1.1
 */
public class Viewport {

    /** All rendered elements */
    private List<RenderContainer> renderEvents;

    /** The actual canvas to draw on. */
    final GameCanvas canvas;
    private final GameWindow window;

    /**
     * Package-private constructor. Only to be initialized
     *  by {@code GameLoop}.
     */
    Viewport(){
        this.canvas = new GameCanvas();
        this.window = new GameWindow(canvas);
    }

    /**
     * Set the background-color for the Viewport.
     * @param color the background color.
     */
    public void setBackground(Color color){
        canvas.setBackground(color);
    }

    /**
     * Get the viewport's X-coordinate on the whole screen.
     * @return the X-coordinate of the viewport.
     */
    public int getX(){
        return (int)canvas.getLocationOnScreen().getX();
    }

    /**
     * Get the viewport's Y-coordinate on the whole screen.
     * @return the Y-coordinate of the viewport.
     */
    public int getY(){
        return (int)canvas.getLocationOnScreen().getY();
    }

    /**
     * Set the size of the Viewport.
     * @param width the new width in pixels.
     * @param height the new height in pixels.
     */
    public void setSize(int width, int height){
        canvas.setSize(width, height);
        window.pack();
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
     * Return the games window in which the Viewport is embedded.
     * @return the window.
     */
    public JFrame getWindow(){
        return this.window;
    }

    /**
     * Set the visibility of the game's window.
     */
    public void setWindowVisibility(boolean visible){
        this.window.setVisible(visible);
    }

    /**
     * Set the title to be display on the game's window.
     */
    public void setTitle(String title){
        this.window.setTitle(title);
    }

    /**
     * Loads the necessary objects from the given {@code Scene} to work
     *  with them.
     * @param scene the scene to load from.
     */
    void loadFromScene(Scene scene){
        // Load the RenderEvents:
        this.renderEvents = scene.renderEvents;
        Collections.sort(this.renderEvents);
        // Load the key-bindings:
        canvas.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, scene.inputMap);
        canvas.setActionMap(scene.actionMap);
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

    class GameWindow extends JFrame{

        /**
         * Create but <b>don't</b> show a window to play the game in.
         * @param gc the canvas to draw the game on.
         */
        public GameWindow(GameCanvas gc){
            // Set some default options:
            this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            this.add(gc);
            this.pack();
            // Close-callback to stop the game-loop.
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    GameLoop.INSTANCE.stopLoop();
                    GameWindow.this.dispose();
                    System.exit(0);
                }
            });
        }
    }

}
