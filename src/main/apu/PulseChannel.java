package apu;

import model.Util;
import ui.window.Display;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import static apu.APU.SAMPLE_RATE;

public class PulseChannel {

    // TODO: Add Sweep Unit
    private int duty;               // 2  bits
    private int envelopeLoop;       // 1  bit
    private int constantVolume;     // 1  bit
    private int volume;             // 4  bits
    private int timer;              // 11 bits
    private int lengthCounterTimer; // 5  bits

    private int memoryOffset;

    private AudioFormat af;
    private SourceDataLine line;
    private double toneOffset;

    private byte[] tone;

    private boolean enabled;
    private boolean dataLineStarted;

    // https://wiki.nesdev.com/w/index.php/APU_Length_Counter
    private int[] lengthCounterLoadTable = new int[] {
            10, 254,  20,  2,  40,   4,  80,  6,  160,   8,  60,  10,  14,  12,  26,  14,
            12,  16,  24, 18,  48,  20,  96,  22, 192,  24,  72,  26,  16,  28,  32,  30
    };

    public PulseChannel(int memoryOffset) {
        this.duty               = 0;
        this.envelopeLoop       = 0;
        this.constantVolume     = 0;
        this.volume             = 0;
        this.timer              = 0;
        this.lengthCounterTimer = 0;

        this.memoryOffset       = memoryOffset;
        this.toneOffset         = 0;

        this.enabled            = false;

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
        if        (pointer + memoryOffset == 0x4000) {
            volume            = Util.getNthBits(value, 0, 4);
            constantVolume    = Util.getNthBits(value, 4, 1);
            envelopeLoop      = Util.getNthBits(value, 5, 1);
            duty              = Util.getNthBits(value, 6, 2);
            if (constantVolume == 0) {
                lengthCounterTimer = 0;
            }
        } else if (pointer + memoryOffset == 0x4002) {
            timer = Util.maskNthBits(value, timer, 0, 0, 8);
            line.flush();
            generateTone();
        } else if (pointer + memoryOffset == 0x4003) {
            lengthCounterTimer = lengthCounterLoadTable[Util.getNthBits(value, 0, 5)];
            enabled = true;
            timer = Util.maskNthBits(value, timer, 0, 8, 3);
            line.flush();
            generateTone();
        }
    }

    public void frameCycle() {
        if (!dataLineStarted) {
            return;
        }

        if (enabled) {
            line.write(tone, (int) (toneOffset % SAMPLE_RATE), (int) APU.OFFSET_INCREMENT);
            toneOffset = (toneOffset + APU.OFFSET_INCREMENT) % SAMPLE_RATE;
        } else {
            if (toneOffset != 0) {
                line.write(tone, (int) toneOffset, (int) (tone.length - toneOffset));
            }
            toneOffset = 0;
        }
    }

    private float getDutyCycle() {
        switch (duty) {
            case 0:
                return 0.125f;
            case 1:
                return 0.25f;
            case 2:
                return 0.5f;
            default:
                return 0.75f;
        }
    }

    private void generateTone() {
        double frequency = (Display.CYCLES_PER_FRAME * 2 * 60) / (16 * (timer + 1));
        tone = new byte[SAMPLE_RATE];
        for (int i = 0; i < tone.length; i++) {
            tone[i] = ((double) i / (SAMPLE_RATE / frequency)) % 1 < getDutyCycle()
                    ? (byte) (APU.VOLUME * Byte.MAX_VALUE) : 0;
        }
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
        if (lengthCounterTimer != 0 && !(constantVolume == 1)) {
            lengthCounterTimer--;
        }

        if (lengthCounterTimer == 0) {
            enabled = false;
        }
    }

    public boolean getEnabled() {
        return enabled;
    }

    public int getDuty() {
        return duty;
    }

    public int getEnvelopeLoop() {
        return envelopeLoop;
    }

    public int getConstantVolume() {
        return constantVolume;
    }

    public int getVolume() {
        return volume;
    }

    public int getTimer() {
        return timer;
    }

    public int getLengthCounterTimer() {
        return lengthCounterTimer;
    }

    public int getMemoryOffset() {
        return memoryOffset;
    }
}
