package apu;

import apu.function.NoiseFunction;
import apu.function.PulseWaveFunction;
import apu.function.TriangleWaveFunction;

public class APU {
    public static void main(String[] args) {
        SoundGenerator soundGenerator = new SoundGenerator(new NoiseFunction(), 60);
        soundGenerator.setPlayingSound(true);
        soundGenerator.setFrequency(440);
        new Thread(soundGenerator).start();
    }
}
