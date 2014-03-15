package system;

import org.knuth.mgf.GameLoop;
import org.knuth.mgf.RenderEvent;
import org.knuth.mgf.Scene;
import org.knuth.mgf.input.Keyboard;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * A simple implementation example on how the {@link Keyboard}-class can be used
 *  for input purposes.
 *
 * @author Lukas Knuth
 * @version 1.0
 */
public class KeyboardInputTest {

    public KeyboardInputTest(){
        GameLoop.INSTANCE.Viewport.setTitle("Keyboard Input Test");
        GameLoop.INSTANCE.addScene("main", new KeyboardScene());
        GameLoop.INSTANCE.startLoop();
    }

    private class KeyboardScene extends Scene {

        @Override
        public void onStart(SceneBuilder builder){
            GameLoop.INSTANCE.Viewport.setBackground(Color.BLACK);
            builder.addRenderEvent(new RenderEvent() {

                @Override
                public void render(Graphics2D g, float interpolation) {
                    Keyboard keyboard = GameLoop.INSTANCE.getInputDevice(Keyboard.class);
                    g.setColor(Color.WHITE);
                    g.drawString("Pressed keys: ", 10, 20);
                    g.drawLine(10, 22, 100, 22);
                    int y = 40;
                    for (int key_code : keyboard.getPressedKeys()){
                        g.drawLine(12, y-5, 14, y-5);
                        g.drawString(KeyEvent.getKeyText(key_code), 16, y);
                        y += 20;
                    }
                    g.drawLine(12, 22, 12, y-20);
                }
            }, 0);
        }
    }

    public static void main(String[] args){
        new KeyboardInputTest();
    }
}
