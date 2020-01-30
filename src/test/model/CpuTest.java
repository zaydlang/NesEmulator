package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CpuTest {
    CPU cpu;

    @BeforeEach
    void runBefore() {
        cpu = new CPU();
    }

    @Test
    void testConstructor() {
        assertTrue(cpu.getRegisterA()  == CPU.INITIAL_REGISTER_A);
        assertTrue(cpu.getRegisterX()  == CPU.INITIAL_REGISTER_X);
        assertTrue(cpu.getRegisterY()  == CPU.INITIAL_REGISTER_Y);
        assertTrue(cpu.getRegisterPC() == CPU.INITIAL_REGISTER_PC);
        assertTrue(cpu.getRegisterP()  == CPU.INITIAL_REGISTER_P);
        assertTrue(cpu.getRegisterS()  == CPU.INITIAL_REGISTER_S);
        assertTrue(cpu.getCycles()     == CPU.INITIAL_CYCLES);
        assertTrue(cpu.peekStack()     == CPU.INITIAL_STACK_STATE);

        for (int i : cpu.getRam()) {
            assertTrue(i == CPU.INITIAL_RAM_STATE);
        }
    }
}
