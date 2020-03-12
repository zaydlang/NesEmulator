package apu.function;

import static apu.SoundGenerator.SAMPLE_RATE;

public class PulseWaveFunction implements SoundFunction {
    private static final float DUTY_CYCLE = 0.5f;

    public byte[] getTone(double frequency) {
        byte[] tone = new byte[(int) SAMPLE_RATE];
        for (int i = 0; i < tone.length; i++) {
            double wrappedX = (i * frequency / SAMPLE_RATE) % 1;
            tone[i] = (wrappedX > DUTY_CYCLE ? Byte.MAX_VALUE : 0);
        }
        return tone;
    }
}
