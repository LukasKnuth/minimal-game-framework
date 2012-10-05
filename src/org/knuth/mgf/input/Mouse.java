package org.knuth.mgf.input;

import org.knuth.mgf.GameLoop;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * <p>This is the a standard input-device for getting input from a standard
 *  mouse with a mouse-wheel andup to three buttons: left, right and middle
 *  (might be the mouse wheel).</p>
 * <p>A usage example might be found in the
 *  <a href="https://github.com/LukasKnuth/minimal-game-framework/blob/master/tests/system/MouseInputTest.java">{@code MouseInputTest}</a> class.</p>
 *
 * @author Lukas Knuth
 * @version 1.0
 */
public class Mouse implements InputDevice {

    private int x;
    private int y;
    private int wheel_value;
    public enum MouseButton {
        RIGHT, LEFT, MIDDLE;

        private boolean isPressed;
    }

    /**
     * <p>The mouse-adapter, used to get events from the mouse.</p>
     * <p>We can't implement this in a pull-fashioned way, so this is
     *  the workaround which simulates that behaviour.</p>
     */
    private MouseAdapter adapter = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            switch (e.getButton()){
                case MouseEvent.BUTTON1:
                    MouseButton.LEFT.isPressed = true;
                    break;
                case MouseEvent.BUTTON2:
                    MouseButton.MIDDLE.isPressed = true;
                    break;
                case MouseEvent.BUTTON3:
                    MouseButton.RIGHT.isPressed = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e){
            switch (e.getButton()){
                case MouseEvent.BUTTON1:
                    MouseButton.LEFT.isPressed = false;
                    break;
                case MouseEvent.BUTTON2:
                    MouseButton.MIDDLE.isPressed = false;
                    break;
                case MouseEvent.BUTTON3:
                    MouseButton.RIGHT.isPressed = false;
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            wheel_value += e.getWheelRotation();
        }
    };

    /** Whether the {@code MouseAdapter} is registered yet */
    boolean reg = false;
    @Override
    public void update() {
        if (!reg){
            // Register the adapter:
            GameLoop.INSTANCE.Viewport.getView().addMouseListener(adapter);
            GameLoop.INSTANCE.Viewport.getView().addMouseWheelListener(adapter);
            GameLoop.INSTANCE.Viewport.getView().addMouseMotionListener(adapter);
            reg = true;
        }
        // Get coordinates relative to the window:
        Point mouse_pointer = MouseInfo.getPointerInfo().getLocation();
        this.x = ((int) mouse_pointer.getX()) - GameLoop.INSTANCE.Viewport.getX();
        this.y = ((int) mouse_pointer.getY()) - GameLoop.INSTANCE.Viewport.getY();
    }

    /**
     * Get the mouse-pointers X-coordinate, relative to the upper-left corner of
     *  the game-window ({@link GameLoop#Viewport}).
     * @return the X-coordinate of the mouse-pointer, relative to the upper
     *  left corner of the game-window.
     *  <p>A negative value indicates that the mouse is outside the window on the
     *   left side, a value greater than the window-width indicates it's outside
     *   on the right side.</p>
     */
    public int getX() {
        return x;
    }

    /**
     * Get the mouse-pointers Y-coordinate, relative to the upper-left corner of
     *  the game-window ({@link GameLoop#Viewport}).
     * @return the Y-coordinate of the mouse-pointer, relative to the upper
     *  left corner of the game-window.
     *  <p>A negative value indicates that the mouse is outside the window on the
     *   top, a value greater than the window-height indicates it's outside
     *   on the bottom.</p>
     */
    public int getY() {
        return y;
    }

    /**
     * <p>This method will return {@code true} if the given mouse-button was clicked,
     *  and {@code false} otherwise.</p>
     * <p>Note that only clicks inside the game-window are registered by this method!</p>
     * @param button the button to check the current state for.
     * @return whether the button is pressed or not.
     */
    public boolean getButtonPressed(MouseButton button){
        return button.isPressed;
    }

    /**
     * <p>The current scroll-wheel value of the mouse.</p>
     * <p>This value will be increased by the amount of "clicks" that have been performed
     *  since the last update. Scrolling up (away from the user) decreases the value,
     *  scrolling down (towards the user) increases it.</p>
     * <p>To determine the direction in which the user scrolled, store the last value and
     *  compare it to the current one. </p>
     * @return the current scroll-wheel value.
     */
    public int getScrollWheelValue(){
        return wheel_value;
    }

}
