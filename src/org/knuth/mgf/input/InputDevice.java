package org.knuth.mgf.input;

/**
 * <p>This class is used to describe a single input device, which can be used
 *  to control the game.</p>
 * <p>It is possible to create your own {@code InputDevice}s to enable
 *  compatibility with Gamepads or other input devices.</p>
 *
 * <p>Querying an input device is done by using a "pull"-mechanism by getting the
 *  desired input-device from the {@code GameLoop} and using it's provided methods
 *  to check it's current state.</p>
 * <p>The methods exposed by each input-device may vary.</p>
 *
 * @author Lukas Knuth
 * @version 1.0
 */
public interface InputDevice {

    /**
     * <p>This method will be called by the {@code GameLoop} to update the
     *  current state of this input device.</p>
     */
    public void update();

}
