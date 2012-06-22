package sound;

import org.junit.Test;
import org.knuth.mgf.Sound;
import org.knuth.mgf.SoundManager;

import java.io.File;

/**
 * Tests for the {@code SoundManager}.</p>
 * The used test-sound is from the
 *  <a href="http://www.freesound.org/people/pryght%20one/sounds/27146/">Freesound Project</a>
 * @author Lukas Knuth
 * @version 1.0
 */
public class SoundManagerTest {

    @Test
    public void testMute() throws Exception {
        File test_sound = new File("tests/sound/test.wav");
        Sound sound = new Sound("test", test_sound.toURI().toURL());
        SoundManager manager = SoundManager.INSTANCE;
        manager.addSound(sound);
        manager.play("test");
        System.out.println("Playing");
        Thread.sleep(1000);

        manager.mute("test");
        System.out.println("Muted.");
        Thread.sleep(3000);

        // TODO Why does it take ~1 second to mute/unmute? Depending on implementation??

        manager.mute("test");
        System.out.println("Unmuted");
        Thread.sleep(3000);

        manager.stop("test");
        System.out.println("Stopped");
    }

    @Test
    public void testVolume() throws Exception{
        File test_sound = new File("tests/sound/test.wav");
        Sound sound = new Sound("test", test_sound.toURI().toURL());
        SoundManager manager = SoundManager.INSTANCE;
        manager.addSound(sound);

        manager.play("test");
        manager.setVolume("test", 100);
        System.out.println("Playing");
        Thread.sleep(2000);

        manager.setVolume("test", 80);
        System.out.println("80% volume");
        Thread.sleep(2000);

        manager.setVolume("test", 20);
        System.out.println("20% volume");
        Thread.sleep(2000);

        manager.setVolume("test", 100);
        System.out.println("100% volume");
        Thread.sleep(2000);

        manager.stop("test");
    }
}
