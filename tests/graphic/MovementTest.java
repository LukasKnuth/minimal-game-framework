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
        MovingSquare[] squares = new MovingSquare[3];
        squares[0] = new MovingSquare(0.5f, Color.GREEN, 20);
        squares[1] = new MovingSquare(0.6f, Color.RED, 60);
        squares[2] = new MovingSquare(0.7f, Color.YELLOW, 100);

        builder.putKeyBinding(KeyEvent.VK_P, 0, false, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pause with the "P"-key.
                if (GameLoop.INSTANCE.isPaused()) GameLoop.INSTANCE.play();
                else GameLoop.INSTANCE.pause();
            }
        });
        GameLoop.INSTANCE.Viewport.setBackground(Color.BLACK);

        for (MovingSquare square : squares){
            builder.addRenderEvent(square, 0);
            builder.addMovementEvent(square);
        }
        builder.addRenderEvent(new RenderEvent() {
            @Override
            public void render(Graphics g) {
                g.setColor(Color.WHITE);
                g.drawString("Press P to pause", 40, 160);
            }
        }, 1);
    }

    private static class MovingSquare implements RenderEvent, MovementEvent{

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
        public void render(Graphics g) {
            g.setColor(color);
            if (blink) g.fillRect((int) x, (int) y, 20, 20);
            else g.drawRect((int)x, (int)y, 20, 20);
        }

    }

}
