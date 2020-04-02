package apu;

import model.Bus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApuTest {
    APU apu;

    @BeforeEach
    void runBefore() {
        apu = new APU(new Bus());
    }

    @Test
    void testConstructor() {
        assertEquals(0, apu.getCycles());
    }

    @Test
    void testCycle4Step() {
        apu.writeMemory(Integer.parseInt("4017", 16), 0 << 7);
        for (int i = 0; i < 14915 + 3; i++) {
            System.out.println(i);
            assertEquals(i % 14915, apu.getCycles());
            apu.cycle();
        }
    }

    @Test
    void testCycle5Step() {
        apu.writeMemory(Integer.parseInt("4017", 16), 1 << 7);
        for (int i = 0; i < 18641 + 3; i++) {
            System.out.println(i);
            assertEquals(i % 18641, apu.getCycles());
            apu.cycle();
        }
    }

    @Test
    void testWriteMemory$4015() {
        apu.writeMemory(Integer.parseInt("4015", 16), 0); // Reset
        apu.writeMemory(Integer.parseInt("4015", 16), Integer.parseInt("00000011", 2));
        assertTrue(apu.getPulseChannel1().getEnabled());
        assertTrue(apu.getPulseChannel2().getEnabled());
    }

    @Test
    void testWriteMemory$4017() {
        apu.writeMemory(Integer.parseInt("4015", 16), 0); // Reset
        apu.writeMemory(Integer.parseInt("4015", 16), Integer.parseInt("00000000", 2));
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
