package org.knuth.mgf.input;

import org.knuth.mgf.GameLoop;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Description
 *
 * @author Lukas Knuth
 * @version 1.0
 */
public class Mouse implements InputDevice {

    private int x;
    private int y;
    private int wheel_movement;
    public enum MouseButton {
        RIGHT, LEFT, MIDDLE;

        private boolean isPressed;
    }

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
            wheel_movement += e.getWheelRotation();
        }
    };

    boolean reg = false;
    @Override
    public void update() {
        if (!reg){
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
     */
    public int getX() {
        return x;
    }

    /**
     * Get the mouse-pointers Y-coordinate, relative to the upper-left corner of
     *  the game-window ({@link GameLoop#Viewport}).
     * @return the Y-coordinate of the mouse-pointer, relative to the upper
     *  left corner of the game-window.
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
     * @return the current scroll-wheel value.
     */
    public int getScrollWheelValue(){
        return wheel_movement;
    }

}
