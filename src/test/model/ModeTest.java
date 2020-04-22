package model;

import mapper.NRom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ppu.Mirroring;

import java.io.IOException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("SimplifiableJUnitAssertion")
public class ModeTest {
    Bus bus;
    CPU cpu;

    @BeforeEach
    void runBefore() {
        try {
            Bus bus = Bus.getInstance();
            cpu = bus.getCpu();
            bus.loadCartridge(new File("data/test/TestLoadRomTrainerPresent.nes"));
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testImplicitNoArguments() {
        Address[] arguments = new Address[] {new Address(0)};
        assertTrue(Mode.runMode("IMPLICIT", arguments, cpu).getValue() == 0);
    }

    @Test
    void testAccumulatorNoArguments() {
        Address[] arguments = new Address[] {new Address(0)};
        cpu.setRegisterA(0);
        assertTrue(Mode.runMode("ACCUMULATOR", arguments, cpu).getValue() == 0);
        cpu.setRegisterA(47);
        assertTrue(Mode.runMode("ACCUMULATOR", arguments, cpu).getValue() == 47);
        cpu.setRegisterA(123);
        assertTrue(Mode.runMode("ACCUMULATOR", arguments, cpu).getValue() == 123);
        cpu.setRegisterA(255);
        assertTrue(Mode.runMode("ACCUMULATOR", arguments, cpu).getValue() == 255);
    }

    @Test
    void testImmediateNoArguments() {
        Address[] arguments = new Address[] {new Address(0)};
        assertTrue(Mode.runMode("IMMEDIATE", arguments, cpu).getValue()  == 0);
    }

    @Test
    void testImmediateOneArgument() {
        Address[] arguments = new Address[] {new Address(0)};
        assertTrue(Mode.runMode("IMMEDIATE", arguments, cpu).getValue()  == 0);
        arguments = new Address[]           {new Address(47)};
        assertTrue(Mode.runMode("IMMEDIATE", arguments, cpu).getValue()  == 47);
        arguments = new Address[]           {new Address(123)};
        assertTrue(Mode.runMode("IMMEDIATE", arguments, cpu).getValue()  == 123);
        arguments = new Address[]           {new Address(255)};
        assertTrue(Mode.runMode("IMMEDIATE", arguments, cpu).getValue() == 255);
    }

    @Test
    void testZeroPageZeroArguments() {
        Address[] arguments = new Address[] {new Address(0)};
        assertTrue(Mode.runMode("ZERO_PAGE", arguments, cpu).getValue() == 0);
    }

    @Test
    void testZeroPageOneArgument() {
        Address[] arguments;

        cpu.writeMemory(0,   240);
        arguments = new Address[] {new Address(0)};
        assertTrue(Mode.runMode("ZERO_PAGE", arguments, cpu).getValue() == 240);
        cpu.writeMemory(47,  239);
        arguments = new Address[] {new Address(47)};
        assertTrue(Mode.runMode("ZERO_PAGE", arguments, cpu).getValue() == 239);
        cpu.writeMemory(123, 238);
        arguments = new Address[] {new Address(123)};
        assertTrue(Mode.runMode("ZERO_PAGE", arguments, cpu).getValue() == 238);
        cpu.writeMemory(255, 237);
        arguments = new Address[] {new Address(255)};
        assertTrue(Mode.runMode("ZERO_PAGE", arguments, cpu).getValue() == 237);
    }

    @Test
    void testAbsoluteTwoArguments() {
        int argumentOne;
        int argumentTwo;
        Address fullArgument;
        Address[] arguments;

        argumentOne  = 0xC5;
        argumentTwo  = 0x65;
        fullArgument = cpu.readMemory(argumentOne + argumentTwo * 256);
        arguments    = new Address[] {new Address(argumentOne), new Address(argumentTwo)};
        assertTrue(Mode.runMode("ABSOLUTE", arguments, cpu).getValue() == fullArgument.getValue());
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    void testIndirectTwoArguments() {
        int argumentOne  = 0xC5;
        int argumentTwo  = 0x60;
        int fullArgument = argumentOne + argumentTwo * 256;

        cpu.writeMemory(fullArgument + 0, 0xA9);
        cpu.writeMemory(fullArgument + 1, 0x60);
        Address expectedResult = cpu.readMemory(0x60A9);

        Address[] arguments = new Address[] {new Address(argumentOne), new Address(argumentTwo)};
        assertTrue(Mode.runMode("INDIRECT", arguments, cpu).getValue() == expectedResult.getValue());
    }

    @Test
    void testIndirectTwoArgumentsBuggyFF() {
        int argumentOne     = 0xFF;
        int argumentTwo     = 0x65;
        int fullArgumentOne = argumentOne + argumentTwo * 256;
        int fullArgumentTwo = fullArgumentOne - 0xFF;

        cpu.writeMemory(fullArgumentOne, 0xA9);
        cpu.writeMemory(fullArgumentTwo, 0x60);
        Address expectedResult = cpu.readMemory(0x60A9);

        Address[] arguments = new Address[] {new Address(argumentOne), new Address(argumentTwo)};
        assertTrue(Mode.runMode("INDIRECT", arguments, cpu).getValue()== expectedResult.getValue());
    }

    @Test
    void testRelativePositiveSignOneArgument() {
        int argument      = 43;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC + argument;
        cpu.setRegisterPC(oldRegisterPC);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("RELATIVE", arguments, cpu).getValue() == expectedResult);
    }

    @Test
    void testRelativePositiveSignLowerBoundOneArgument() {
        int argument      = 1;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC + argument;
        cpu.setRegisterPC(oldRegisterPC);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("RELATIVE", arguments, cpu).getValue() == expectedResult);
    }

    @Test
    void testRelativePositiveSignUpperBoundOneArgument() {
        int argument      = 127;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC + argument;
        cpu.setRegisterPC(oldRegisterPC);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("RELATIVE", arguments, cpu).getValue() == expectedResult);
    }

    @Test
    void testRelativeNegativeSignOneArgument() {
        int argument      = 174;
        int oldRegisterPC = 300;
        int expectedResult = (oldRegisterPC - (256 - argument));

        cpu.setRegisterPC(oldRegisterPC);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("RELATIVE", arguments, cpu).getValue() == expectedResult);
    }

    @Test
    void testRelativeNegativeSignLowerBoundOneArgument() {
        int argument      = 128;
        int oldRegisterPC = 300;
        int expectedResult = (oldRegisterPC - (256 - argument));

        cpu.setRegisterPC(oldRegisterPC);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("RELATIVE", arguments, cpu).getValue() == expectedResult);
    }

    @Test
    void testRelativeNegativeSignUpperBoundOneArgument() {
        int argument      = 255;
        int oldRegisterPC = 300;
        int expectedResult = (oldRegisterPC - (256 - argument));

        cpu.setRegisterPC(oldRegisterPC);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("RELATIVE", arguments, cpu).getValue() == expectedResult);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    void testRelativeNoSignOneArgument() {
        int argument      = 0;
        int oldRegisterPC = 50;

        int expectedResult = oldRegisterPC;
        cpu.setRegisterPC(oldRegisterPC);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("RELATIVE", arguments, cpu).getValue() == oldRegisterPC + argument);
    }

    @Test
    void testAbsoluteIndexedXTwoArguments() {
        int argumentOne;
        int argumentTwo;
        int fullArgument;
        int registerX;

        argumentOne  = 0xA4;
        argumentTwo  = 0x67;
        registerX    = 0x30;

        fullArgument = argumentOne + argumentTwo * 256;
        Address expectedResult = cpu.readMemory(registerX + fullArgument);
        Address[] arguments = new Address[] {new Address(argumentOne), new Address(argumentTwo)};
        cpu.setRegisterX(registerX);
        assertTrue(Mode.runMode("ABSOLUTE_INDEXED_X", arguments, cpu).getValue() == expectedResult.getValue());
    }

    @Test
    void testAbsoluteIndexedYTwoArguments() {
        int argumentOne;
        int argumentTwo;
        int fullArgument;
        int registerY;

        argumentOne  = 0xA4;
        argumentTwo  = 0x67;
        registerY    = 0x30;

        fullArgument = argumentOne + argumentTwo * 256;
        Address expectedResult = cpu.readMemory(registerY + fullArgument);
        Address[] arguments = new Address[] {new Address(argumentOne), new Address(argumentTwo)};
        cpu.setRegisterY(registerY);
        assertTrue(Mode.runMode("ABSOLUTE_INDEXED_Y", arguments, cpu).getValue() == expectedResult.getValue());
    }

    @Test
    void testZeroPageIndexedXOneArgument() {
        int argument  = 0xA4;
        int registerX = 0x30;
        Address expectedResult = cpu.readMemory((registerX + argument) % 0x0100);
        cpu.setRegisterX(registerX);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("ZERO_PAGE_INDEXED_X", arguments, cpu).getPointer() == expectedResult.getPointer());
    }

    @Test
    void testZeroPageIndexedXOneArgumentOverflow() {
        int argument  = 0xA4;
        int registerX = 0xFE;
        Address expectedResult = cpu.readMemory((registerX + argument) % 0x0100);
        cpu.setRegisterX(registerX);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("ZERO_PAGE_INDEXED_X", arguments, cpu).getPointer() == expectedResult.getPointer());
    }

    @Test
    void testZeroPageIndexedYOneArgument() {
        int argument  = 0xA4;
        int registerY = 0x30;
        Address expectedResult = cpu.readMemory((registerY + argument) % 0x0100);
        cpu.setRegisterY(registerY);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("ZERO_PAGE_INDEXED_Y", arguments, cpu).getPointer() == expectedResult.getPointer());
    }

    @Test
    void testZeroPageIndexedYOneArgumentOverflow() {
        int argument  = 0xA4;
        int registerY = 0xFE;
        Address expectedResult = cpu.readMemory((registerY + argument) % 0x0100);
        cpu.setRegisterY(registerY);
        Address[] arguments = new Address[] {new Address(argument)};
        assertTrue(Mode.runMode("ZERO_PAGE_INDEXED_Y", arguments, cpu).getPointer() == expectedResult.getPointer());
    }

    @Test
    void testIndexedIndirectNonZeroRegisterX() {
        int argumentOne = 0xC2;
        int argumentTwo = 0xD3;
        Address[] arguments = new Address[] {new Address(argumentOne), new Address(argumentTwo)};
        cpu.setRegisterX(0x02);

        int pointerOne = (argumentOne + cpu.getRegisterX().getValue()) % 0x0100;
        int pointerTwo = (pointerOne + 1) % 0x0100;
        int fullPointer = cpu.readMemory(pointerOne).getValue() + cpu.readMemory(pointerTwo).getValue() * 256;
        Address expectedResult = cpu.readMemory(fullPointer);

        assertTrue(Mode.runMode("INDEXED_INDIRECT", arguments, cpu).getPointer() == expectedResult.getPointer());
    }

    @Test
    void testIndexedIndirectZeroRegisterX() {
        int argumentOne = 0xC2;
        int argumentTwo = 0xD3;
        Address[] arguments = new Address[] {new Address(argumentOne), new Address(argumentTwo)};
        cpu.setRegisterX(0x00);

        int pointerOne = (argumentOne + cpu.getRegisterX().getValue()) % 0x0100;
        int pointerTwo = (pointerOne + 1) % 0x0100;
        int fullPointer = cpu.readMemory(pointerOne).getValue() + cpu.readMemory(pointerTwo).getValue() * 256;
        Address expectedResult = cpu.readMemory(fullPointer);

        assertTrue(Mode.runMode("INDEXED_INDIRECT", arguments, cpu).getPointer() == expectedResult.getPointer());
    }

    @Test
    void testIndirectIndexedNonZeroRegisterY() {
        int argumentOne = 0xC2;
        int argumentTwo = 0xD3;
        Address[] arguments = new Address[] {new Address(argumentOne), new Address(argumentTwo)};
        cpu.setRegisterY(0x02);

        int pointerOne = cpu.readMemory(argumentOne % 0x0100).getValue();
        int pointerTwo = cpu.readMemory((argumentOne + 1) % 0x0100).getValue();
        int fullPointer = (pointerOne + pointerTwo * 256 + cpu.getRegisterY().getValue());
        Address expectedResult = cpu.readMemory(fullPointer % 0x10000);

        assertTrue(Mode.runMode("INDIRECT_INDEXED", arguments, cpu).getPointer() == expectedResult.getPointer());
    }

    @Test
    void testIndirectIndexedZeroRegisterY() {
        int argumentOne = 0xC2;
        int argumentTwo = 0xD3;
        Address[] arguments = new Address[] {new Address(argumentOne), new Address(argumentTwo)};
        cpu.setRegisterY(0x00);

        int pointerOne = cpu.readMemory(argumentOne % 0x0100).getValue();
        int pointerTwo = cpu.readMemory((argumentOne + 1) % 0x0100).getValue();
        int fullPointer = (pointerOne + pointerTwo * 256 + cpu.getRegisterY().getValue());
        Address expectedResult = cpu.readMemory(fullPointer % 0x10000);

        assertTrue(Mode.runMode("INDIRECT_INDEXED", arguments, cpu).getPointer() == expectedResult.getPointer());
    }
}
