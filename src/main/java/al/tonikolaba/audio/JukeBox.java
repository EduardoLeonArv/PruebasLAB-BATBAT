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

    // Inicialización directa de clips para evitar NullPointerException
    public static HashMap<String, Clip> clips = new HashMap<>();
    private static int gap;
    private static boolean mute = false;

    // Constructor privado para evitar instanciación
    private JukeBox() {
        throw new IllegalStateException("Utility Class");
    }

    public static void init() {
        gap = 0;
    }

    public static void load(String s, String n) {
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
            LoggingHelper.LOGGER.log(Level.SEVERE, "Error al cargar el clip: " + e.getMessage(), e);
        }
    }

    public static void play(String s) {
        play(s, gap);
    }

    public static void play(String s, int i) {
        if (mute) return;
        Clip c = clips.get(s);
        if (c == null) return;
        if (c.isRunning()) c.stop();
        c.setFramePosition(i);
        while (!c.isRunning()) c.start();
    }

    public static void stop(String s) {
        Clip c = clips.get(s);
        if (c != null && c.isRunning()) {
            c.stop();
        }
    }

    public static void resume(String s) {
        if (mute) return;
        Clip c = clips.get(s);
        if (c != null && !c.isRunning()) {
            c.start();
        }
    }

    public static void loop(String s) {
        Clip c = clips.get(s);
        if (c != null) {
            loop(s, gap, gap, c.getFrameLength() - 1);
        }
    }

    public static void loop(String s, int frame) {
        Clip c = clips.get(s);
        if (c != null) {
            loop(s, frame, gap, c.getFrameLength() - 1);
        }
    }

    public static void loop(String s, int start, int end) {
        loop(s, gap, start, end);
    }

    public static void loop(String s, int frame, int start, int end) {
        stop(s);
        if (mute) return;
        Clip c = clips.get(s);
        if (c != null) {
            c.setLoopPoints(start, end);
            c.setFramePosition(frame);
            c.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void setPosition(String s, int frame) {
        Clip c = clips.get(s);
        if (c != null) {
            c.setFramePosition(frame);
        }
    }

    public static int getFrames(String s) {
        Clip c = clips.get(s);
        return (c != null) ? c.getFrameLength() : -1;
    }

    public static int getPosition(String s) {
        Clip c = clips.get(s);
        return (c != null) ? c.getFramePosition() : -1;
    }

    public static void close(String s) {
        Clip c = clips.get(s);
        if (c != null) {
            stop(s);
            c.close();
        }
    }
}
