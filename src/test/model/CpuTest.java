package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SimplifiableJUnitAssertion")
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

    @Test
    void testReadMemoryInternalRam() {
        cpu.ram[123] = 157;
        assertTrue(cpu.readMemory(123) == 157);
    }

    @Test
    void testReadMemoryInternalRamMirrors() {
        cpu.ram[123] = 157;
        for (int i = 1; i < 4; i++) {
            int address = 123 + i * Integer.parseInt("0800",16);
            assertTrue(cpu.readMemory(address) == 157);
        }
    }

    @Test
    void testWriteMemoryInternalRam() {
        cpu.writeMemory(123, 157);
        assertTrue(cpu.ram[123] == 157);
    }

    @Test
    void testWriteMemoryInternalRamMirrors() {
        for (int i = 1; i < 4; i++) {
            int address = 123 + i * Integer.parseInt("0800",16) + i;
            cpu.writeMemory(address, 157 + i);
            assertTrue(cpu.ram[123 + i] == 157 + i);
        }
    }

    @Test
    void testWriteMemoryInternalRamOverflow() {
        cpu.writeMemory(123, 157 + 256);
        assertTrue(cpu.ram[123] == 157);
    }

    @Test
    void testWriteMemoryInternalRamMirrorsOverflow() {
        for (int i = 1; i < 4; i++) {
            int address = 123 + i * Integer.parseInt("0800",16) + i;
            cpu.writeMemory(address, 157 + i + 256);
            assertTrue(cpu.ram[123 + i] == 157 + i);
        }
    }

    @Test
    void testWriteMemoryInternalRamUnderflow() {
        cpu.writeMemory(123, 157 - 256);
        assertTrue(cpu.ram[123] == 157);
    }

    @Test
    void testWriteMemoryInternalRamMirrorsUnderflow() {
        for (int i = 1; i < 4; i++) {
            int address = 123 + i * Integer.parseInt("0800",16) + i;
            cpu.writeMemory(address, 157 + i - 256);
            assertTrue(cpu.ram[123 + i] == 157 + i);
        }
    }

    @Test
    void testPushStack() {
        cpu.pushStack(100);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 1);
        assertTrue(cpu.peekStack()    == 100);

        cpu.pushStack(370);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 2);
        assertTrue(cpu.peekStack()    == 370);

        cpu.pushStack(123);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 3);
        assertTrue(cpu.peekStack()    == 123);
    }

    @Test
    void testPullStack() {
        cpu.pushStack(100);
        cpu.pushStack(370);
        cpu.pushStack(123);

        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 3);
        assertTrue(cpu.pullStack()    == 123);

        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 2);
        assertTrue(cpu.pullStack()    == 370);

        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 1);
        assertTrue(cpu.pullStack()    == 100);
    }
}
