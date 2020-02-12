package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("SimplifiableJUnitAssertion")
class OpcodeTest {
    CPU cpu;

    @BeforeEach
    void runBefore() {
        cpu = new CPU();
    }

    @Test
    void testAdcNoFlags() {
        cpu.setRegisterA(Integer.parseInt("01011010", 2));
        Opcode.runOpcode("ADC", new Address(Integer.parseInt("00000000", 2)), cpu);
        assertTrue(cpu.getRegisterA().getValue() == Integer.parseInt("01011010", 2));
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testAdcVFlag() {
        cpu.setRegisterA(Integer.parseInt("11111111", 2));
        Opcode.runOpcode("ADC", new Address(Integer.parseInt("10000000", 2)), cpu);
        assertTrue(cpu.getRegisterA().getValue() == Integer.parseInt("01111111", 2));
        assertTrue(cpu.getFlagV() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testAdcZFlag() {
        cpu.setRegisterA(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("ADC", new Address(Integer.parseInt("00000000", 2)), cpu);
        assertTrue(cpu.getRegisterA().getValue() == Integer.parseInt("00000000", 2));
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testAdcCFlag() {
        cpu.setRegisterA(Integer.parseInt("11111111", 2));
        Opcode.runOpcode("ADC", new Address(Integer.parseInt("11111111", 2)), cpu);
        assertTrue(cpu.getRegisterA().getValue() == Integer.parseInt("11111110", 2));
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testAdcNFlag() {
        cpu.setRegisterA(Integer.parseInt("01011010", 2));
        Opcode.runOpcode("ADC", new Address(Integer.parseInt("01000000", 2)), cpu);
        assertTrue(cpu.getRegisterA().getValue() == Integer.parseInt("10011010", 2));
        assertTrue(cpu.getFlagV() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testAndNoFlags() {
        cpu.setRegisterA(Integer.parseInt("11100110", 2));
        Opcode.runOpcode("AND", new Address(Integer.parseInt("01101110", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("01100110", 2)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testAndZFlag() {
        cpu.setRegisterA(Integer.parseInt(                            "00100000", 2));
        Opcode.runOpcode("AND", new Address(Integer.parseInt( "11011111", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testAndNFlag() {
        cpu.setRegisterA(Integer.parseInt(                            "10100000", 2));
        Opcode.runOpcode("AND", new Address(Integer.parseInt( "11011111", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("10000000", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testAslNoFlags() {
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("00010101",  2);
        int expectedValue = Integer.parseInt("00101010", 2);

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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("10010101",  2);
        int expectedValue = Integer.parseInt("00101010", 2);

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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("10000000",  2);
        int expectedValue = Integer.parseInt("00000000", 2);

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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("11010101",  2);
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
        int registerA = Integer.parseInt("10010101", 2);
        int memory    = Integer.parseInt("00011110", 2);
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
        int registerA = Integer.parseInt("11010101", 2);
        int memory    = Integer.parseInt("01011011", 2);
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
        int registerA = Integer.parseInt("11010101", 2);
        int memory    = Integer.parseInt("10011011", 2);
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
        try {
            cpu.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        } catch (IOException e) {
            fail();
        }

        cpu.setStatus(190);
        cpu.setRegisterPC(23 * 256 + 47);
        Opcode.runOpcode("BRK", new Address(0), cpu);

        int byteOne = cpu.readMemory(Integer.parseInt("FFFE", 16)).getValue();
        int byteTwo = cpu.readMemory(Integer.parseInt("FFFF", 16)).getValue();
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
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("01111111", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testEorZFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                            "00100100", 2));
        Opcode.runOpcode("EOR", new Address(Integer.parseInt( "00100100", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testEorNFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                            "11100110", 2));
        Opcode.runOpcode("EOR", new Address(Integer.parseInt( "01101110", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("10001000", 2)));
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
        Opcode.runOpcode("JSR", new Address(0,CPU.INITIAL_REGISTER_PC + 234), cpu);
        assertTrue(cpu.getRegisterPC().getValue() == CPU.INITIAL_REGISTER_PC + 234);

        int byteOne = cpu.pullStack().getValue();
        int byteTwo = cpu.pullStack().getValue();
        assertTrue(byteOne + byteTwo * 256 == CPU.INITIAL_REGISTER_PC - 1);
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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("01010100",  2);
        int expectedValue = Integer.parseInt("00101010", 2);

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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("11010101",  2);
        int expectedValue = Integer.parseInt("01101010", 2);

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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("00000001",  2);
        int expectedValue = Integer.parseInt("00000000", 2);

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
        assertTrue(cpu.getRegisterPC().getValue() == CPU.INITIAL_REGISTER_PC);
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
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("01111111", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testOraZFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                            "00000000", 2));
        Opcode.runOpcode("ORA", new Address(Integer.parseInt( "00000000", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
    }

    @Test
    void testOraNFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                            "11100110", 2));
        Opcode.runOpcode("ORA", new Address(Integer.parseInt( "01101110", 2)), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("11101110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testPhaSingle() {
        cpu.setRegisterA(Integer.parseInt("00010111", 2));
        Opcode.runOpcode("PHA", new Address(0), cpu);
        assertTrue((cpu.peekStack().getValue()    == Integer.parseInt("00010111", 2)));
        assertTrue((cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 1));
    }

    @Test
    void testPhaMultiple() {
        cpu.setRegisterA(Integer.parseInt("00010111", 2));
        Opcode.runOpcode("PHA", new Address(0), cpu);
        assertTrue((cpu.peekStack().getValue()    == Integer.parseInt("00010111", 2)));
        assertTrue((cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 1));

        cpu.setRegisterA(Integer.parseInt("01010000", 2));
        Opcode.runOpcode("PHA", new Address(0), cpu);
        assertTrue((cpu.peekStack().getValue()    == Integer.parseInt("01010000", 2)));
        assertTrue((cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 2));

        cpu.setRegisterA(Integer.parseInt("01000011", 2));
        Opcode.runOpcode("PHA", new Address(0), cpu);
        assertTrue((cpu.peekStack().getValue()    == Integer.parseInt("01000011", 2)));
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
        cpu.pushStack(Integer.parseInt("10111001", 2));

        Opcode.runOpcode("PLA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("10111001", 2)));
    }

    @Test
    void testPlaMultiple() {
        cpu.pushStack(Integer.parseInt("10111001", 2));
        cpu.pushStack(Integer.parseInt("11010010", 2));

        Opcode.runOpcode("PLA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("11010010", 2)));

        Opcode.runOpcode("PLA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("10111001", 2)));
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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("00010101",  2);
        int expectedValue = Integer.parseInt("00101011", 2);
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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("10010101",  2);
        int expectedValue = Integer.parseInt("00101011", 2);
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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("10000000",  2);
        int expectedValue = Integer.parseInt("00000000", 2);
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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("01010101",  2);
        int expectedValue = Integer.parseInt("10101011", 2);
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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("01010100",  2);
        int expectedValue = Integer.parseInt("00101010", 2);
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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("01010111",  2);
        int expectedValue = Integer.parseInt("00101011", 2);
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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("00000001",  2);
        int expectedValue = Integer.parseInt("00000000", 2);
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
        int pointer       = Integer.parseInt("1035", 16);
        int value         = Integer.parseInt("01010101",  2);
        int expectedValue = Integer.parseInt("10101011", 2);
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
        cpu.setRegisterA(Integer.parseInt("01011010", 2));
        cpu.setFlagC(1);

        Opcode.runOpcode("SBC", new Address(Integer.parseInt("00000000", 2)), cpu);
        assertTrue(cpu.getRegisterA().getValue() == Integer.parseInt("01011010", 2));
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testSbcVFlag() {
        cpu.setRegisterA(Integer.parseInt("11111110", 2));
        cpu.setFlagC(1);

        Opcode.runOpcode("SBC", new Address(Integer.parseInt("01111111", 2)), cpu);
        //assertTrue(cpu.getRegisterA().getValue() == Integer.parseInt("1", 2));
        assertTrue(cpu.getFlagV() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testSbcZFlag() {
        cpu.setRegisterA(Integer.parseInt("00000000", 2));
        cpu.setFlagC(1);

        Opcode.runOpcode("SBC", new Address(Integer.parseInt("00000000", 2)), cpu);
        assertTrue(cpu.getRegisterA().getValue() == Integer.parseInt("00000000", 2));
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testSbcCFlag() {
        cpu.setRegisterA(Integer.parseInt("11111111", 2));
        cpu.setFlagC(1);

        Opcode.runOpcode("SBC", new Address(Integer.parseInt("11111111", 2)), cpu);
        assertTrue(cpu.getRegisterA().getValue() == Integer.parseInt("00000000", 2));
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testSbcNFlag() {
        cpu.setRegisterA(Integer.parseInt("11011010", 2));
        cpu.setFlagC(0);

        Opcode.runOpcode("SBC", new Address(Integer.parseInt("00000001", 2)), cpu);
        assertTrue(cpu.getRegisterA().getValue() == Integer.parseInt("11011000", 2));
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
        cpu.setRegisterA( Integer.parseInt("03", 16));
        cpu.setRegisterX( Integer.parseInt("2A", 16));
        cpu.setRegisterY( Integer.parseInt("3D", 16));
        cpu.setRegisterS( Integer.parseInt("E4", 16));
        cpu.setRegisterPC(Integer.parseInt("1F", 16));

        Opcode.runOpcode("STP", new Address(0), cpu);
        assertTrue(cpu.getRegisterA().getValue()  == Integer.parseInt("03", 16));
        assertTrue(cpu.getRegisterX().getValue()  == Integer.parseInt("2A", 16));
        assertTrue(cpu.getRegisterY().getValue()  == Integer.parseInt("3D", 16));
        assertTrue(cpu.getRegisterS().getValue()  == Integer.parseInt("E4", 16));
        assertTrue(cpu.getRegisterPC().getValue() == Integer.parseInt("1F", 16));
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
        cpu.setRegisterA(Integer.parseInt("32", 16));
        cpu.setRegisterX(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TAX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == Integer.parseInt("32", 16)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTaxZFlag() {
        cpu.setRegisterA(Integer.parseInt("00", 16));
        cpu.setRegisterX(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TAX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == Integer.parseInt("00", 16)));
        assertTrue((cpu.getFlagZ() == 1));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTaxNFlag() {
        cpu.setRegisterA(Integer.parseInt("FF", 16));
        cpu.setRegisterX(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TAX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == Integer.parseInt("FF", 16)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 1));
    }

    @Test
    void testTayNoFlags() {
        cpu.setRegisterA(Integer.parseInt("32", 16));
        cpu.setRegisterY(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TAY", new Address(0), cpu);
        assertTrue((cpu.getRegisterY().getValue() == Integer.parseInt("32", 16)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTayZFlag() {
        cpu.setRegisterA(Integer.parseInt("00", 16));
        cpu.setRegisterY(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TAY", new Address(0), cpu);
        assertTrue((cpu.getRegisterY().getValue() == Integer.parseInt("00", 16)));
        assertTrue((cpu.getFlagZ() == 1));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTayNFlag() {
        cpu.setRegisterA(Integer.parseInt("FF", 16));
        cpu.setRegisterY(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TAY", new Address(0), cpu);
        assertTrue((cpu.getRegisterY().getValue() == Integer.parseInt("FF", 16)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 1));
    }

    @Test
    void testTsxNoFlags() {
        cpu.setRegisterS(Integer.parseInt("32", 16));
        cpu.setRegisterX(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TSX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == Integer.parseInt("32", 16)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTsxZFlag() {
        cpu.setRegisterS(Integer.parseInt("00", 16));
        cpu.setRegisterX(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TSX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == Integer.parseInt("00", 16)));
        assertTrue((cpu.getFlagZ() == 1));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTsxNFlag() {
        cpu.setRegisterS(Integer.parseInt("FF", 16));
        cpu.setRegisterX(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TSX", new Address(0), cpu);
        assertTrue((cpu.getRegisterX().getValue() == Integer.parseInt("FF", 16)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 1));
    }

    @Test
    void testTxaNoFlags() {
        cpu.setRegisterX(Integer.parseInt("32", 16));
        cpu.setRegisterA(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TXA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("32", 16)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTxaZFlag() {
        cpu.setRegisterX(Integer.parseInt("00", 16));
        cpu.setRegisterA(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TXA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("00", 16)));
        assertTrue((cpu.getFlagZ() == 1));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTxaNFlag() {
        cpu.setRegisterX(Integer.parseInt("FF", 16));
        cpu.setRegisterA(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TXA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("FF", 16)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 1));
    }

    @Test
    void testTxs() {
        cpu.setRegisterX(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TXS", new Address(0), cpu);
        assertTrue((cpu.getRegisterS().getValue() == Integer.parseInt("01000110", 2)));

        cpu.setRegisterX(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TXS", new Address(0), cpu);
        assertTrue((cpu.getRegisterS().getValue() == Integer.parseInt("00000000", 2)));

        cpu.setRegisterX(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TXS", new Address(0), cpu);
        assertTrue((cpu.getRegisterS().getValue() == Integer.parseInt("10011001", 2)));
    }

    @Test
    void testTyaNoFlags() {
        cpu.setRegisterY(Integer.parseInt("32", 16));
        cpu.setRegisterA(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TYA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("32", 16)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTyaZFlag() {
        cpu.setRegisterY(Integer.parseInt("00", 16));
        cpu.setRegisterA(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TYA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("00", 16)));
        assertTrue((cpu.getFlagZ() == 1));
        assertTrue((cpu.getFlagN() == 0));
    }

    @Test
    void testTyaNFlag() {
        cpu.setRegisterY(Integer.parseInt("FF", 16));
        cpu.setRegisterA(Integer.parseInt("C0", 16));

        Opcode.runOpcode("TYA", new Address(0), cpu);
        assertTrue((cpu.getRegisterA().getValue() == Integer.parseInt("FF", 16)));
        assertTrue((cpu.getFlagZ() == 0));
        assertTrue((cpu.getFlagN() == 1));
    }
}