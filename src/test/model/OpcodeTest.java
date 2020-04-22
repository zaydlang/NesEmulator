package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SimplifiableJUnitAssertion")
class OpcodeTest {
    Bus bus;
    CPU cpu;

    @BeforeEach
    void runBefore() {
        try {
            Bus.hardReset();
            bus = Bus.getInstance();
            cpu = bus.getCpu();
            bus.loadCartridge(new File("./data/test/TestLoadRomTrainerPresent.nes"));
        } catch (IOException e) {
            fail("Bus could not load cartridge!");
        }
    }

    @Test
    void testAdcNoFlags() {
        cpu.setRegisterA(0b01011010);
        Opcode.runOpcode("ADC", new Address(0b00000000), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 0b01011010);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testAdcVOldRegisterAAndResultHasMismatchedSign() {
        cpu.setRegisterA(0b11111111);
        Opcode.runOpcode("ADC", new Address(0b01000000), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 0b00111111);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testAdcZFlag() {
        cpu.setRegisterA(0b00000000);
        Opcode.runOpcode("ADC", new Address(0b00000000), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 0b00000000);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testAdcCFlag() {
        cpu.setRegisterA(0b11111111);
        Opcode.runOpcode("ADC", new Address(0b11111111), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 0b11111110);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testAdcNFlag() {
        cpu.setRegisterA(0b01011010);
        Opcode.runOpcode("ADC", new Address(0b01000000), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 0b10011010);
        assertTrue(cpu.getFlagV() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testAndNoFlags() {
        cpu.setRegisterA(0b11100110);
        Opcode.runOpcode("AND", new Address(0b01101110), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b01100110));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testAndZFlag() {
        cpu.setRegisterA(Integer.parseInt(                            "00100000", 2));
        Opcode.runOpcode("AND", new Address(Integer.parseInt( "11011111", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b00000000));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testAndNFlag() {
        cpu.setRegisterA(Integer.parseInt(                            "10100000", 2));
        Opcode.runOpcode("AND", new Address(Integer.parseInt( "11011111", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b10000000));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testAslNoFlags() {
        int pointer       = 0x1035;
        int value         = 0b00010101;
        int expectedValue = 0b00101010;

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ASL", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testAslFlagC() {
        int pointer       = 0x1035;
        int value         = 0b10010101;
        int expectedValue = 0b00101010;

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ASL", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testAslFlagZ() {
        int pointer       = 0x1035;
        int value         = 0b10000000;
        int expectedValue = 0b00000000;

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ASL", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testAslFlagN() {
        int pointer       = 0x1035;
        int value         = 0b11010101;
        int expectedValue = Integer.parseInt( "10101010", 2);

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ASL", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testBccBranch() {
        cpu.setFlagC(0);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BCC", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 34);
    }

    @Test
    void testBccNoBranch() {
        cpu.setFlagC(1);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BCC", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 12);
    }

    @Test
    void testBcsBranch() {
        cpu.setFlagC(1);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BCS", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 34);
    }

    @Test
    void testBcsNoBranch() {
        cpu.setFlagC(0);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BCS", new Address(CPU.OFFSET_REGISTER_PC + 34), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 12);
    }

    @Test
    void testBeqBranch() {
        cpu.setFlagZ(1);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BEQ", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 34);
    }

    @Test
    void testBeqNoBranch() {
        cpu.setFlagZ(0);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BEQ", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 12);
    }

    @Test
    void testBitNoFlags() {
        int registerA = 0b10010101;
        int memory    = 0b00011110;
        int address   = 24;

        cpu.setRegisterA(registerA);
        cpu.writeMemory(address, memory);
        Opcode.runOpcode("BIT", cpu.readMemory(address), cpu);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testBitVFlag() {
        int registerA = 0b11010101;
        int memory    = 0b01011011;
        int address   = 24;

        cpu.setRegisterA(registerA);
        cpu.writeMemory(address, memory);
        Opcode.runOpcode("BIT", cpu.readMemory(address), cpu);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagV() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testBitNFlag() {
        int registerA = 0b11010101;
        int memory    = 0b10011011;
        int address   = 24;

        cpu.setRegisterA(registerA);
        cpu.writeMemory(address, memory);
        Opcode.runOpcode("BIT", cpu.readMemory(address), cpu);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testBmiBranch() {
        cpu.setFlagN(1);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BMI", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 34);
    }

    @Test
    void testBmiNoBranch() {
        cpu.setFlagN(0);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BMI", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 12);
    }

    @Test
    void testBneBranch() {
        cpu.setFlagZ(0);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BNE", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 34);
    }

    @Test
    void testBneNoBranch() {
        cpu.setFlagZ(1);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BNE", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 12);
    }

    @Test
    void testBplBranch() {
        cpu.setFlagN(0);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BPL", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 34);
    }

    @Test
    void testBplNoBranch() {
        cpu.setFlagN(1);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BPL", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 12);
    }

    @Test
    void testBrk() {
        cpu.setStatus(190);
        cpu.setRegisterPC(23 * 256 + 47);
        Opcode.runOpcode("BRK", new Address(0), cpu);

        int byteOne = cpu.readMemory(0xFFFE).getValue();
        int byteTwo = cpu.readMemory(0xFFFF).getValue();
        assertTrue(cpu.getRegisterPC().getValue() == byteOne * 256 + byteTwo);
        assertTrue(cpu.pullStack().getValue()     == 190);
        assertTrue(cpu.pullStack().getValue()     == 23);
        assertTrue(cpu.pullStack().getValue()     == 47 + 3);
        assertTrue(cpu.getFlagB()                 == 1);
    }

    @Test
    void testBvcBranch() {
        cpu.setFlagV(0);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BVC", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 34);
    }

    @Test
    void testBvcNoBranch() {
        cpu.setFlagV(1);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BVC", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 12);
    }

    @Test
    void testBvsBranch() {
        cpu.setFlagV(1);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BVS", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 34);
    }

    @Test
    void testBvsNoBranch() {
        cpu.setFlagV(0);
        cpu.setRegisterPC(CPU.OFFSET_REGISTER_PC + 12);
        Opcode.runOpcode("BVS", new Address(CPU.OFFSET_REGISTER_PC + 34, 0, 0, 65536), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == CPU.OFFSET_REGISTER_PC + 12);
    }

    @Test
    void testClc() {
        cpu.setFlagC(1);
        Opcode.runOpcode("CLC", new Address(0), cpu);
        assertTrue(cpu.getFlagC() == 0);
    }

    @Test
    void testCld() {
        cpu.setFlagD(1);
        Opcode.runOpcode("CLD", new Address(0), cpu);
        assertTrue(cpu.getFlagD() == 0);
    }

    @Test
    void testCli() {
        cpu.setFlagI(1);
        Opcode.runOpcode("CLI", new Address(0), cpu);
        assertTrue(cpu.getFlagI() == 0);
    }

    @Test
    void testClv() {
        cpu.setFlagV(1);
        Opcode.runOpcode("CLV", new Address(0), cpu);
        assertTrue(cpu.getFlagV() == 0);
    }

    @Test
    void testCmpEqual() {
        cpu.setRegisterA(123);
        Opcode.runOpcode("CMP", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCmpAccumulatorLess() {
        cpu.setRegisterA(99);
        Opcode.runOpcode("CMP", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCmpAccumulatorGreater() {
        cpu.setRegisterA(153);
        Opcode.runOpcode("CMP", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCmpAccumulatorLessBorder() {
        cpu.setRegisterA(122);
        Opcode.runOpcode("CMP", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCmpAccumulatorGreaterBorder() {
        cpu.setRegisterA(124);
        Opcode.runOpcode("CMP", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpxEqual() {
        cpu.setRegisterX(123);
        Opcode.runOpcode("CPX", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpxAccumulatorLess() {
        cpu.setRegisterX(99);
        Opcode.runOpcode("CPX", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCpxAccumulatorGreater() {
        cpu.setRegisterX(153);
        Opcode.runOpcode("CPX", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpxAccumulatorLessBorder() {
        cpu.setRegisterX(122);
        Opcode.runOpcode("CPX", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCpxAccumulatorGreaterBorder() {
        cpu.setRegisterX(124);
        Opcode.runOpcode("CPX", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpyEqual() {
        cpu.setRegisterY(123);
        Opcode.runOpcode("CPY", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpyAccumulatorLess() {
        cpu.setRegisterY(99);
        Opcode.runOpcode("CPY", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCpyAccumulatorGreater() {
        cpu.setRegisterY(153);
        Opcode.runOpcode("CPY", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpyAccumulatorLessBorder() {
        cpu.setRegisterY(122);
        Opcode.runOpcode("CPY", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCpyAccumulatorGreaterBorder() {
        cpu.setRegisterY(124);
        Opcode.runOpcode("CPY", new Address(123), cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testDecNoFlags() {
        cpu.writeMemory(43, 15);
        Opcode.runOpcode("DEC", cpu.readMemory(43), cpu);
        assertTrue(cpu.readMemory(43).getValue() == 14);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testDecZFlag() {
        cpu.writeMemory(43, 1);
        Opcode.runOpcode("DEC", cpu.readMemory(43), cpu);
        assertTrue(cpu.readMemory(43).getValue() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testDecNFlag() {
        cpu.writeMemory(43, 0);
        Opcode.runOpcode("DEC", cpu.readMemory(43), cpu);
        assertTrue(cpu.readMemory(43).getValue() == 255);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testDexNoFlags() {
        cpu.setRegisterX(10);
        Opcode.runOpcode("DEX", new Address(0), cpu);
        assertTrue(cpu.getRegisterX().getValue() == 9);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testDexZFlag() {
        cpu.setRegisterX(1);
        Opcode.runOpcode("DEX", new Address(0), cpu);
        assertTrue(cpu.getRegisterX().getValue() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testDexNFlag() {
        cpu.setRegisterX(0);
        Opcode.runOpcode("DEX", new Address(0), cpu);
        assertTrue(cpu.getRegisterX().getValue() == 255);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testDeyNoFlags() {
        cpu.setRegisterY(10);
        Opcode.runOpcode("DEY", new Address(0), cpu);
        assertTrue(cpu.getRegisterY().getValue() == 9);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testDeyZFlag() {
        cpu.setRegisterY(1);
        Opcode.runOpcode("DEY", new Address(0), cpu);
        assertTrue(cpu.getRegisterY().getValue() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testDeyNFlag() {
        cpu.setRegisterY(0);
        Opcode.runOpcode("DEY", new Address(0), cpu);
        assertTrue(cpu.getRegisterY().getValue() == 255);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testEorNoFlags() {
        cpu.setRegisterA(Integer.parseInt(                            "00100000", 2));
        Opcode.runOpcode("EOR", new Address(Integer.parseInt( "01011111", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b01111111));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testEorZFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                            "00100100", 2));
        Opcode.runOpcode("EOR", new Address(Integer.parseInt( "00100100", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b00000000));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testEorNFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                            "11100110", 2));
        Opcode.runOpcode("EOR", new Address(Integer.parseInt( "01101110", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b10001000));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testIncNoFlags() {
        cpu.writeMemory(74, 17);
        Opcode.runOpcode("INC", cpu.readMemory(74), cpu);
        assertTrue(cpu.readMemory(74).getValue() == 18);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testIncZFlag() {
        cpu.writeMemory(74, 255);
        Opcode.runOpcode("INC", cpu.readMemory(74), cpu);
        assertTrue(cpu.readMemory(74).getValue() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testIncNFlag() {
        cpu.writeMemory(74, 127);
        Opcode.runOpcode("INC", cpu.readMemory(74), cpu);
        assertTrue(cpu.readMemory(74).getValue() == 128);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testInxNoFlags() {
        cpu.setRegisterX(10);
        Opcode.runOpcode("INX", new Address(0), cpu);
        assertTrue(cpu.getRegisterX().getValue() == 11);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testInxZFlag() {
        cpu.setRegisterX(255);
        Opcode.runOpcode("INX", new Address(0), cpu);
        assertTrue(cpu.getRegisterX().getValue() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testInxNFlag() {
        cpu.setRegisterX(127);
        Opcode.runOpcode("INX", new Address(0), cpu);
        assertTrue(cpu.getRegisterX().getValue() == 128);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testInyNoFlags() {
        cpu.setRegisterY(10);
        Opcode.runOpcode("INY", new Address(0), cpu);
        assertTrue(cpu.getRegisterY().getValue() == 11);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testInyZFlag() {
        cpu.setRegisterY(255);
        Opcode.runOpcode("INY", new Address(0), cpu);
        assertTrue(cpu.getRegisterY().getValue() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testInyNFlag() {
        cpu.setRegisterY(127);
        Opcode.runOpcode("INY", new Address(0), cpu);
        assertTrue(cpu.getRegisterY().getValue() == 128);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testJmp() {
        Opcode.runOpcode("JMP", new Address(0, CPU.INITIAL_REGISTER_PC + 234), cpu);
        assertTrue(cpu.getRegisterPC().getValue() == CPU.INITIAL_REGISTER_PC + 234);
    }

    @Test
    void testJsr() {
        int oldRegisterPC = cpu.getRegisterPC().getValue();
        Opcode.runOpcode("JSR", new Address(0,CPU.INITIAL_REGISTER_PC + 234), cpu);
        assertTrue(cpu.getRegisterPC().getValue() == CPU.INITIAL_REGISTER_PC + 234);

        int byteOne = cpu.pullStack().getValue();
        int byteTwo = cpu.pullStack().getValue();
        assertEquals(byteOne + byteTwo * 256, oldRegisterPC - 1);
    }

    @Test
    void testLdaNoFlags() {
        Opcode.runOpcode("LDA", new Address(43), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 43);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testLdaZFlag() {
        // Register A starts off as 0.
        cpu.setRegisterA(1);

        Opcode.runOpcode("LDA", new Address(0), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 0);
        assertTrue(cpu.getFlagZ()     == 1);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testLdaNFlag() {
        Opcode.runOpcode("LDA", new Address(128), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 128);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 1);
    }

    @Test
    void testLdxNoFlags() {
        cpu.setRegisterX(1);

        Opcode.runOpcode("LDX", new Address(43), cpu);
        assertTrue(cpu.getRegisterX().getValue() == 43);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testLdxZFlag() {
        cpu.setRegisterX(1);

        Opcode.runOpcode("LDX", new Address(0), cpu);
        assertTrue(cpu.getRegisterX().getValue() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testLdxNFlag() {
        cpu.setRegisterX(1);

        Opcode.runOpcode("LDX", new Address(128), cpu);
        assertTrue(cpu.getRegisterX().getValue() == 128);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testLdyNoFlags() {
        Opcode.runOpcode("LDY", new Address(43), cpu);
        assertTrue(cpu.getRegisterY().getValue() == 43);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testLdyZFlag() {
        cpu.setRegisterY(1);

        Opcode.runOpcode("LDY", new Address(0), cpu);
        assertTrue(cpu.getRegisterY().getValue() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testLdyNFlag() {
        cpu.setRegisterY(1);

        Opcode.runOpcode("LDY", new Address(128), cpu);
        assertTrue(cpu.getRegisterY().getValue() == 128);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testLsrNoFlags() {
        int pointer       = 0x1035;
        int value         = 0b01010100;
        int expectedValue = 0b00101010;

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("LSR", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testLsrFlagC() {
        int pointer       = 0x1035;
        int value         = 0b11010101;
        int expectedValue = 0b01101010;

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("LSR", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testLsrFlagZ() {
        int pointer       = 0x1035;
        int value         = 0b00000001;
        int expectedValue = 0b00000000;

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("LSR", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testNop() {
        // KEY THING TO NOTE: NOP does not reset a cpu's state to the initial state.
        // it just doesn't affect cpu at all.
        Opcode.runOpcode("NOP", new Address(0), cpu);
        assertTrue(cpu.getRegisterA().getValue()  == CPU.INITIAL_REGISTER_A);
        assertTrue(cpu.getRegisterX().getValue()  == CPU.INITIAL_REGISTER_X);
        assertTrue(cpu.getRegisterY().getValue()  == CPU.INITIAL_REGISTER_Y);
        assertTrue(cpu.getRegisterS().getValue()  == CPU.INITIAL_REGISTER_S);
        assertTrue(cpu.getCycles()     == CPU.INITIAL_CYCLES);

        for (Address address : cpu.ram) {
            assertTrue(address.getValue() == CPU.INITIAL_RAM_STATE);
        }
    }

    @Test
    void testOraNoFlags() {
        cpu.setRegisterA(Integer.parseInt(                 "00100000", 2));
        Opcode.runOpcode("ORA", new Address(Integer.parseInt(  "01011111", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b01111111));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testOraZFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                            "00000000", 2));
        Opcode.runOpcode("ORA", new Address(Integer.parseInt( "00000000", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b00000000));
        assertTrue((cpu.getFlagZ()     == 1));
    }

    @Test
    void testOraNFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                            "11100110", 2));
        Opcode.runOpcode("ORA", new Address(Integer.parseInt( "01101110", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b11101110));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testPhaSingle() {
        cpu.setRegisterA(0b00010111);
        Opcode.runOpcode("PHA", new Address(0), cpu);
        assertTrue((cpu.peekStack().getValue()    == 0b00010111));
        assertTrue((cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 1));
    }

    @Test
    void testPhaMultiple() {
        cpu.setRegisterA(0b00010111);
        Opcode.runOpcode("PHA", new Address(0), cpu);
        assertTrue((cpu.peekStack().getValue()    == 0b00010111));
        assertTrue((cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 1));

        cpu.setRegisterA(0b01010000);
        Opcode.runOpcode("PHA", new Address(0), cpu);
        assertTrue((cpu.peekStack().getValue()    == 0b01010000));
        assertTrue((cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 2));

        cpu.setRegisterA(0b01000011);
        Opcode.runOpcode("PHA", new Address(0), cpu);
        assertTrue((cpu.peekStack().getValue()    == 0b01000011));
        assertTrue((cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 3));
    }

    @Test
    void testPhp() {
        int testCpuStatus = Integer.parseInt("11110001");
        cpu.setFlagC(Util.getNthBit(testCpuStatus, 0));
        cpu.setFlagZ(Util.getNthBit(testCpuStatus, 1));
        cpu.setFlagI(Util.getNthBit(testCpuStatus, 2));
        cpu.setFlagD(Util.getNthBit(testCpuStatus, 3));
        // bit 4 in the flags byte is empty
        // bit 5 in the flags byte is empty
        cpu.setFlagV(Util.getNthBit(testCpuStatus, 6));
        cpu.setFlagN(Util.getNthBit(testCpuStatus, 7));
        Opcode.runOpcode("PHP", new Address(0), cpu);

        int actualCpuStatus = cpu.peekStack().getValue();
        for (int i = 0; i < 8; i++) {
            assertTrue(Util.getNthBit(testCpuStatus, i) == Util.getNthBit(actualCpuStatus, i));
        }
    }

    @Test
    void testPlaSingle() {
        cpu.pushStack(0b10111001);

        Opcode.runOpcode("PLA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b10111001));
    }

    @Test
    void testPlaFlagNoFlags() {
        cpu.pushStack(0b00111001);

        Opcode.runOpcode("PLA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b00111001));
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testPlaFlagFlagZ() {
        cpu.pushStack(0b0000000);

        Opcode.runOpcode("PLA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b00000000));
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }


    @Test
    void testPlaFlagFlagN() {
        cpu.pushStack(0b10111001);

        Opcode.runOpcode("PLA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b10111001));
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }


    @Test
    void testPlaMultiple() {
        cpu.pushStack(0b10111001);
        cpu.pushStack(0b11010010);

        Opcode.runOpcode("PLA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b11010010));

        Opcode.runOpcode("PLA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0b10111001));
    }

    @Test
    void testPlp() {
        int testCpuStatus = Integer.parseInt("11010001");
        cpu.pushStack(testCpuStatus);
        Opcode.runOpcode("PLP", new Address(0), cpu);

        assertTrue(cpu.getFlagC() == Util.getNthBit(testCpuStatus, 0));
        assertTrue(cpu.getFlagZ() == Util.getNthBit(testCpuStatus, 1));
        assertTrue(cpu.getFlagI() == Util.getNthBit(testCpuStatus, 2));
        assertTrue(cpu.getFlagD() == Util.getNthBit(testCpuStatus, 3));
        // bit 4 in the flags byte is empty
        // bit 5 in the flags byte is empty
        assertTrue(cpu.getFlagV() == Util.getNthBit(testCpuStatus, 6));
        assertTrue(cpu.getFlagN() == Util.getNthBit(testCpuStatus, 7));
    }

    @Test
    void testRolNoFlags() {
        int pointer       = 0x1035;
        int value         = 0b00010101;
        int expectedValue = 0b00101011;
        cpu.setFlagC(1);

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ROL", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testRolCFlag() {
        int pointer       = 0x1035;
        int value         = 0b10010101;
        int expectedValue = 0b00101011;
        cpu.setFlagC(1);

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ROL", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testRolZFlag() {
        int pointer       = 0x1035;
        int value         = 0b10000000;
        int expectedValue = 0b00000000;
        cpu.setFlagC(0);

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ROL", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testRolNFlag() {
        int pointer       = 0x1035;
        int value         = 0b01010101;
        int expectedValue = 0b10101011;
        cpu.setFlagC(1);

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ROL", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testRorNoFlags() {
        int pointer       = 0x1035;
        int value         = 0b01010100;
        int expectedValue = 0b00101010;
        cpu.setFlagC(0);

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ROR", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testRorCFlag() {
        int pointer       = 0x1035;
        int value         = 0b01010111;
        int expectedValue = 0b00101011;
        cpu.setFlagC(0);

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ROR", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testRorZFlag() {
        int pointer       = 0x1035;
        int value         = 0b00000001;
        int expectedValue = 0b00000000;
        cpu.setFlagC(0);

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ROR", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testRorNFlag() {
        int pointer       = 0x1035;
        int value         = 0b01010101;
        int expectedValue = 0b10101011;
        cpu.setFlagC(1);

        cpu.writeMemory(pointer, value);
        Address argument = cpu.readMemory(pointer);
        Opcode.runOpcode("ROL", argument, cpu);

        assertTrue(argument.getValue() == expectedValue);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testRti() {
        cpu.pushStack(47);
        cpu.pushStack(142);
        cpu.pushStack(245);
        Opcode.runOpcode("RTI", new Address(0), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == 142 +  47 * 256);
        assertTrue(cpu.getStatus()     == 245);
    }

    @Test
    void testRts() {
        cpu.setRegisterPC(18);
        cpu.pushStack(123);
        cpu.pushStack(243);
        cpu.pushStack(34);
        Opcode.runOpcode("RTS", new Address(0), cpu);

        assertTrue(cpu.getRegisterPC().getValue() == 243 * 256 + 34 + 1);
        assertTrue(cpu.peekStack().getValue()     == 123);
    }

    @Test
    void testSbcNoFlags() {
        cpu.setRegisterA(0b01011010);
        cpu.setFlagC(1);

        Opcode.runOpcode("SBC", new Address(0b00000000), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 0b01011010);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testSbcVFlag() {
        cpu.setRegisterA(0b11111110);
        cpu.setFlagC(1);

        Opcode.runOpcode("SBC", new Address(0b01111111), cpu);
        //assertTrue(cpu.getRegisterA().getValue() == 0b1);
        assertTrue(cpu.getFlagV() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testSbcZFlag() {
        cpu.setRegisterA(0b00000000);
        cpu.setFlagC(1);

        Opcode.runOpcode("SBC", new Address(0b00000000), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 0b00000000);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testSbcCFlag() {
        cpu.setRegisterA(0b11111111);
        cpu.setFlagC(1);

        Opcode.runOpcode("SBC", new Address(0b11111111), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 0b00000000);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testSbcNFlag() {
        cpu.setRegisterA(0b11011010);
        cpu.setFlagC(0);

        Opcode.runOpcode("SBC", new Address(0b00000001), cpu);
        assertTrue(cpu.getRegisterA().getValue() == 0b11011000);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testSec() {
        Opcode.runOpcode("SEC", new Address(0), cpu);
        assertTrue(cpu.getFlagC() == 1);
    }

    @Test
    void testSed() {
        Opcode.runOpcode("SED", new Address(0), cpu);
        assertTrue(cpu.getFlagD() == 1);
    }

    @Test
    void testSei() {
        Opcode.runOpcode("SEI", new Address(0), cpu);
        assertTrue(cpu.getFlagI() == 1);
    }

    @Test
    void testShx() {
    }

    @Test
    void testShy() {
    }

    @Test
    void testSta() {
        cpu.setRegisterA(123);
        Opcode.runOpcode("STA", new Address(47), cpu);
        assertTrue(cpu.readMemory(47).getValue() == 123);
    }

    @Test
    void testStp() {
        cpu.setRegisterA( 0x03);
        cpu.setRegisterX( 0x2A);
        cpu.setRegisterY( 0x3D);
        cpu.setRegisterS( 0xE4);
        cpu.setRegisterPC(0x1F);

        Opcode.runOpcode("STP", new Address(0), cpu);
        assertTrue(cpu.getRegisterA().getValue()  == 0x03);
        assertTrue(cpu.getRegisterX().getValue()  == 0x2A);
        assertTrue(cpu.getRegisterY().getValue()  == 0x3D);
        assertTrue(cpu.getRegisterS().getValue()  == 0xE4);
        assertTrue(cpu.getRegisterPC().getValue() == 0x1F);
    }

    @Test
    void testStx() {
        cpu.setRegisterX(123);
        Opcode.runOpcode("STX", new Address(47), cpu);
        assertTrue(cpu.readMemory(47).getValue() == 123);
    }

    @Test
    void testSty() {
        cpu.setRegisterY(123);
        Opcode.runOpcode("STY", new Address(47), cpu);
        assertTrue(cpu.readMemory(47).getValue() == 123);
    }

    @Test
    void testTaxNoFlags() {
        cpu.setRegisterA(0x32);
        cpu.setRegisterX(0xC0);

        Opcode.runOpcode("TAX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == 0x32));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTaxZFlag() {
        cpu.setRegisterA(0x00);
        cpu.setRegisterX(0xC0);

        Opcode.runOpcode("TAX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == 0x00));
        assertTrue((cpu.getFlagZ() == 1));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTaxNFlag() {
        cpu.setRegisterA(0xFF);
        cpu.setRegisterX(0xC0);

        Opcode.runOpcode("TAX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == 0xFF));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 1));
    }

    @Test
    void testTayNoFlags() {
        cpu.setRegisterA(0x32);
        cpu.setRegisterY(0xC0);

        Opcode.runOpcode("TAY", new Address(0), cpu);
        assertTrue((cpu.getRegisterY().getValue() == 0x32));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTayZFlag() {
        cpu.setRegisterA(0x00);
        cpu.setRegisterY(0xC0);

        Opcode.runOpcode("TAY", new Address(0), cpu);
        assertTrue((cpu.getRegisterY().getValue() == 0x00));
        assertTrue((cpu.getFlagZ() == 1));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTayNFlag() {
        cpu.setRegisterA(0xFF);
        cpu.setRegisterY(0xC0);

        Opcode.runOpcode("TAY", new Address(0), cpu);
        assertTrue((cpu.getRegisterY().getValue() == 0xFF));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 1));
    }

    @Test
    void testTsxNoFlags() {
        cpu.setRegisterS(0x32);
        cpu.setRegisterX(0xC0);

        Opcode.runOpcode("TSX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == 0x32));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTsxZFlag() {
        cpu.setRegisterS(0x00);
        cpu.setRegisterX(0xC0);

        Opcode.runOpcode("TSX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == 0x00));
        assertTrue((cpu.getFlagZ() == 1));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTsxNFlag() {
        cpu.setRegisterS(0xFF);
        cpu.setRegisterX(0xC0);

        Opcode.runOpcode("TSX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == 0xFF));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 1));
    }

    @Test
    void testTxaNoFlags() {
        cpu.setRegisterX(0x32);
        cpu.setRegisterA(0xC0);

        Opcode.runOpcode("TXA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0x32));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTxaZFlag() {
        cpu.setRegisterX(0x00);
        cpu.setRegisterA(0xC0);

        Opcode.runOpcode("TXA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0x00));
        assertTrue((cpu.getFlagZ() == 1));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTxaNFlag() {
        cpu.setRegisterX(0xFF);
        cpu.setRegisterA(0xC0);

        Opcode.runOpcode("TXA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0xFF));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 1));
    }

    @Test
    void testTxs() {
        cpu.setRegisterX(0b01000110);
        Opcode.runOpcode("TXS", new Address(0), cpu);
        assertTrue((cpu.getRegisterS().getValue() == 0b01000110));

        cpu.setRegisterX(0b00000000);
        Opcode.runOpcode("TXS", new Address(0), cpu);
        assertTrue((cpu.getRegisterS().getValue() == 0b00000000));

        cpu.setRegisterX(0b10011001);
        Opcode.runOpcode("TXS", new Address(0), cpu);
        assertTrue((cpu.getRegisterS().getValue() == 0b10011001));
    }

    @Test
    void testTyaNoFlags() {
        cpu.setRegisterY(0x32);
        cpu.setRegisterA(0xC0);

        Opcode.runOpcode("TYA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0x32));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTyaZFlag() {
        cpu.setRegisterY(0x00);
        cpu.setRegisterA(0xC0);

        Opcode.runOpcode("TYA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0x00));
        assertTrue((cpu.getFlagZ() == 1));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTyaNFlag() {
        cpu.setRegisterY(0xFF);
        cpu.setRegisterA(0xC0);

        Opcode.runOpcode("TYA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == 0xFF));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 1));
    }
}