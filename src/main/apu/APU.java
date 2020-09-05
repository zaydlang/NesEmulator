package apu;

import model.Bus;
import model.Util;
import ui.window.Display;

public class APU {
    public static final float  VOLUME           = 0.05f;
    public static final int    SAMPLE_RATE      = 44100;
    public static final double OFFSET_INCREMENT = SAMPLE_RATE / (double) Display.CYCLING_FPS;

    private PulseChannel    pulseChannel1;
    private PulseChannel    pulseChannel2;

    private int mode;
    // private boolean interruptInhibit;
    private int cycle;

    public APU() {
        pulseChannel1 = new PulseChannel(0);
        pulseChannel2 = new PulseChannel(4);

        cycle = 0;
    }

    // https://wiki.nesdev.com/w/index.php/APU_Frame_Counter
    public void cycle() {
        if (mode == 0) { // 4-Step Sequence
            cycle4StepSequence();
        } else {         // 5-Step Sequence
            cycle5StepSequence();
        }
    }

    // https://wiki.nesdev.com/w/index.php/APU_Frame_Counter
    private void cycle4StepSequence() {
        if (cycle == 7456 || cycle == 14914) {
            cycleLengthCounters();
        }

        cycle++;
        if (cycle == 14915) {
            cycle = 0;
        }
    }

    // https://wiki.nesdev.com/w/index.php/APU_Frame_Counter
    private void cycle5StepSequence() {
        if (cycle == 7456 || cycle == 18640) {
            cycleLengthCounters();
        }

        cycle++;
        if (cycle == 18641) {
            cycle = 0;
        }
    }

    public void startDataLines() {
        pulseChannel1.startDataLine();
        pulseChannel2.startDataLine();
    }

    private void cycleLengthCounters() {
        pulseChannel1.cycleLengthCounter();
        pulseChannel2.cycleLengthCounter();
    }

    public void writeChannelMemory(int pointer, int value) {
        //System.out.println(Integer.toHexString(pointer));
        pulseChannel1.writeMemory(pointer, value);
        pulseChannel2.writeMemory(pointer, value);
    }

    public void writeMemory(int pointer, int value) {
        if        (pointer == 0x4015) {
            pulseChannel2.setEnabled(Util.getNthBit(value,   1) == 1);
            pulseChannel1.setEnabled(Util.getNthBit(value,   0) == 1);
        } else if (pointer == 0x4017) {
            this.mode             = Util.getNthBit(value, 7);
            // this.interruptInhibit = Util.getNthBit(value, 6) == 1;
            cycle = 0;
        }
    }

    public void enable() {
        pulseChannel1.setEnabled(true);
        pulseChannel2.setEnabled(true);
    }

    // EFFECTS: called 60 times per second. advances the APU by one frame forward.
    public void frameCycle() {
        pulseChannel1.frameCycle();
        pulseChannel2.frameCycle();
    }

    public int getCycles() {
        return cycle;
    }

    protected PulseChannel getPulseChannel1() {
        return pulseChannel1;
    }

    protected PulseChannel getPulseChannel2() {
        return pulseChannel2;
    }
}
