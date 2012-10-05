package org.knuth.mgf;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * <p>Represents a single sound that is added to the game for later playback.</p>
 * <p>To play the sound, add it to the sound-library. See the {@link SoundManager}-
 *  documentation.</p>
 * @author Lukas Knuth
 * @version 1.0
 */
public class Sound {

    /** The audio-stream to read the sound from */
    private Clip audio;
    /** The given event-name to trigger this sound */
    private final String event;
    /** How often this sound should be looped. */
    private int loop_cycles;

    /** The control, used to adjust the volume of this {@code Clip} */
    private FloatControl volume_control;
    /** The control used to mute this {@code Clip} */
    private BooleanControl mute_control;

    /**
     * Create a new {@code Sound}, which can the be added to the {@link SoundManager}
     *  for playback.
     * @param event the name of the event, used to trigger this sound.
     * @param sound_res the audio-resource of this sound.
     * @throws IllegalArgumentException if the give URL couldn't be used to get
     *  a working audio input.
     * @see SoundManager#addSound(Sound)
     */
    public Sound(String event, URL sound_res){
        this.event = event;
        try {
            audio = AudioSystem.getClip();
            audio.open(AudioSystem.getAudioInputStream(sound_res));
            // Obtain the volume-control:
            volume_control = (FloatControl) audio.getControl(FloatControl.Type.MASTER_GAIN);
            mute_control = (BooleanControl) audio.getControl(BooleanControl.Type.MUTE);
        } catch (LineUnavailableException e){
            System.err.println("Something is blocking the audio line.");
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * This method will set the playback-volume of this {@code Sound}-instance to the
     *  given amount in percent.
     * @param percent a percent-number (between 0 and 100) where {@code 100} means
     *  <i>maximum volume</i> and {@code 0} means <i>minimum volume</i>.
     */
    void setVolumePercent(int percent){
        if (percent < 0 || percent > 100)
            throw new IllegalArgumentException(percent+"% is not a valid percent-value");
        // Set the volume:
        volume_control.setValue((volume_control.getMinimum() / 100) * (100-percent));
    }

    /**
     * <p>Calling this method will cause this {@code Sound} to be muted.</p>
     * <p>Calling the method again after muting it will set the sound-volume to the
     *  same amount it was before muting it.<p>
     */
    void mute(){
        // Mute/Un-mute (always the opposite one)
        mute_control.setValue( !mute_control.getValue() );
    }

    /**
     * Returns this sounds event name.
     * @return the event-name.
     */
    String getEventName(){
        return this.event;
    }

    /**
     * <p>Get the {@code AudioInputStream} for this {@code Sound}, which is then used
     *  for playback.</p>
     * <p>The returned {@code AudioInputStream} is guaranteed to be valid!</p>
     * <p>This method is <u>not for direct use</u> and should only be accessed by the
     *  {@code SoundManager}-class.</p>
     * @return the input-stream to read the sound from.
     */
    Clip getAudioClip(){
        return this.audio;
    }

    /**
     * <p>Get the count of cycles this sound should loop.</p>
     * <p>This might return an actual number {@code > 0}, which indicates the actual
     *  count of cycles, the exact value of {@code 0} telling you that this sound
     *  isn't looping at all, or a number {@code < 0} to indicate, that it's looping
     *  forever.</p>
     * @return the amount of cycles this sound is looping, exactly {@code 0}or a
     *  number {@code <= 0} to indicate, that it's looping forever.
     */
    int getLoopCycles() {
        return loop_cycles;
    }

    /**
     * <p>Decline this sound to be looped for the given cycles, exactly {@code 0} to
     *  indicate that this sound is not looping at all, or forever by giving
     *  a number {@code < 0}.</p>
     * @param cycles how often the sound should be looped. Give {@code > 0} to
     *  loop it for n-times, exactly {@code 0} to not loop at all or {@code < 0}
     *  to loop forever.
     */
    void setLoopCycles(int cycles) {
        loop_cycles = cycles;
    }
}
