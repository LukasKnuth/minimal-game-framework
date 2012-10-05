package system;

import org.knuth.mgf.GameLoop;
import org.knuth.mgf.RenderEvent;
import org.knuth.mgf.Scene;
import org.knuth.mgf.input.Mouse;

import javax.swing.*;
import java.awt.*;

/**
 * A simple implementation example on how the {@link Mouse}-class can be used
 *  for input purposes.
 *
 * @author Lukas Knuth
 * @version 1.0
 */
public class MouseInputTest {

    public MouseInputTest(){
        JFrame frame = new JFrame("Mouse Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(GameLoop.INSTANCE.Viewport.getView());

        GameLoop.INSTANCE.addScene("main", new MouseScene());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        GameLoop.INSTANCE.startLoop();
    }

    private class MouseScene extends Scene{

        @Override
        public void onStart(SceneBuilder builder){
            GameLoop.INSTANCE.Viewport.setBackground(Color.BLACK);
            builder.addRenderEvent(new RenderEvent() {
                int last_wheel = 0;
                @Override
                public void render(Graphics g) {
                    Mouse mouse = GameLoop.INSTANCE.getInputDevice(Mouse.class);
                    g.setColor(Color.WHITE);
                    g.drawString("X: "+mouse.getX(), 40, 100);
                    g.drawString("Y: "+mouse.getY(), 40, 120);
                    g.drawString("L-Press: "+mouse.getButtonPressed(Mouse.MouseButton.LEFT), 40, 140);
                    g.drawString("M-Press: "+mouse.getButtonPressed(Mouse.MouseButton.MIDDLE), 40, 160);
                    g.drawString("R-Press: "+mouse.getButtonPressed(Mouse.MouseButton.RIGHT), 40, 180);
                    // Determine the scrolling-direction:
                    if (last_wheel < mouse.getScrollWheelValue())
                        g.drawString("Wheel: v", 40, 200);
                    else if (last_wheel > mouse.getScrollWheelValue())
                        g.drawString("Wheel: ^", 40, 200);
                    else
                        g.drawString("Wheel: -", 40, 200);
                    last_wheel = mouse.getScrollWheelValue();
                }
            }, 0);
        }
    }

    public static void main(String[] args){
        new MouseInputTest();
    }

}
