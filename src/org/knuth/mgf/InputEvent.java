package org.knuth.mgf;

import java.awt.event.KeyEvent;

/**
 * Describes an event which is interested in the users input.
 * @author Lukas Knuth
 * @version 1.0
 */
public interface InputEvent {

    /**
     * The type of event that was triggered.</p>
     * This is used to determine if a key was pressed
     *  ({@code KeyEventType.PRESSED}), or released
     *  ({@code KeyEventType.RELEASED}).
     */
    public enum KeyEventType{
        PRESSED, RELEASED
    }

    /**
     * The user has given input by using the keyboard.</p>
     * If no key was pressed, no event-listeners are notified.
     *  This means, that the parameters given to this method
     *  can never be {@code null} or any other "spacing"-value.
     * @param event the input-event.
     * @param type the type of event that occurred.
     */
    public void keyboardInput(KeyEvent event, KeyEventType type);
}
