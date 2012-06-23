package graphic;

import org.knuth.mgf.GameLoop;

import javax.swing.*;
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
        JFrame frame = new JFrame("Testing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(GameLoop.INSTANCE.getView());
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

        frame.setSize(100, 100);
        frame.setVisible(true);
    }
}
