package org.knuth.mgf.input;

/**
 * <p>This class is used to describe a single input device, which can be used
 *  to control the game.</p>
 * <p>It is possible to create your own {@code InputDevice}s to enable
 *  compatibility with Gamepads or other, non standard input devices.
 *  Simply implement this interface and register the new {@code InputDevice}
 *  using the {@link org.knuth.mgf.GameLoop#addInputDevice(InputDevice)}-
 *  method.</p>
 * <br />
 * <p>Querying an input device is done by in a "pull"-fashioned way by getting the
 *  desired input-device from the {@link org.knuth.mgf.GameLoop#getInputDevice(Class)}-
 *  method and using it's provided methods to check for the current state.</p>
 * <p>The methods exposed by each individual input-device may vary widely depending
 *  on the kind of device.</p>
 *
 * @author Lukas Knuth
 * @version 1.0
 */
public interface InputDevice {

    /**
     * <p>This method will be called by the {@code GameLoop} before any other events
     *  are triggered to update the current state of this input device.</p>
     */
    public void update();

    /**
     * <p>This method is called before {@link #update()} is called the first time, to
     *  do any initial work necessary to get the input-device ready to receive input.</p>
     */
    public void initialize();

    /**
     * <p>This method is called after the last call to {@link #update()} and should
     *  release any resources associated with this input-device.</p>
     * <p>It is save to assume that this method will only be called when the game is
     *  in the process of shutting down and the device will not be required after this
     *  method returns.</p>
     */
    public void release();

}
