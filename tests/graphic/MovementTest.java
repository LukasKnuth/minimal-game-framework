package graphic;

import org.knuth.mgf.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * A small test-program which moves objects on different speeds.
 * @author Lukas Knuth
 * @version 1.0
 */
public class MovementTest extends Scene{

    public static void main(String[] args){
        new MovementTest();
    }

    public MovementTest(){
        GameLoop.INSTANCE.Viewport.setTitle("Movement Test");
        GameLoop.INSTANCE.addScene("main", this);
        GameLoop.INSTANCE.startLoop();
    }

    @Override
    public void onStart(SceneBuilder builder){
        MovingSquare[] squares = new MovingSquare[4];
        squares[0] = new MovingSquare(0.5f, Color.GREEN, 20);
        squares[1] = new MovingSquare(0.6f, Color.RED, 60);
        squares[2] = new MovingSquare(0.7f, Color.YELLOW, 100);
        squares[3] = new MovingSquare(0.8f, Color.BLUE, 140);

        builder.putKeyBinding(KeyEvent.VK_P, 0, false, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pause with the "P"-key.
                if (GameLoop.INSTANCE.isPaused()) GameLoop.INSTANCE.play();
                else GameLoop.INSTANCE.pause();
            }
        });
        builder.putKeyBinding(KeyEvent.VK_I, 0, false, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Toggle interpolation:
                MovingSquare.use_interpolation = !MovingSquare.use_interpolation;
            }
        });
        GameLoop.INSTANCE.Viewport.setBackground(Color.BLACK);

        for (MovingSquare square : squares){
            builder.addRenderEvent(square, 0);
            builder.addMovementEvent(square);
        }
        builder.addRenderEvent(new RenderEvent() {
            @Override
            public void render(Graphics2D g, float interpolation) {
                g.setColor(Color.WHITE);
                g.drawString("Press P to pause", 40, 200);
                g.drawString("Press i to toggle interpolation (smooth animations): "
                        + (MovingSquare.use_interpolation ? "on" : "off"), 40, 220);
            }
        }, 1);

        FPSRenderer fps = new FPSRenderer();
        builder.addMovementEvent(fps);
        builder.addRenderEvent(fps, 10);
    }

    private static class MovingSquare implements RenderEvent, MovementEvent{

        public static boolean use_interpolation = true;

        private final float speed;
        private final Color color;

        private float x;
        private float y;

        private boolean blink;
        private TimeSpan last_blink;
        private TimeSpan blink_time;

        public MovingSquare(float speed, Color color, int y){
            this.speed = speed;
            this.color = color;
            x = 20;
            this.y = y;
            blink = false;
            blink_time = TimeSpan.fromSeconds(1);
            last_blink = TimeSpan.ZERO;
        }


        @Override
        public void move(TimeSpan total_game_time) {
            // Check if blinking end/start:
            if (total_game_time.subtract(last_blink).isGreaterThen(blink_time)){
                // Should be true every second.
                blink = !blink;
                last_blink = total_game_time;
            }
            this.x += speed;
        }

        @Override
        public void render(Graphics2D g, float interpolation) {
            g.setColor(color);
            int draw_x = (int)this.x;
            int draw_y = (int)this.y;
            if (use_interpolation){
                draw_x = (int)(this.x+speed*interpolation);
                draw_y = (int)(this.y+speed*interpolation);
            }

            if (blink){
                g.fillRect(draw_x, draw_y, 20, 20);
            } else {
                g.drawRect(draw_x, draw_y, 20, 20);
            }
        }

    }

    class FPSRenderer implements RenderEvent, MovementEvent{

        private int fps = 0;
        private int fps_show = fps;
        private final TimeSpan sec = TimeSpan.fromSeconds(1);
        private TimeSpan last = TimeSpan.ZERO;

        @Override
        public void render(Graphics2D g, float interpolation) {
            fps++;
            g.setColor(Color.YELLOW);
            g.drawString("FPS: "+fps_show, 5, 15);
        }

        @Override
        public void move(TimeSpan total_game_time) {
            if (total_game_time.subtract(last).isGreaterThen(sec)){
                fps_show = fps;
                fps = 0;
                last = total_game_time;
            }
        }
    }

}
