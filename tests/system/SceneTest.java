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
                public void render(Graphics g) {
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
                public void render(Graphics g) {
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

        public Walker(){
            this.x = 0;
            this.y = 60;
        }

        @Override
        public void move(TimeSpan total_game_time) {
            if (x < GameLoop.INSTANCE.Viewport.getView().getSize().getWidth())
                x += 2.5f;
            else {
                x = 10f;
                y += 30;
            }
        }

        @Override
        public void render(Graphics g) {
            g.setColor(Color.GREEN);
            g.fillOval((int)x, y, 40, 40);
        }
    }
}
