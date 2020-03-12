package apu.function;

import static apu.SoundGenerator.SAMPLE_RATE;

public class NoiseFunction implements SoundFunction {
    private static final float DUTY_CYCLE = 0.5f;

    public byte[] getTone(double frequency) {
        byte[] tone = new byte[(int) SAMPLE_RATE];
        for (int i = 0; i < tone.length; i++) {
            tone[i] = (byte) (Math.random() * Byte.MAX_VALUE);
        }
        return tone;
    }
}
