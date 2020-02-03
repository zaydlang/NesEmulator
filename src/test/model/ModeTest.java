package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

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
    void testImplicitNoArguments() {
        assertTrue(Mode.runMode("IMPLICIT", new int[0], cpu) == 0);
    }

    @Test
    void testAccumulatorNoArguments() {
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
    void testImmediateNoArguments() {
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

    @Test
    void testIndirectTwoArguments() {
        int argumentOne;
        int argumentTwo;
        int fullArgument;

        argumentOne  = Integer.parseInt("C5", 16);
        argumentTwo  = Integer.parseInt("F5", 16);
        fullArgument = argumentOne + argumentTwo * 256;
        assertTrue(Mode.runMode("INDIRECT", new int[] {argumentOne, argumentTwo}, cpu) == fullArgument);

        argumentOne  = Integer.parseInt("A4", 16);
        argumentTwo  = Integer.parseInt("B7", 16);
        fullArgument = argumentOne + argumentTwo * 256;
        assertTrue(Mode.runMode("INDIRECT", new int[] {argumentOne, argumentTwo}, cpu) == fullArgument);

        argumentOne  = Integer.parseInt("07", 16);
        argumentTwo  = Integer.parseInt("62", 16);
        fullArgument = argumentOne + argumentTwo * 256;
        assertTrue(Mode.runMode("INDIRECT", new int[] {argumentOne, argumentTwo}, cpu) == fullArgument);

        argumentOne  = Integer.parseInt("24", 16);
        argumentTwo  = Integer.parseInt("58", 16);
        fullArgument = argumentOne + argumentTwo * 256;
        assertTrue(Mode.runMode("INDIRECT", new int[] {argumentOne, argumentTwo}, cpu) == fullArgument);
    }

    @Test
    void testRelativePositiveSignOneArgument() {
        int argument      = 43;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC + argument;
        cpu.setRegisterPC(oldRegisterPC);
        assertTrue(Mode.runMode("RELATIVE", new int[] {argument}, cpu) == expectedResult);
    }

    @Test
    void testRelativePositiveSignLowerBoundOneArgument() {
        int argument      = 1;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC + argument;
        cpu.setRegisterPC(oldRegisterPC);
        assertTrue(Mode.runMode("RELATIVE", new int[] {argument}, cpu) == expectedResult);
    }

    @Test
    void testRelativePositiveSignUpperBoundOneArgument() {
        int argument      = 127;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC + argument;
        cpu.setRegisterPC(oldRegisterPC);
        assertTrue(Mode.runMode("RELATIVE", new int[] {argument}, cpu) == expectedResult);
    }

    @Test
    void testRelativeNegativeSignOneArgument() {
        int argument      = 174;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC - (256 - argument);
        cpu.setRegisterPC(oldRegisterPC);
        assertTrue(Mode.runMode("RELATIVE", new int[] {argument}, cpu) == expectedResult);
    }

    @Test
    void testRelativeNegativeSignLowerBoundOneArgument() {
        int argument      = 128;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC - (256 - argument);
        cpu.setRegisterPC(oldRegisterPC);
        assertTrue(Mode.runMode("RELATIVE", new int[] {argument}, cpu) == expectedResult);
    }

    @Test
    void testRelativeNegativeSignUpperBoundOneArgument() {
        int argument      = 255;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC - (256 - argument);
        cpu.setRegisterPC(oldRegisterPC);
        assertTrue(Mode.runMode("RELATIVE", new int[] {argument}, cpu) == expectedResult);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    void testRelativeNoSignOneArgument() {
        int argument      = 0;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC;
        cpu.setRegisterPC(oldRegisterPC);
        assertTrue(Mode.runMode("RELATIVE", new int[] {argument}, cpu) == oldRegisterPC + argument);
    }

    // TODO: there is an absolute indexed x that uses zero arguments, but as that instruction hasn't been fully
    // TODO: implemented yet, i will write that test later. Same goes for absolute indexed y
    // TODO: remove the separation of implementation and declaration maybe? ask in office hours.
    @Test
    void testAbsoluteIndexedXTwoArguments() {
        int argumentOne;
        int argumentTwo;
        int fullArgument;
        int registerX;

        argumentOne  = Integer.parseInt("A4", 16);
        argumentTwo  = Integer.parseInt("B7", 16);
        registerX    = Integer.parseInt("30", 16);

        fullArgument = argumentOne + argumentTwo * 256;
        int expectedResult = registerX + fullArgument;
        int[] arguments = new int[] {argumentOne, argumentTwo};
        cpu.setRegisterX(registerX);
        assertTrue(Mode.runMode("ABSOLUTE_INDEXED_X", arguments, cpu) == expectedResult);
    }

    @Test
    void testAbsoluteIndexedYTwoArguments() {
        int argumentOne;
        int argumentTwo;
        int fullArgument;
        int registerY;

        argumentOne  = Integer.parseInt("A4", 16);
        argumentTwo  = Integer.parseInt("B7", 16);
        registerY    = Integer.parseInt("30", 16);

        fullArgument = argumentOne + argumentTwo * 256;
        int expectedResult = registerY + fullArgument;
        int[] arguments = new int[] {argumentOne, argumentTwo};
        cpu.setRegisterY(registerY);
        assertTrue(Mode.runMode("ABSOLUTE_INDEXED_Y", arguments, cpu) == expectedResult);
    }

    @Test
    void testZeroPageIndexedXOneArgument() {
        int argument  = Integer.parseInt("A4", 16);
        int registerX = Integer.parseInt("30", 16);
        int expectedResult = registerX + argument;
        cpu.setRegisterX(registerX);
        assertTrue(Mode.runMode("ZERO_PAGE_INDEXED_X", new int[] {argument}, cpu) == expectedResult);
    }

    @Test
    void testZeroPageIndexedXOneArgumentOverflow() {
        int argument  = Integer.parseInt("A4", 16);
        int registerX = Integer.parseInt("FE", 16);
        int expectedResult = (registerX + argument) % Integer.parseInt("FF", 16);
        cpu.setRegisterX(registerX);
        assertTrue(Mode.runMode("ZERO_PAGE_INDEXED_X", new int[] {argument}, cpu) == expectedResult);
    }

    @Test
    void testZeroPageIndexedYOneArgument() {
        int argument  = Integer.parseInt("A4", 16);
        int registerY = Integer.parseInt("30", 16);
        int expectedResult = registerY + argument;
        cpu.setRegisterY(registerY);
        assertTrue(Mode.runMode("ZERO_PAGE_INDEXED_Y", new int[] {argument}, cpu) == expectedResult);
    }

    @Test
    void testZeroPageIndexedYOneArgumentOverflow() {
        int argument  = Integer.parseInt("A4", 16);
        int registerY = Integer.parseInt("FE", 16);
        int expectedResult = (registerY + argument) % Integer.parseInt("FF", 16);
        cpu.setRegisterY(registerY);
        assertTrue(Mode.runMode("ZERO_PAGE_INDEXED_Y", new int[] {argument}, cpu) == expectedResult);
    }
}
