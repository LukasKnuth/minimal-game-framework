package org.knuth.mgf.input;

import org.knuth.mgf.GameLoop;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>An Input-device to get input from the keyboard in a pulling-fashion.</p>
 *
 * @author Lukas Knuth
 * @version 1.0
 */
public class Keyboard implements InputDevice {

    private final Map<Integer, Boolean> key_table = new HashMap<Integer, Boolean>();
    private final Map<Integer, Boolean> frozen_key_table = new HashMap<Integer, Boolean>();

    private KeyAdapter adapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            key_table.put(e.getKeyCode(), Boolean.TRUE);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            key_table.put(e.getKeyCode(), Boolean.FALSE);
        }
    };

    @Override
    public void initialize() {
        GameLoop.INSTANCE.Viewport.getWindow().addKeyListener(adapter);
    }
    @Override public void release() {}

    @Override public void update() {
        frozen_key_table.clear();
        for (Map.Entry<Integer, Boolean> key : key_table.entrySet()){
            if (key.getValue() == Boolean.TRUE){
                frozen_key_table.put(key.getKey(), key.getValue());
            }
        }
    }

    /**
     * Checks whether the key corresponding to the given key-code is currently
     *  pressed or not.
     * @param key_code a key-code (a {@link java.awt.event.KeyEvent} constant),
     *                 representing the code to check for.
     * @return whether the key is currently pressed or not.
     */
    public boolean isKeyPressed(int key_code){
        if (frozen_key_table.containsKey(key_code)){
            return frozen_key_table.get(key_code);
        } else {
            // It's not in the map if it has never been pressed. So, it's not pressed now.
            return false;
        }
    }

    /**
     * Get a list of currently pressed keys. The keys are stored as their corresponding
     *  constants from the {@link java.awt.event.KeyEvent}-class.
     */
    public Collection<Integer> getPressedKeys(){
        return Collections.unmodifiableCollection(frozen_key_table.keySet());
    }
}
