package org.knuth.mgf;

import javax.sound.sampled.Clip;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class offers the basic logic to play sounds when certain events occur.</p>
 * <p>Due to restrictions in the "Java SE 6" library, the <b>supported file-formats are
 *  "wav", "au" and "aiff"</b><br>
 *  The samples can be either 8-bit or 16-bit, with sampling rate from 8-48kHz</p>
 *
 * <p>The basic workflow to get music playing in your game is:</p>
 * <ol>
 *     <li>Create {@link Sound}-instances of your sound/music files</li>
 *     <li>Populate the "music library" using the {@link #addSound(Sound)}-method</li>
 *     <li>Perform actions (play, mute, etc) on certain sounds</li>
 * </ol>
 * <p>To identify the different sounds, a consistent "event name" is used. This event-name
 *  binds a sound to a occurring event (by logic). For example, an explosion might have
 *  the event-name "explosion".</p>
 *
 * <p>To get your sound-files ready for playback, place them inside a resource-package (a
 *  normal package which only contains resources like sound/graphics) and follow these
 *  steps:
 * <ol>
 *     <li>Create a {@link Sound}-object from the file</li>
 *     <li>Choose a corresponding event-name for the new sound</li>
 *     <li>Add it to the music-library by using the {@link #addSound(Sound)}-method</li>
 *     <li>Use the corresponding methods to interact with the sound (e.g. {@link #play(String)}</li>
 * </ol>
 * @author Lukas Knuth
 * @version 1.0
 */
public enum SoundManager {

    /** The instance to work with */
    INSTANCE;

    /** The sounds mapped to their event-names */
    private final Map<String, Sound> sounds;
    /** A list of sounds which where paused with {@code pauseAll()} */
    private final Map<String, Integer> paused;

    /**
     * Singleton -> Private constructor.
     */
    private SoundManager(){
        sounds = new HashMap<String, Sound>(8);
        paused = new HashMap<String, Integer>(8);
    }

    /**
     * Set the playback-volume for the given {@code Sound}-event to the
     *  given amount in percent.
     * @param event_name the event-name of the desired sound.
     * @param percent a percent-number (between 0 and 100) where {@code 100} means
     *  <i>maximum volume</i> and {@code 0} means <i>minimum volume</i>.
     */
    public void setVolume(String event_name, int percent){
        // Check if the sound is in the library:
        if (!sounds.containsKey(event_name))
            throw new IllegalArgumentException("There is no sound for '"+event_name+"'");
        // Set the new Volume:
        sounds.get(event_name).setVolumePercent(percent);
    }

    /**
     * <p>Calling this method will cause the desired sound to be muted.</p>
     * <p>Calling the method again after muting it will set the sound-volume to the
     *  same amount it was before muting it.</p>
     * @param event_name the event-name of the desired sound.
     */
    public void mute(String event_name){
        // Check if the sound is in the library:
        if (!sounds.containsKey(event_name))
            throw new IllegalArgumentException("There is no sound for '"+event_name+"'");
        // Mute the sound:
        sounds.get(event_name).mute();
    }

    /**
     * <p>This method will continue the playback of all previously paused sounds.</p>
     * <p>Sounds can be paused by using the {@code pauseAll()}-method.</p>
     * @see #pauseAll()
     */
    public void unpauseAll(){
        if (paused.size() == 0) return;
        // Continue all paused sounds:
        for (Map.Entry<String, Integer> entry : paused.entrySet()){
            if (entry.getValue() > 0){
                // Sound was looping for n-times:
                sounds.get(entry.getKey()).getAudioClip().loop(entry.getValue());
            } else if (entry.getValue() < 0){
                // Sound was looping forever:
                sounds.get(entry.getKey()).getAudioClip().loop(-1);
            } else
                sounds.get(entry.getKey()).getAudioClip().start();
        }
    }

    /**
     * <p>This method is used to pause all currently played sounds, no madder if
     *  they where just playing or looping. One of the use-cases could be pausing
     *  every sound playback, when pausing your application.</p>
     * <p>To continue all sounds, paused by this method, use the {@code unpauseAll()}-
     *  method.</p>
     * @see #unpauseAll()
     */
    public void pauseAll(){
        // Clear the list:
        paused.clear();
        // Pause everything:
        for (Sound sound : sounds.values()){
            if (sound.getAudioClip().isActive()){
                // Stop the sound
                sound.getAudioClip().stop();
                // List all playing sounds and whether their looping or not:
                paused.put(sound.getEventName(), sound.getLoopCycles());
            }
        }
    }

    /**
     * <p>Plays the sound, identified by the given event-name for only once.</p>
     * <p>If you want to loop a sound or play it more frequently, you might want
     *  to use the {@code loop()}-method.</p>
     * <p>When this method is called, any previously playback will be stopped and
     *  the sound will start from the beginning.</p>
     * @param event_name the event-name of the desired sound.
     * @return the length of the played sound in milliseconds or -1 if
     *  there was a problem reading the length.
     * @throws IllegalArgumentException if the specified {@code event_name}
     *  is not in the media-library.
     * @see SoundManager#loop(String, int)
     */
    public int play(String event_name){
        // Check if the sound is in the library:
        if (!sounds.containsKey(event_name))
            throw new IllegalArgumentException("There is no sound for '"+event_name+"'");
        // Play it:
        Clip clip = sounds.get(event_name).getAudioClip();
        // Check if playback is finished:
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
        return (int) (clip.getMicrosecondLength()/1000);
    }

    /**
     * <p>Will loop the sound identified by the given {@code event_name}.</p>
     * <p>This is thought as a method for playing background noises (for example). When
     *  specifying the {@code loop_cycles}, you can either give a number
     *  {@code > 0} to specify how often the sound should be looped, or a number of
     *  {@code <= 0} to make it loop "forever".</p>
     * To stop a sound which is continuously looping, use the {@code stop()}-method.</p>
     * <p>If a sound is already looping, calling this method will not have any effect!</p>
     * @param event_name the event-name of the desired sound.
     * @param loop_cycles how often the sound should be looped. Give {@code > 0} to
     *  loop it for n-times or {@code <= 0} to loop forever.
     * @throws IllegalArgumentException if the specified {@code event_name}
     *  is not in the media-library.
     * @see SoundManager#stop(String)
     */
    public void loop(String event_name, int loop_cycles){
        // Check if the sound is in the library:
        if (!sounds.containsKey(event_name))
            throw new IllegalArgumentException("There is no sound for '"+event_name+"'");
        if (sounds.get(event_name).getLoopCycles() != 0) return;
        // Loop it:
        Clip clip = sounds.get(event_name).getAudioClip();
        if (loop_cycles <= 0){
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            sounds.get(event_name).setLoopCycles(-1);
        } else {
            clip.loop(loop_cycles);
            sounds.get(event_name).setLoopCycles(loop_cycles);
        }
    }

    /**
     * <p>This method should be used to stop a currently playing sound.</p>
     * <p>A common case of usage for this method is, when you are "endlessly" playing a
     *  sound (by using the {@code loop()}-method) and now want to stop it.</p>
     * <p>Stopping a sound will also cause it to be set to it's beginning, so any further
     *  playback using either the {@code loop()} or {@code play()}-methods will start
     *  from the beginning again.</p>
     * @param event_name the event-name of the desired sound.
     * @throws IllegalArgumentException if the specified {@code event_name}
     *  is not in the media-library.
     */
    public void stop(String event_name){
        // Check if the sound is in the library:
        if (!sounds.containsKey(event_name))
            throw new IllegalArgumentException("There is no sound for '"+event_name+"'");
        // Stop it:
        Clip clip = sounds.get(event_name).getAudioClip();
        sounds.get(event_name).setLoopCycles(0);
        clip.stop();
        clip.setFramePosition(0);
    }

    /**
     * <p>Add a sound to the "media library" for later playback.</p>
     * <p>To play this sound, use the {@code play(String)}-method with the event-
     *  name, you choose for this sound.</p>
     * @param sound the sound to add to the "media library".
     * @see #play(String)
     */
    public void addSound(Sound sound){
        // Validate the argument:
        if (sound == null) throw  new NullPointerException("The sound can't be null.");
        // Add to the library:
        sounds.put(sound.getEventName(), sound);
    }
}
