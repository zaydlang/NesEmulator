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
    void testAndNoFlags() {
        cpu.setRegisterA(Integer.parseInt(                 "11100110", 2));
        Opcode.runOpcode("AND", Integer.parseInt(  "01101110", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01100110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testAndZFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                 "00100000", 2));
        Opcode.runOpcode("AND", Integer.parseInt(  "11011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterA(Integer.parseInt(                 "00100001", 2));
        Opcode.runOpcode("AND", Integer.parseInt(  "11011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000001", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testAndNFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                 "10100000", 2));
        Opcode.runOpcode("AND", Integer.parseInt(  "11011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10000000", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));

        cpu.setRegisterA(Integer.parseInt(                 "00100000", 2));
        Opcode.runOpcode("AND", Integer.parseInt(  "11011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testAsl() {
    }

    @Test
    void testBcc() {
    }

    @Test
    void testBcs() {
    }

    @Test
    void testBeq() {
    }

    @Test
    void testBit() {
    }

    @Test
    void testBmi() {
    }

    @Test
    void testBne() {
    }

    @Test
    void testBpl() {
    }

    @Test
    void testBrk() {
    }

    @Test
    void testBvc() {
    }

    @Test
    void testBvs() {
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
    void testCmp() {
    }

    @Test
    void testCpx() {
    }

    @Test
    void testCpy() {
    }

    @Test
    void testDec() {
    }

    @Test
    void testDex() {
    }

    @Test
    void testDey() {
    }

    @Test
    void testEorNoFlags() {
        cpu.setRegisterA(Integer.parseInt(                 "00100000", 2));
        Opcode.runOpcode("EOR", Integer.parseInt(  "01011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01111111", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testEorZFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                 "00100100", 2));
        Opcode.runOpcode("EOR", Integer.parseInt(  "00100100", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterA(Integer.parseInt(                 "11100110", 2));
        Opcode.runOpcode("EOR", Integer.parseInt(  "01101110", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10001000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testEorNFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                 "11100110", 2));
        Opcode.runOpcode("EOR", Integer.parseInt(  "01101110", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10001000", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));

        cpu.setRegisterA(Integer.parseInt(                 "00100100", 2));
        Opcode.runOpcode("EOR", Integer.parseInt(  "00100100", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testInc() {
    }

    @Test
    void testInx() {
    }

    @Test
    void testIny() {
    }

    @Test
    void testJmp() {
    }

    @Test
    void testJsr() {
    }

    @Test
    void testLda() {
    }

    @Test
    void testLdx() {
    }

    @Test
    void testLdy() {
    }

    @Test
    void testLsr() {
    }

    @Test
    void testNop() {
    }

    @Test
    void testOraNoFlags() {
        cpu.setRegisterA(Integer.parseInt(                 "00100000", 2));
        Opcode.runOpcode("ORA", Integer.parseInt(  "01011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01111111", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testOraZFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                 "00000000", 2));
        Opcode.runOpcode("ORA", Integer.parseInt(  "00000000", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterA(Integer.parseInt(                 "11100110", 2));
        Opcode.runOpcode("ORA", Integer.parseInt(  "01101110", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("11101110", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testOraNFlagSustain() {
        cpu.setRegisterA(Integer.parseInt(                 "11100110", 2));
        Opcode.runOpcode("ORA", Integer.parseInt(  "01101110", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("11101110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));

        cpu.setRegisterA(Integer.parseInt(                 "00000000", 2));
        Opcode.runOpcode("ORA", Integer.parseInt(  "00000000", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testPhaSingle() {
        cpu.setRegisterA(Integer.parseInt("00010111", 2));
        Opcode.runOpcode("PHA", 0, cpu);
        assertTrue((cpu.peekStack() == Integer.parseInt("00010111", 2)));
        assertTrue((cpu.registerS   == CPU.INITIAL_REGISTER_S - 1));
    }

    @Test
    void testPhaMultiple() {
        cpu.setRegisterA(Integer.parseInt("00010111", 2));
        Opcode.runOpcode("PHA", 0, cpu);
        assertTrue((cpu.peekStack() == Integer.parseInt("00010111", 2)));
        assertTrue((cpu.registerS   == CPU.INITIAL_REGISTER_S - 1));

        cpu.setRegisterA(Integer.parseInt("01010000", 2));
        Opcode.runOpcode("PHA", 0, cpu);
        assertTrue((cpu.peekStack() == Integer.parseInt("01010000", 2)));
        assertTrue((cpu.registerS   == CPU.INITIAL_REGISTER_S - 2));

        cpu.setRegisterA(Integer.parseInt("01000011", 2));
        Opcode.runOpcode("PHA", 0, cpu);
        assertTrue((cpu.peekStack() == Integer.parseInt("01000011", 2)));
        assertTrue((cpu.registerS   == CPU.INITIAL_REGISTER_S - 3));
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
        assertTrue((cpu.registerA   == Integer.parseInt("10111001", 2)));
    }

    @Test
    void testPlaMultiple() {
        cpu.pushStack(Integer.parseInt("10111001", 2));
        cpu.pushStack(Integer.parseInt("11010010", 2));

        Opcode.runOpcode("PLA", 0, cpu);
        assertTrue((cpu.registerA   == Integer.parseInt("11010010", 2)));

        Opcode.runOpcode("PLA", 0, cpu);
        assertTrue((cpu.registerA   == Integer.parseInt("10111001", 2)));
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
    }

    @Test
    void testRts() {
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
    }

    @Test
    void testStp() {
    }

    @Test
    void testStx() {
    }

    @Test
    void testSty() {
    }

    @Test
    void testTaxNoFlags() {
        cpu.setRegisterA(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TAX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testTaxZFlagSustain() {
        cpu.setRegisterA(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TAX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterA(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TAX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));

    }

    @Test
    void testTaxNFlagSustain() {
        cpu.setRegisterA(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TAX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));

        cpu.setRegisterA(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TAX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testTayNoFlags() {
        cpu.setRegisterA(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TAY", 0, cpu);
        assertTrue((cpu.getRegisterY() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testTayZFlagSustain() {
        cpu.setRegisterA(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TAY", 0, cpu);
        assertTrue((cpu.getRegisterY() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterA(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TAY", 0, cpu);
        assertTrue((cpu.getRegisterY() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));

    }

    @Test
    void testTayNFlagSustain() {
        cpu.setRegisterA(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TAY", 0, cpu);
        assertTrue((cpu.getRegisterY() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));

        cpu.setRegisterA(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TAY", 0, cpu);
        assertTrue((cpu.getRegisterY() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testTsxNoFlags() {
        cpu.setRegisterS(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TSX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testTsxZFlagSustain() {
        cpu.setRegisterS(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TSX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterS(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TSX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));

    }

    @Test
    void testTsxNFlagSustain() {
        cpu.setRegisterS(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TSX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));

        cpu.setRegisterS(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TSX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testTxaNoFlags() {
        cpu.setRegisterX(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testTxaZFlagSustain() {
        cpu.setRegisterX(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterX(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));

    }

    @Test
    void testTxaNFlagSustain() {
        cpu.setRegisterX(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));

        cpu.setRegisterX(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
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
    void testTyaNoFlags() {
        cpu.setRegisterY(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TYA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));
    }

    @Test
    void testTyaZFlagSustain() {
        cpu.setRegisterY(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TYA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterY(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TYA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));

    }

    @Test
    void testTyaNFlagSustain() {
        cpu.setRegisterY(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TYA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 1));

        cpu.setRegisterY(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TYA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }
}