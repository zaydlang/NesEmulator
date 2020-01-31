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
    void testPushStack() {
        cpu.pushStack(100);
        assertTrue(cpu.peekStack()    == 100);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 1);

        cpu.pushStack(370);
        assertTrue(cpu.peekStack()    == 370);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 2);

        cpu.pushStack(123);
        assertTrue(cpu.peekStack()    == 123);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 3);
    }

    @Test
    void testPullStack() {
        cpu.pushStack(100);
        cpu.pushStack(370);
        cpu.pushStack(123);

        assertTrue(cpu.pullStack()    == 123);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 3);

        assertTrue(cpu.pullStack()    == 370);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 2);

        assertTrue(cpu.pullStack()    == 100);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 1);
    }
}
