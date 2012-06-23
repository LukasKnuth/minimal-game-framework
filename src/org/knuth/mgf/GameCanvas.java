package org.knuth.mgf;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * The canvas all actions on the game-filed are drawn on.</p>
 * This canvas already implements buffered drawing against
 *  fading's when multiple re-draws take place.
 * @author Lukas Knuth
 * @author Fabian Bottler
 * @version 1.0
 */
class GameCanvas extends JPanel{

    /** The off-screen image */
    private Image dbImage;
    /** The buffered graphics object to draw on */
    private Graphics dbg;
    /** All rendered elements */
    private List<RenderContainer> renderEvents;

    /**
     * Package-private constructor. Only to be initialized
     *  by {@code GameLoop}.
     */
    GameCanvas(){
        dbg = this.getGraphics();
    }

    /**
     * This method is implemented here, but it's user-documentation might
     *  be found at it's wrapper in the {@link GameLoop}-class.
     * @see GameLoop#putKeyBinding(int, int, boolean, javax.swing.Action)
     */
    void putKeyBinding(int key_code, int modifiers, boolean released, Action action){
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

    /**
     * Sets the {@code RenderEvent}s (packed in a {@code RenderContainer}
     *  with their desired Z-index), which should be rendered on this
     *  Canvas.
     * @param renderEvents the events to be painted on this canvas.
     */
    void setRenderEvents(List<RenderContainer> renderEvents){
        this.renderEvents = renderEvents;
        Collections.sort(this.renderEvents);
    }

    @Override
    public void paint( Graphics g )
    {
        if (renderEvents == null || renderEvents.size() < 1) return;
        for (RenderContainer event : renderEvents){
            event.getEvent().render(g);
        }
    }

    @Override
    public void update(Graphics g){
        // Double Buffer
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);
        dbg.setColor(getForeground());
        paint(dbg);
        g.drawImage(dbImage, 0, 0, this);
    }


}
