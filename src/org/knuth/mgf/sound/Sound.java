package org.knuth.mgf.sound;

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

    /** A workaround for when there is no mute, only volume control */
    private float before_mute;

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
            AudioInputStream audio_in = AudioSystem.getAudioInputStream(sound_res);
            AudioFormat format = audio_in.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            this.audio = (Clip) AudioSystem.getLine(info);
            this.audio.open(audio_in);
            // Obtain the volume-control:
            if (this.audio.isControlSupported(FloatControl.Type.MASTER_GAIN)){
                volume_control = (FloatControl) audio.getControl(FloatControl.Type.MASTER_GAIN);
            }
            if (this.audio.isControlSupported(BooleanControl.Type.MUTE)){
                mute_control = (BooleanControl) audio.getControl(BooleanControl.Type.MUTE);
            }
        } catch (LineUnavailableException e){
            System.err.println("Something is blocking the audio line.");
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * This method will set the playback-volume of this {@code Sound}-instance to the
     *  given amount in percent.
     * @param percent a percent-number (between 0.0 and 1.0) where {@code 1.0} means
     *  <i>maximum volume</i> and {@code 0.0} means <i>minimum volume</i>.
     */
    void setVolumePercent(float percent){
        if (percent < 0.0 || percent > 1.0)
            throw new IllegalArgumentException(percent+" is not a valid percent-value");
        if (volume_control == null){
            System.err.println("Volume-Control for sounds is not supported");
            return;
        }
        // Set the volume:
        float db = (float) ((Math.log((percent != 0.0) ? percent : 1.0E-14) / Math.log(10.0)) * 20.0);
        volume_control.setValue(db);
        before_mute = db;
    }

    /**
     * <p>Mutes/Un-mutes the given sound.</p>
     * @param mute whether the sound should be muted or not (un-muted).
     */
    void mute(boolean mute){
        if (mute_control == null){
            // We don't have a mute-control... try using the volume:
            if (volume_control != null){
                if (mute){
                    before_mute = volume_control.getValue();
                    volume_control.setValue(0);
                } else {
                    volume_control.setValue(before_mute);
                }
            } else {
                System.err.println("Muting sounds is not supported.");
            }
        } else {
            mute_control.setValue(mute);
        }
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
