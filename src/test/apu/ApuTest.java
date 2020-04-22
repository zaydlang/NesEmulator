package apu;

import model.Bus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApuTest {
    APU apu;

    @BeforeEach
    void runBefore() {
        apu = new APU();
    }

    @Test
    void testConstructor() {
        assertEquals(0, apu.getCycles());
    }

    @Test
    void testCycle4Step() {
        apu.writeMemory(0x4017, 0 << 7);
        for (int i = 0; i < 14915 + 3; i++) {
            System.out.println(i);
            assertEquals(i % 14915, apu.getCycles());
            apu.cycle();
        }
    }

    @Test
    void testCycle5Step() {
        apu.writeMemory(0x4017, 1 << 7);
        for (int i = 0; i < 18641 + 3; i++) {
            System.out.println(i);
            assertEquals(i % 18641, apu.getCycles());
            apu.cycle();
        }
    }

    @Test
    void testWriteMemory$4015() {
        apu.writeMemory(0x4015, 0); // Reset
        apu.writeMemory(0x4015, 0b00000011);
        assertTrue(apu.getPulseChannel1().getEnabled());
        assertTrue(apu.getPulseChannel2().getEnabled());
    }

    @Test
    void testWriteMemory$4017() {
        apu.writeMemory(0x4015, 0); // Reset
        apu.writeMemory(0x4015, 0b00000000);
        assertEquals(0, apu.getCycles());
    }

    @Test
    void testFrameCycle() {
        try {
            apu.frameCycle();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testWriteChannelMemory() {

    }
}
