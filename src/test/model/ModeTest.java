package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SimplifiableJUnitAssertion")
public class ModeTest {
    CPU cpu;

    @BeforeEach
    void runBefore() {
        cpu = new CPU();
    }

    @Test
    void testImplicit() {
        assertTrue(Mode.runMode("IMPLICIT", 0,   cpu) == 0);
        assertTrue(Mode.runMode("IMPLICIT", 47,  cpu) == 0);
        assertTrue(Mode.runMode("IMPLICIT", 123, cpu) == 0);
        assertTrue(Mode.runMode("IMPLICIT", 255, cpu) == 0);
    }

    @Test
    void testAccumulator() {
        cpu.setRegisterA(0);
        assertTrue(Mode.runMode("ACCUMULATOR", 0, cpu) == 0);
        cpu.setRegisterA(47);
        assertTrue(Mode.runMode("ACCUMULATOR", 0, cpu) == 47);
        cpu.setRegisterA(123);
        assertTrue(Mode.runMode("ACCUMULATOR", 0, cpu) == 123);
        cpu.setRegisterA(255);
        assertTrue(Mode.runMode("ACCUMULATOR", 0, cpu) == 255);
    }

    @Test
    void testImmediate() {
        for (int i = 0; i < 256; i++) {
            assertTrue(Mode.runMode("IMMEDIATE", i, cpu) == i);
        }
    }

    @Test
    void testZeroPage() {
        cpu.writeMemory(0,   240);
        assertTrue(Mode.runMode("ZERO_PAGE", 0,   cpu) == 240);
        cpu.writeMemory(47,  239);
        assertTrue(Mode.runMode("ZERO_PAGE", 47,  cpu) == 239);
        cpu.writeMemory(123, 238);
        assertTrue(Mode.runMode("ZERO_PAGE", 123, cpu) == 238);
        cpu.writeMemory(255, 237);
        assertTrue(Mode.runMode("ZERO_PAGE", 255, cpu) == 237);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void testZeroPageOverflow() {
        cpu.writeMemory(0,   240);
        assertTrue(Mode.runMode("ZERO_PAGE", 0   + 4096, cpu) == 240);
        cpu.writeMemory(47,  239);
        assertTrue(Mode.runMode("ZERO_PAGE", 47  + 4096, cpu) == 239);
        cpu.writeMemory(123, 238);
        assertTrue(Mode.runMode("ZERO_PAGE", 123 + 4096, cpu) == 238);
        cpu.writeMemory(255, 237);
        assertTrue(Mode.runMode("ZERO_PAGE", 255 + 4096, cpu) == 237);
    }

    //@Test
    void testAbsolute() {
        cpu.writeMemory(Integer.parseInt("5000",  16), 240);
        System.out.println(Mode.runMode("ABSOLUTE", Integer.parseInt("5000", 16), cpu));
        System.out.println(cpu.readMemory(Integer.parseInt("5000", 16)));
        assertTrue(Mode.runMode("ABSOLUTE", Integer.parseInt("5000",  16), cpu) == 240);
        cpu.writeMemory(Integer.parseInt("10000", 16), 239);
        assertTrue(Mode.runMode("ABSOLUTE", Integer.parseInt("10000", 16), cpu) == 239);
        cpu.writeMemory(Integer.parseInt("15000", 16), 238);
        assertTrue(Mode.runMode("ABSOLUTE", Integer.parseInt("15000", 16), cpu) == 238);
        cpu.writeMemory(Integer.parseInt("20000", 16), 237);
        assertTrue(Mode.runMode("ABSOLUTE", Integer.parseInt("20000", 16), cpu) == 237);
    }
}
