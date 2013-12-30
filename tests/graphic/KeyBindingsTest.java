package graphic;

import org.knuth.mgf.GameLoop;
import org.knuth.mgf.RenderEvent;
import org.knuth.mgf.Scene;

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
        GameLoop.INSTANCE.Viewport.setTitle("Key Bindings Test");
        GameLoop.INSTANCE.Viewport.setSize(200, 200);
        GameLoop.INSTANCE.addScene("main", new Scene(){
            @Override
            public void onStart(SceneBuilder builder){
                builder.addRenderEvent(new RenderEvent() {
                    @Override
                    public void render(Graphics2D g) {
                        g.setColor(Color.GREEN);
                        g.drawRect(20, 20, 80, 80);
                        g.setColor(Color.RED);
                        g.drawRect(300, 20, 80, 80);
                    }
                }, 0);

                builder.putKeyBinding(KeyEvent.VK_E, 0, false, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Pressed E");
                    }
                });
                builder.putKeyBinding(KeyEvent.VK_E, 0, true, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Released E");
                    }
                });

                builder.putKeyBinding(KeyEvent.VK_SPACE, 0, false, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Space!");
                        GameLoop.INSTANCE.Viewport.setSize(400, 200);
                    }
                });
            }
        });
        GameLoop.INSTANCE.Viewport.setBackground(Color.BLACK);

        GameLoop.INSTANCE.startLoop();
    }
}
