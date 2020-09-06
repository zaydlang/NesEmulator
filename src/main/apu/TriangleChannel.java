package apu;

import model.Util;
import ui.window.Display;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.text.MutableAttributeSet;

import static apu.APU.SAMPLE_RATE;

public class TriangleChannel {
    private int linearCounterTimer;
    private int timer;
    private int lengthCounterTimer;
    private int counterReloadValue;

    private boolean lengthCounterHaltFlag;
    private boolean linearCounterReloadFlag;

    private AudioFormat af;
    private SourceDataLine line;
    private double toneOffset;

    private byte[] tone;

    private boolean enabled;
    private boolean dataLineStarted;
    private int     frequency;

    // https://wiki.nesdev.com/w/index.php/APU_Length_Counter
    private int[] lengthCounterLoadTable = new int[] {
            10, 254,  20,  2,  40,   4,  80,  6,  160,   8,  60,  10,  14,  12,  26,  14,
            12,  16,  24, 18,  48,  20,  96,  22, 192,  24,  72,  26,  16,  28,  32,  30
    };

    public TriangleChannel() {
        this.linearCounterTimer      = 0;
        this.timer                   = 0;
        this.lengthCounterTimer      = 0;
        this.counterReloadValue      = 0;
        this.frequency               = 1; // a frequency of 0 might cause division by 0 errors to occur

        this.lengthCounterHaltFlag   = false;
        this.linearCounterReloadFlag = false;

        dataLineStarted = false;
        af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
    }

    public void startDataLine() {
        dataLineStarted = true;
        try {
            line = AudioSystem.getSourceDataLine(af);
            line.open(af, SAMPLE_RATE);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        generateTone();
    }

    public void writeMemory(int pointer, int value) {
        if        (pointer == 0x4008) {
            lengthCounterHaltFlag = (value & 0x80) != 0;
            counterReloadValue    = value & 0x7F;
        } else if (pointer == 0x400A) {
            timer = (timer & 0x700) | (value & 0xFF);
            line.flush();
            generateTone();
        } else if (pointer == 0x400B) {
            timer = (timer & 0x0FF) | ((value & 0x7) << 8);
            enabled = true;
            lengthCounterTimer = lengthCounterLoadTable[(value & 0xF8) >> 3];
            linearCounterReloadFlag = true;
            line.flush();
            generateTone();
        }
    }

    public void frameCycle() {
        if (!dataLineStarted) {
            return;
        }

        if (linearCounterReloadFlag) {
            linearCounterTimer = counterReloadValue;
        } else if (linearCounterTimer != 0) {
            linearCounterTimer--;
        }

        if (!lengthCounterHaltFlag) {
            linearCounterReloadFlag = false;
        }

        if (linearCounterTimer != 0 && lengthCounterTimer != 0) {
            if (enabled) {
                line.write(tone, (int) (toneOffset % SAMPLE_RATE), (int) APU.OFFSET_INCREMENT);
                toneOffset = (toneOffset + APU.OFFSET_INCREMENT) % SAMPLE_RATE;
                line.write(tone, (int) (toneOffset % SAMPLE_RATE), (int) APU.OFFSET_INCREMENT);
                toneOffset = (toneOffset + APU.OFFSET_INCREMENT) % SAMPLE_RATE;
                line.write(tone, (int) (toneOffset % SAMPLE_RATE), (int) APU.OFFSET_INCREMENT);
                toneOffset = (toneOffset + APU.OFFSET_INCREMENT) % SAMPLE_RATE;
                line.write(tone, (int) (toneOffset % SAMPLE_RATE), (int) APU.OFFSET_INCREMENT);
                toneOffset = (toneOffset + APU.OFFSET_INCREMENT) % SAMPLE_RATE;
            } else {
                flushTone();
            }
        } else {
            int x = 2;
        }
    }

    private void generateTone() {
        frequency = (int) (Display.CYCLES_PER_FRAME * 8 * 60) / (32 * (timer + 1));
        double sOverF = ((double)SAMPLE_RATE) / (double) frequency;

        tone = new byte[SAMPLE_RATE];
        for (int i = 0; i < tone.length; i++) {
            tone[i] = (byte) (APU.VOLUME * 2 * Byte.MAX_VALUE * Math.abs((2 * (((double)i) % sOverF) / sOverF) - 1));
        }
    }

    private void flushTone() {
        // find the end of the current wave in tone
        int nearestEndpoint = (int) (Math.ceil((toneOffset) * ((double) frequency) / ((double) SAMPLE_RATE)) * ((double) SAMPLE_RATE) / ((double) frequency));
        line.write(tone, (int) toneOffset, nearestEndpoint - (int) toneOffset);
        toneOffset = 0;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!dataLineStarted) {
            return;
        }

        if (enabled) {
            line.start();
        } else {
            line.stop();
        }
    }

    public void cycleLengthCounter() {
        if (lengthCounterTimer != 0 && !lengthCounterHaltFlag) {
            lengthCounterTimer--;
        }

        if (lengthCounterTimer == 0) {
            enabled = false;
        }
    }
}
