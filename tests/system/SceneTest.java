package system;

import org.knuth.mgf.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * <p>An easy test to check if and how the support for multiple scenes works.</p>
 * This is not a JUnit test!
 * @author Lukas Knuth
 * @version 1.0
 */
public class SceneTest {

    public static void main(String[] args){
        new SceneTest();
    }

    public SceneTest(){
        GameLoop.INSTANCE.Viewport.setTitle("Scene Test");
        GameLoop.INSTANCE.addScene("game", new GameScene());
        GameLoop.INSTANCE.addScene("menu", new MenuScene());
        GameLoop.INSTANCE.startLoop();
    }

    private class GameScene extends Scene{
        @Override
        public void onStart(SceneBuilder builder){
            Walker walker = new Walker();
            builder.addRenderEvent(walker, 0);
            builder.addMovementEvent(walker);
            builder.addRenderEvent(new RenderEvent() {
                @Override
                public void render(Graphics2D g, float interpolation) {
                    g.drawString("ESC back to menu", 10, 20);
                }
            }, 1);

            builder.putKeyBinding(KeyEvent.VK_ESCAPE, 0, true, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GameLoop.INSTANCE.switchScene("menu");
                    System.out.println("Menu...");
                }
            });
        }

        @Override
        public void onResume(){
            GameLoop.INSTANCE.Viewport.setBackground(Color.WHITE);
        }
    }

    private class MenuScene extends Scene{
        @Override
        public void onStart(SceneBuilder builder){
            builder.addRenderEvent(new RenderEvent() {
                @Override
                public void render(Graphics2D g, float interpolation) {
                    g.setColor(Color.WHITE);
                    g.drawString("Menu screen...", 200, 200);
                    g.drawString("Press Space to play...", 200, 300);
                }
            }, 0);

            builder.putKeyBinding(KeyEvent.VK_SPACE, 0, true, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GameLoop.INSTANCE.switchScene("game");
                    System.out.println("Game...");
                }
            });
        }

        @Override
        public void onResume(){
            GameLoop.INSTANCE.Viewport.setBackground(Color.BLACK);
        }
    }

    private class Walker implements MovementEvent, RenderEvent{

        private float x;
        private int y;
        private float speed = 2.5f;
        private Color color = Color.GREEN;
        private final TimeSpan color_switch_interv = TimeSpan.fromSeconds(1);
        private TimeSpan switch_start = TimeSpan.ZERO;

        public Walker(){
            this.x = 0;
            this.y = 60;
        }

        @Override
        public void move(TimeSpan time) {
            if (x < GameLoop.INSTANCE.Viewport.getView().getSize().getWidth())
                x += speed;
            else {
                x = 10f;
                y += 30;
            }
            // Change colors:
            if (time.subtract(switch_start).isGreaterThen(color_switch_interv)){
                if (color == Color.GREEN) color = Color.RED;
                else color = Color.GREEN;
                // Reset:
                switch_start = time;
            }
        }

        @Override
        public void render(Graphics2D g, float interpolation) {
            g.setColor(this.color);
            g.fillOval((int)(x+speed*interpolation), y, 40, 40);
        }
    }
}
