package apu;

import apu.function.SoundFunction;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SoundGenerator implements Runnable {
    public static final int SAMPLE_RATE = 44100;

    private double         offsetIncrement;
    private double         offset;

    private AudioFormat    af;
    private boolean        playingSound;
    private SourceDataLine line;

    private SoundFunction  soundFunction;
    private byte[]         tone;

    public SoundGenerator(SoundFunction soundFunction, int fps) {
        this.offsetIncrement = (double) SAMPLE_RATE / (double) fps;
        this.soundFunction   = soundFunction;

        playingSound = false;
        offset       = 0;

        af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
        try {
            line = AudioSystem.getSourceDataLine(af);
            line.open(af, SAMPLE_RATE);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (playingSound) {
                line.start();
                line.write(tone, (int) (offset % SAMPLE_RATE), (int) offsetIncrement);
                offset = (offset + offsetIncrement) % SAMPLE_RATE;
            } else {
                line.stop();
            }
        }
    }

    public void setPlayingSound(boolean playingSound) {
        this.playingSound = playingSound;
    }

    public void setFrequency(int frequency) {
        tone = soundFunction.getTone(frequency);
    }
}
