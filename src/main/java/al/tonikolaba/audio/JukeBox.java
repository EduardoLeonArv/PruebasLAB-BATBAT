package al.tonikolaba.audio;

import java.util.HashMap;
import java.util.logging.Level;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import al.tonikolaba.handlers.LoggingHelper;

/**
 * Clase JukeBox para gestionar clips de audio.
 */
public class JukeBox {

    public static HashMap<String, Clip> clips;
    private static int gap;
    private static boolean mute = false;
    static {
        init(); // Inicialización automática
    }
    public static void init() {
        clips = new HashMap<>();
        gap = 0;
    }
    public static void load(String s, String n) {
        if (clips == null) {
            init();
        }
        Clip clip;
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(JukeBox.class.getResourceAsStream(s));
            AudioFormat baseFormat = ais.getFormat();
            AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
                    baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
            AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
            clip = AudioSystem.getClip();
            clip.open(dais);
            clips.put(n, clip);
        } catch (Exception e) {
            LoggingHelper.LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
    public static void play(String s) {
        play(s, gap);
    }
    public static void play(String s, int i) {
        if (mute || clips == null || !clips.containsKey(s)) {
            return;
        }
        Clip c = clips.get(s);
        if (c.isRunning()) {
            c.stop();
        }
        c.setFramePosition(i);
        while (!c.isRunning()) {
            c.start();
        }
    }
    public static void stop(String s) {
        if (clips == null || !clips.containsKey(s)) {
            return;
        }
        if (clips.get(s).isRunning()) {
            clips.get(s).stop();
        }
    }
    // Los otros métodos no cambian, pero se debe agregar validación de `null` donde sea necesario
    public static void close(String s) {
        if (clips == null || !clips.containsKey(s)) {
            return;
        }
        stop(s);
        clips.get(s).close();
    }
    public static int getFrames(String s) {
		if (clips == null || !clips.containsKey(s)) {
			return -1; // Devuelve un valor predeterminado si no existe el clip
		}
        return clips.get(s).getFrameLength();
	}
    public static void loop(String s, int start, int end) {
		loop(s, 0, start, end); // Llama al método más completo con frame = 0
	}
    public static void loop(String s, int frame, int start, int end) {
        if (clips == null || !clips.containsKey(s)) {
			return;
        }
		Clip c = clips.get(s);
		stop(s);
        if (mute) {
			return;
		}
		c.setLoopPoints(start, end);
		c.setFramePosition(frame);
		c.loop(Clip.LOOP_CONTINUOUSLY);
	}
    	
}
