package graphic;

import org.knuth.mgf.CollisionTest;
import org.knuth.mgf.GameLoop;
import org.knuth.mgf.Map;
import org.knuth.mgf.RenderEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Testing the Key-Bindings and other, graphic related stuff.</p>
 * This is not a Unit-Test!</p>
 * Note that there is a Linux Bug which causes the underlying system to trigger
 *  multiple "KeyReleased" actions when holding down a key. For more information,
 *  see the <a href="http://bugs.sun.com/view_bug.do?bug_id=4153069">Bug Report</a>
 * @author Lukas Knuth
 * @version 1.0
 */
public class KeyBindingsTest {

    public static void main(String[] args){
        final JFrame frame = new JFrame("Testing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(GameLoop.INSTANCE.Viewport.getView());
        GameLoop.INSTANCE.putKeyBinding(KeyEvent.VK_E, 0, false, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pressed E");
            }
        });
        GameLoop.INSTANCE.putKeyBinding(KeyEvent.VK_E, 0, true, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Released E");
            }
        });

        GameLoop.INSTANCE.putKeyBinding(KeyEvent.VK_SPACE, 0, false, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Space!");
                GameLoop.INSTANCE.Viewport.setSize(400, 200);
                frame.pack();
            }
        });

        GameLoop.INSTANCE.Viewport.setSize(200, 200);
        GameLoop.INSTANCE.addRenderEvent(new RenderEvent() {
            @Override
            public void render(Graphics g) {
                g.setColor(Color.GREEN);
                g.drawRect(20, 20, 80, 80);
                g.setColor(Color.RED);
                g.drawRect(300, 20, 80, 80);
            }
        }, 0);
        frame.pack();
        GameLoop.INSTANCE.Viewport.setBackground(Color.BLACK);

        GameLoop.INSTANCE.setMap(new Map() {
            @Override
            public CollisionTest getCollusionTest() {
                return null;
            }
        });
        GameLoop.INSTANCE.startLoop();
        frame.setVisible(true);
    }
}
