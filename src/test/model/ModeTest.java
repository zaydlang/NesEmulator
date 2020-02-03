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
        cpu.setMapper(new NRom());
    }

    @Test
    void testImplicit() {
        assertTrue(Mode.runMode("IMPLICIT", new int[0], cpu) == 0);
    }

    @Test
    void testAccumulator() {
        cpu.setRegisterA(0);
        assertTrue(Mode.runMode("ACCUMULATOR", new int[0], cpu) == 0);
        cpu.setRegisterA(47);
        assertTrue(Mode.runMode("ACCUMULATOR", new int[0], cpu) == 47);
        cpu.setRegisterA(123);
        assertTrue(Mode.runMode("ACCUMULATOR", new int[0], cpu) == 123);
        cpu.setRegisterA(255);
        assertTrue(Mode.runMode("ACCUMULATOR", new int[0], cpu) == 255);
    }

    @Test
    void testImmediateNoArgument() {
        assertTrue(Mode.runMode("IMMEDIATE", new int[0], cpu) == 0);
    }

    @Test
    void testImmediateOneArgument() {
        assertTrue(Mode.runMode("IMMEDIATE", new int[] {0},   cpu) == 0);
        assertTrue(Mode.runMode("IMMEDIATE", new int[] {47},  cpu) == 47);
        assertTrue(Mode.runMode("IMMEDIATE", new int[] {123}, cpu) == 123);
        assertTrue(Mode.runMode("IMMEDIATE", new int[] {256}, cpu) == 256);
    }

    @Test
    void testZeroPageZeroArguments() {
        assertTrue(Mode.runMode("ZERO_PAGE", new int[0], cpu) == 0);
    }

    @Test
    void testZeroPageOneArgument() {
        cpu.writeMemory(0,   240);
        assertTrue(Mode.runMode("ZERO_PAGE", new int[] {0},   cpu) == 240);
        cpu.writeMemory(47,  239);
        assertTrue(Mode.runMode("ZERO_PAGE", new int[] {47},  cpu) == 239);
        cpu.writeMemory(123, 238);
        assertTrue(Mode.runMode("ZERO_PAGE", new int[] {123}, cpu) == 238);
        cpu.writeMemory(255, 237);
        assertTrue(Mode.runMode("ZERO_PAGE", new int[] {255}, cpu) == 237);
    }

    @Test
    void testAbsoluteTwoArguments() {
        int argumentOne;
        int argumentTwo;
        int fullArgument;

        argumentOne  = Integer.parseInt("C5", 16);
        argumentTwo  = Integer.parseInt("F5", 16);
        fullArgument = argumentOne + argumentTwo * 256;
        assertTrue(Mode.runMode("ABSOLUTE", new int[] {argumentOne, argumentTwo}, cpu) == fullArgument);

        argumentOne  = Integer.parseInt("A4", 16);
        argumentTwo  = Integer.parseInt("B7", 16);
        fullArgument = argumentOne + argumentTwo * 256;
        assertTrue(Mode.runMode("ABSOLUTE", new int[] {argumentOne, argumentTwo}, cpu) == fullArgument);

        argumentOne  = Integer.parseInt("07", 16);
        argumentTwo  = Integer.parseInt("62", 16);
        fullArgument = argumentOne + argumentTwo * 256;
        assertTrue(Mode.runMode("ABSOLUTE", new int[] {argumentOne, argumentTwo}, cpu) == fullArgument);

        argumentOne  = Integer.parseInt("24", 16);
        argumentTwo  = Integer.parseInt("58", 16);
        fullArgument = argumentOne + argumentTwo * 256;
        assertTrue(Mode.runMode("ABSOLUTE", new int[] {argumentOne, argumentTwo}, cpu) == fullArgument);
    }
}
