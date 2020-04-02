package apu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PulseChannelTest {
    PulseChannel pulseChannel;

    @BeforeEach
    void runBefore() {
        pulseChannel = new PulseChannel(0);
    }

    @Test
    void testConstructor() {
        assertEquals(0,     pulseChannel.getDuty());
        assertEquals(0,     pulseChannel.getConstantVolume());
        assertEquals(0,     pulseChannel.getEnvelopeLoop());
        assertEquals(0,     pulseChannel.getVolume());
        assertEquals(0,     pulseChannel.getTimer());
        assertEquals(0,     pulseChannel.getLengthCounterTimer());
        assertEquals(false, pulseChannel.getEnabled());
    }

    @Test
    void testWriteMememory$4000NoResetTimer() {
        pulseChannel.writeMemory(Integer.parseInt("4000", 16), Integer.parseInt("11111111", 2));
        assertEquals(15, pulseChannel.getVolume());
        assertEquals(1,  pulseChannel.getConstantVolume());
        assertEquals(1,  pulseChannel.getEnvelopeLoop());
        assertEquals(3,  pulseChannel.getDuty());
    }

    @Test
    void testWriteMememory$4000ResetTimer() {
        pulseChannel.writeMemory(Integer.parseInt("4000", 16), Integer.parseInt("11101111", 2));
        assertEquals(15, pulseChannel.getVolume());
        assertEquals(0,  pulseChannel.getConstantVolume());
        assertEquals(1,  pulseChannel.getEnvelopeLoop());
        assertEquals(3,  pulseChannel.getDuty());
        assertEquals(0,  pulseChannel.getTimer());
    }

    @Test
    void testWriteMemory$4002() {
        pulseChannel.writeMemory(Integer.parseInt("4002", 16), 127);
        assertEquals(127, pulseChannel.getTimer());
    }

    @Test
    void testWriteMemory$4003() {
        pulseChannel.writeMemory(Integer.parseInt("4003", 16), 31);
        assertEquals(true,       pulseChannel.getEnabled());
        assertEquals(7 << 8 + 0, pulseChannel.getTimer());
    }
}
