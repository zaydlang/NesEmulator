package apu.function;

import static apu.SoundGenerator.SAMPLE_RATE;

public class TriangleWaveFunction implements SoundFunction {
    private static final float DUTY_CYCLE = 0.5f;

    public byte[] getTone(double frequency) {
        byte[] tone = new byte[(int) SAMPLE_RATE];
        for (int i = 0; i < tone.length; i++) {
            double wrappedX = (i * frequency / SAMPLE_RATE) % 1;
            tone[i] = (byte) (Math.abs(wrappedX - 0.5) * Byte.MAX_VALUE);
        }
        return tone;
    }
}
