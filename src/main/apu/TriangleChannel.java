package apu;

import model.Util;
import ui.window.Display;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import static apu.APU.SAMPLE_RATE;

public class TriangleChannel {

    // TODO: Add Sweep Unit
    private int lengthCounterHalt; // 1 bit
    private int linearCounterLoad; // 7 bits
    private int timer;             // 11 bits
    private int lengthCounterLoad; // 5  bits

    private AudioFormat af;
    private SourceDataLine line;
    private double toneOffset;

    private byte[] tone;

    public TriangleChannel() {
        this.lengthCounterHalt = 0;
        this.linearCounterLoad = 0;
        this.timer             = 0;
        this.lengthCounterLoad = 0;

        this.toneOffset = 0;

        af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
        try {
            line = AudioSystem.getSourceDataLine(af);
            line.open(af, SAMPLE_RATE);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        generateTone();
    }

    public void writeMemory(int pointer, int value) {
        if        (pointer == Integer.parseInt("4008", 16)) {
            lengthCounterHalt = Util.getNthBits(value, 0, 7);
            lengthCounterLoad = Util.getNthBits(value, 7, 1);
        } else if (pointer == Integer.parseInt("4009", 16)) {
            // Unused
        } else if (pointer == Integer.parseInt("400A", 16)) {
            timer = Util.maskNthBits(value, timer, 0, 0, 8);
            generateTone();
        } else if (pointer == Integer.parseInt("400B", 16)) {
            lengthCounterLoad = Util.getNthBits(value, 0, 5);
            timer = Util.maskNthBits(value, timer, 0, 8, 3);
            generateTone();
        }
    }

    public void frameCycle() {
        line.write(tone, (int) (toneOffset % SAMPLE_RATE), (int) APU.OFFSET_INCREMENT);
        toneOffset = (toneOffset + APU.OFFSET_INCREMENT) % SAMPLE_RATE;
        line.write(tone, (int) (toneOffset % SAMPLE_RATE), (int) APU.OFFSET_INCREMENT);
        toneOffset = (toneOffset + APU.OFFSET_INCREMENT) % SAMPLE_RATE;
    }

    private void generateTone() {
        double frequency = (Display.CYCLES_PER_FRAME * 2 * 60) / (16 * (timer + 1));
        tone = new byte[SAMPLE_RATE];
        for (int i = 0; i < tone.length; i++) {
            tone[i] = ((double) i / (SAMPLE_RATE / frequency)) % 1 > 0.5 ? (byte) (APU.VOLUME * Byte.MAX_VALUE) : 0;
        }
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            line.start();
        } else {
            line.stop();
        }
    }
}
