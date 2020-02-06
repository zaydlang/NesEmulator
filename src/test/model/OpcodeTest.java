package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SimplifiableJUnitAssertion")
class OpcodeTest {
    CPU cpu;

    @BeforeEach
    void runBefore() {
        cpu = new CPU();
    }

    @Test
    void testAdc() {
    }

    @Test
    void testAndNoFlagsSet() {
        cpu.setRegisterA(Integer.parseInt(                 "11100110", 2));
        Opcode.runOpcode("AND", Integer.parseInt(  "01101110", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01100110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testAsl() {
    }

    @Test
    void testBccBranch() {
        cpu.setFlagC(0);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BCC", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 34);
    }

    @Test
    void testBccNoBranch() {
        cpu.setFlagC(1);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BCC", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 12);
    }

    @Test
    void testBcsBranch() {
        cpu.setFlagC(1);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BCS", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 34);
    }

    @Test
    void testBcsNoBranch() {
        cpu.setFlagC(0);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BCS", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 12);
    }

    @Test
    void testBeqBranch() {
        cpu.setFlagZ(1);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BEQ", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 34);
    }

    @Test
    void testBeqNoBranch() {
        cpu.setFlagZ(0);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BEQ", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 12);
    }

    @Test
    void testBitNoFlags() {
        cpu.setRegisterA(Integer.parseInt("10010101", 2));
        Opcode.runOpcode("BIT", Integer.parseInt("00011110", 2), cpu);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testBitVFlag() {
        cpu.setRegisterA(Integer.parseInt("11010101", 2));
        Opcode.runOpcode("BIT", Integer.parseInt("01011011", 2), cpu);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagV() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testBitNFlag() {
        cpu.setRegisterA(Integer.parseInt("11010101", 2));
        Opcode.runOpcode("BIT", Integer.parseInt("10011011", 2), cpu);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagV() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }



    @Test
    void testBmiBranch() {
        cpu.setFlagN(1);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BMI", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 34);
    }

    @Test
    void testBmiNoBranch() {
        cpu.setFlagN(0);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BMI", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 12);
    }

    @Test
    void testBneBranch() {
        cpu.setFlagZ(0);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BNE", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 34);
    }

    @Test
    void testBneNoBranch() {
        cpu.setFlagZ(1);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BNE", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 12);
    }

    @Test
    void testBplBranch() {
        cpu.setFlagN(0);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BPL", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 34);
    }

    @Test
    void testBplNoBranch() {
        cpu.setFlagN(1);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BPL", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 12);
    }

    @Test
    void testBrk() {
        cpu.setStatus(142);
        cpu.setMapper(new NRom());
        cpu.writeMemory(Integer.parseInt("FFFE", 16), 71);
        cpu.setRegisterPC(47);
        Opcode.runOpcode("BRK", 0, cpu);

        assertTrue(cpu.getRegisterPC() == cpu.readMemory(Integer.parseInt("FFFE", 16)));
        assertTrue(cpu.pullStack()     == 142);
        assertTrue(cpu.pullStack()     == 47);
        assertTrue(cpu.getFlagB()      == 1);
    }

    @Test
    void testBvcBranch() {
        cpu.setFlagV(0);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BVC", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 34);
    }

    @Test
    void testBvcNoBranch() {
        cpu.setFlagV(1);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BVC", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 12);
    }

    @Test
    void testBvsBranch() {
        cpu.setFlagV(1);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BVS", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 34);
    }

    @Test
    void testBvsNoBranch() {
        cpu.setFlagV(0);
        cpu.setRegisterPC(CPU.REGISTER_PC_OFFSET + 12);
        Opcode.runOpcode("BVS", CPU.REGISTER_PC_OFFSET + 34, cpu);

        assertTrue(cpu.getRegisterPC() == CPU.REGISTER_PC_OFFSET + 12);
    }

    @Test
    void testClc() {
        cpu.setFlagC(1);
        Opcode.runOpcode("CLC", 0, cpu);
        assertTrue(cpu.getFlagC() == 0);
    }

    @Test
    void testCld() {
        cpu.setFlagD(1);
        Opcode.runOpcode("CLD", 0, cpu);
        assertTrue(cpu.getFlagD() == 0);
    }

    @Test
    void testCli() {
        cpu.setFlagI(1);
        Opcode.runOpcode("CLI", 0, cpu);
        assertTrue(cpu.getFlagI() == 0);
    }

    @Test
    void testClv() {
        cpu.setFlagV(1);
        Opcode.runOpcode("CLV", 0, cpu);
        assertTrue(cpu.getFlagV() == 0);
    }

    @Test
    void testCmpEqual() {
        cpu.setRegisterA(123);
        Opcode.runOpcode("CMP", 123, cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCmpAccumulatorLess() {
        cpu.setRegisterA(99);
        Opcode.runOpcode("CMP", 123, cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCmpAccumulatorGreater() {
        cpu.setRegisterA(153);
        Opcode.runOpcode("CMP", 123, cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCmpAccumulatorLessBorder() {
        cpu.setRegisterA(122);
        Opcode.runOpcode("CMP", 123, cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCmpAccumulatorGreaterBorder() {
        cpu.setRegisterA(124);
        Opcode.runOpcode("CMP", 123, cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }







    @Test
    void testCpxEqual() {
        cpu.setRegisterX(123);
        Opcode.runOpcode("CPX", 123, cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpxAccumulatorLess() {
        cpu.setRegisterX(99);
        Opcode.runOpcode("CPX", 123, cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCpxAccumulatorGreater() {
        cpu.setRegisterX(153);
        Opcode.runOpcode("CPX", 123, cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpxAccumulatorLessBorder() {
        cpu.setRegisterX(122);
        Opcode.runOpcode("CPX", 123, cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCpxAccumulatorGreaterBorder() {
        cpu.setRegisterX(124);
        Opcode.runOpcode("CPX", 123, cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpyEqual() {
        cpu.setRegisterY(123);
        Opcode.runOpcode("CPY", 123, cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 1);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpyAccumulatorLess() {
        cpu.setRegisterY(99);
        Opcode.runOpcode("CPY", 123, cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCpyAccumulatorGreater() {
        cpu.setRegisterY(153);
        Opcode.runOpcode("CPY", 123, cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testCpyAccumulatorLessBorder() {
        cpu.setRegisterY(122);
        Opcode.runOpcode("CPY", 123, cpu);

        assertTrue(cpu.getFlagC() == 0);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 1);
    }

    @Test
    void testCpyAccumulatorGreaterBorder() {
        cpu.setRegisterY(124);
        Opcode.runOpcode("CPY", 123, cpu);

        assertTrue(cpu.getFlagC() == 1);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testDecNoFlagsSet() {
        cpu.writeMemory(43, 15);
        Opcode.runOpcode("DEC", 43, cpu);
        assertTrue(cpu.readMemory(43) == 14);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testDexNoFlagsSet() {
        cpu.setRegisterX(10);
        Opcode.runOpcode("DEX", 0, cpu);
        assertTrue(cpu.getRegisterX() == 9);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }





    @Test
    void testDeyNoFlagsSet() {
        cpu.setRegisterY(10);
        Opcode.runOpcode("DEY", 0, cpu);
        assertTrue(cpu.getRegisterY() == 9);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }





    @Test
    void testEorNoFlagsSet() {
        cpu.setRegisterA(Integer.parseInt(                 "00100000", 2));
        Opcode.runOpcode("EOR", Integer.parseInt(  "01011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01111111", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testIncNoFlagsSet() {
        cpu.writeMemory(74, 17);
        Opcode.runOpcode("INC", 74, cpu);
        assertTrue(cpu.readMemory(74) == 18);
        assertTrue(cpu.getFlagZ() == 0);
        assertTrue(cpu.getFlagN() == 0);
    }

    @Test
    void testInxNoFlagsSet() {
        cpu.setRegisterX(10);
        Opcode.runOpcode("INX", 0, cpu);
        assertTrue(cpu.getRegisterX() == 11);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testInyNoFlagsSet() {
        cpu.setRegisterY(10);
        Opcode.runOpcode("INY", 0, cpu);
        assertTrue(cpu.getRegisterY() == 11);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testJmp() {
        Opcode.runOpcode("JMP", 234, cpu);
        assertTrue(cpu.getRegisterPC() == CPU.INITIAL_REGISTER_PC + 234 - 3);
    }

    @Test
    void testJsr() {
        Opcode.runOpcode("JSR", 234, cpu);
        assertTrue(cpu.getRegisterPC() == CPU.INITIAL_REGISTER_PC + 234 - 3);
        assertTrue(cpu.peekStack()     == CPU.INITIAL_REGISTER_PC);
    }

    @Test
    void testLdaNoFlagsSet() {
        Opcode.runOpcode("LDA", 43, cpu);
        assertTrue(cpu.getRegisterA() == 43);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testLdxNoFlagsSet() {
        Opcode.runOpcode("LDX", 43, cpu);
        assertTrue(cpu.getRegisterX() == 43);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testLdyNoFlagsSet() {
        Opcode.runOpcode("LDY", 43, cpu);
        assertTrue(cpu.getRegisterY() == 43);
        assertTrue(cpu.getFlagZ()     == 0);
        assertTrue(cpu.getFlagN()     == 0);
    }

    @Test
    void testLsr() {
    }

    @Test
    void testNop() {
        // KEY THING TO NOTE: NOP does not reset a cpu's state to the initial state.
        // it just doesn't affect cpu at all.
        Opcode.runOpcode("NOP", 0, cpu);
        assertTrue(cpu.getRegisterA()  == CPU.INITIAL_REGISTER_A);
        assertTrue(cpu.getRegisterX()  == CPU.INITIAL_REGISTER_X);
        assertTrue(cpu.getRegisterY()  == CPU.INITIAL_REGISTER_Y);
        assertTrue(cpu.getRegisterPC() == CPU.INITIAL_REGISTER_PC);
        assertTrue(cpu.getRegisterP()  == CPU.INITIAL_REGISTER_P);
        assertTrue(cpu.getRegisterS()  == CPU.INITIAL_REGISTER_S);
        assertTrue(cpu.getCycles()     == CPU.INITIAL_CYCLES);
        assertTrue(cpu.peekStack()     == CPU.INITIAL_STACK_STATE);

        for (int i : cpu.ram) {
            assertTrue(i == CPU.INITIAL_RAM_STATE);
        }
    }

    @Test
    void testOraNoFlagsSet() {
        cpu.setRegisterA(Integer.parseInt(                 "00100000", 2));
        Opcode.runOpcode("ORA", Integer.parseInt(  "01011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01111111", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }





    @Test
    void testPhaSingle() {
        cpu.setRegisterA(Integer.parseInt("00010111", 2));
        Opcode.runOpcode("PHA", 0, cpu);
        assertTrue((cpu.peekStack()    == Integer.parseInt("00010111", 2)));
        assertTrue((cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 1));
    }

    @Test
    void testPhaMultiple() {
        cpu.setRegisterA(Integer.parseInt("00010111", 2));
        Opcode.runOpcode("PHA", 0, cpu);
        assertTrue((cpu.peekStack()    == Integer.parseInt("00010111", 2)));
        assertTrue((cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 1));

        cpu.setRegisterA(Integer.parseInt("01010000", 2));
        Opcode.runOpcode("PHA", 0, cpu);
        assertTrue((cpu.peekStack()    == Integer.parseInt("01010000", 2)));
        assertTrue((cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 2));

        cpu.setRegisterA(Integer.parseInt("01000011", 2));
        Opcode.runOpcode("PHA", 0, cpu);
        assertTrue((cpu.peekStack()    == Integer.parseInt("01000011", 2)));
        assertTrue((cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 3));
    }

    @Test
    void testPhp() {
        int testCpuStatus = Integer.parseInt("11010001");
        cpu.setFlagC(Util.getNthBit(testCpuStatus, 0));
        cpu.setFlagZ(Util.getNthBit(testCpuStatus, 1));
        cpu.setFlagI(Util.getNthBit(testCpuStatus, 2));
        cpu.setFlagD(Util.getNthBit(testCpuStatus, 3));
        cpu.setFlagB(Util.getNthBit(testCpuStatus, 4));
        // bit 5 in the flags byte is empty
        cpu.setFlagV(Util.getNthBit(testCpuStatus, 6));
        cpu.setFlagN(Util.getNthBit(testCpuStatus, 7));
        Opcode.runOpcode("PHP", 0, cpu);

        int actualCpuStatus = cpu.peekStack();
        for (int i = 0; i < 8; i++) {
            assertTrue(Util.getNthBit(testCpuStatus, i) == Util.getNthBit(actualCpuStatus, i));
        }
    }

    @Test
    void testPlaSingle() {
        cpu.pushStack(Integer.parseInt("10111001", 2));

        Opcode.runOpcode("PLA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10111001", 2)));
    }

    @Test
    void testPlaMultiple() {
        cpu.pushStack(Integer.parseInt("10111001", 2));
        cpu.pushStack(Integer.parseInt("11010010", 2));

        Opcode.runOpcode("PLA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("11010010", 2)));

        Opcode.runOpcode("PLA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10111001", 2)));
    }

    @Test
    void testPlp() {
        int testCpuStatus = Integer.parseInt("11010001");
        cpu.pushStack(testCpuStatus);
        Opcode.runOpcode("PLP", 0, cpu);

        assertTrue(cpu.getFlagC() == Util.getNthBit(testCpuStatus, 0));
        assertTrue(cpu.getFlagZ() == Util.getNthBit(testCpuStatus, 1));
        assertTrue(cpu.getFlagI() == Util.getNthBit(testCpuStatus, 2));
        assertTrue(cpu.getFlagD() == Util.getNthBit(testCpuStatus, 3));
        assertTrue(cpu.getFlagB() == Util.getNthBit(testCpuStatus, 4));
        // bit 5 in the flags byte is empty
        assertTrue(cpu.getFlagV() == Util.getNthBit(testCpuStatus, 6));
        assertTrue(cpu.getFlagN() == Util.getNthBit(testCpuStatus, 7));
    }

    @Test
    void testRol() {
    }

    @Test
    void testRor() {
    }

    @Test
    void testRti() {
        cpu.pushStack(47);
        cpu.pushStack(142);
        Opcode.runOpcode("RTI", 0, cpu);

        assertTrue(cpu.getRegisterPC() == 47);
        assertTrue(cpu.getStatus()     == 142);
    }

    @Test
    void testRts() {
        cpu.setRegisterPC(18);
        cpu.pushStack(123);
        cpu.pushStack(34);
        Opcode.runOpcode("RTS", 0, cpu);

        assertTrue(cpu.getRegisterPC() == 33);
        assertTrue(cpu.peekStack()     == 123);
    }

    @Test
    void testSbc() {
    }

    @Test
    void testSec() {
        Opcode.runOpcode("SEC", 0, cpu);
        assertTrue(cpu.getFlagC() == 1);
    }

    @Test
    void testSed() {
        Opcode.runOpcode("SED", 0, cpu);
        assertTrue(cpu.getFlagD() == 1);
    }

    @Test
    void testSei() {
        Opcode.runOpcode("SEI", 0, cpu);
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
        Opcode.runOpcode("STA", 47, cpu);
        assertTrue(cpu.readMemory(47) == 123);
    }

    @Test
    void testStp() {
    }

    @Test
    void testStx() {
        cpu.setRegisterX(123);
        Opcode.runOpcode("STX", 47, cpu);
        assertTrue(cpu.readMemory(47) == 123);
    }

    @Test
    void testSty() {
        cpu.setRegisterY(123);
        Opcode.runOpcode("STY", 47, cpu);
        assertTrue(cpu.readMemory(47) == 123);
    }

    @Test
    void testTaxNoFlagsSet() {
        cpu.setRegisterA(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TAX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testTayNoFlagsSet() {
        cpu.setRegisterA(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TAY", 0, cpu);
        assertTrue((cpu.getRegisterY() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testTsxNoFlagsSet() {
        cpu.setRegisterS(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TSX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testTxaNoFlagsSet() {
        cpu.setRegisterX(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testTxs() {
        cpu.setRegisterX(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TXS", 0, cpu);
        assertTrue((cpu.getRegisterS() == Integer.parseInt("01000110", 2)));

        cpu.setRegisterX(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TXS", 0, cpu);
        assertTrue((cpu.getRegisterS() == Integer.parseInt("00000000", 2)));

        cpu.setRegisterX(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TXS", 0, cpu);
        assertTrue((cpu.getRegisterS() == Integer.parseInt("10011001", 2)));
    }

    @Test
    void testTyaNoFlagsSet() {
        cpu.setRegisterY(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TYA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }
}