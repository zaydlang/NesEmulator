package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OpcodeTest {
    CPU cpu;

    @BeforeEach
    void runBefore() {
        cpu = new CPU();
    }

    @Test
    void testADC() {
    }

    @Test
    void testAND() {
        cpu.setRegisterA(Integer.parseInt(                 "11100110", 2));
        Opcode.runOpcode("AND", Integer.parseInt(  "01101110", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01100110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterA(Integer.parseInt(                 "00100000", 2));
        Opcode.runOpcode("AND", Integer.parseInt(  "11011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterA(Integer.parseInt(                 "10100001", 2));
        Opcode.runOpcode("AND", Integer.parseInt(  "11011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10000001", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }

    @Test
    void testASL() {
    }

    @Test
    void testBCC() {
    }

    @Test
    void testBCS() {
    }

    @Test
    void testBEQ() {
    }

    @Test
    void testBIT() {
    }

    @Test
    void testBMI() {
    }

    @Test
    void testBNE() {
    }

    @Test
    void testBPL() {
    }

    @Test
    void testBRK() {
    }

    @Test
    void testBVC() {
    }

    @Test
    void testBVS() {
    }

    @Test
    void testCLC() {
    }

    @Test
    void testCLD() {
    }

    @Test
    void testCLI() {
    }

    @Test
    void testCLV() {
    }

    @Test
    void testCMP() {
    }

    @Test
    void testCPX() {
    }

    @Test
    void testCPY() {
    }

    @Test
    void testDEC() {
    }

    @Test
    void testDEX() {
    }

    @Test
    void testDEY() {
    }

    @Test
    void testEOR() {
        cpu.setRegisterA(Integer.parseInt(                 "00100000", 2));
        Opcode.runOpcode("EOR", Integer.parseInt(  "01011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01111111", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));

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
    void testINC() {
    }

    @Test
    void testINX() {
    }

    @Test
    void testINY() {
    }

    @Test
    void testJMP() {
    }

    @Test
    void testJSR() {
    }

    @Test
    void testLDA() {
    }

    @Test
    void testLDX() {
    }

    @Test
    void testLDY() {
    }

    @Test
    void testLSR() {
    }

    @Test
    void testNOP() {
    }

    @Test
    void testORA() {
        cpu.setRegisterA(Integer.parseInt(                 "00100000", 2));
        Opcode.runOpcode("ORA", Integer.parseInt(  "01011111", 2), cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01111111", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));

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
    void testPHA() {
    }

    @Test
    void testPHP() {
    }

    @Test
    void testPLA() {
    }

    @Test
    void testPLP() {
    }

    @Test
    void testROL() {
    }

    @Test
    void testROR() {
    }

    @Test
    void testRTI() {
    }

    @Test
    void testRTS() {
    }

    @Test
    void testSBC() {
    }

    @Test
    void testSEC() {
    }

    @Test
    void testSED() {
    }

    @Test
    void testSEI() {
    }

    @Test
    void testSHX() {
    }

    @Test
    void testSHY() {
    }

    @Test
    void testSTA() {
    }

    @Test
    void testSTP() {
    }

    @Test
    void testSTX() {
    }

    @Test
    void testSTY() {
    }

    @Test
    void testTAX() {
        cpu.setRegisterA(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TAX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));

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
    void testTAY() {
        cpu.setRegisterA(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TAY", 0, cpu);
        assertTrue((cpu.getRegisterY() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));

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
    void testTSX() {
        cpu.setRegisterS(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TSX", 0, cpu);
        assertTrue((cpu.getRegisterX() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));

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
    void testTXA() {
        cpu.setRegisterX(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));

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
    void testTXS() {
        cpu.setRegisterX(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterS() == Integer.parseInt("01000110", 2)));

        cpu.setRegisterX(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterS() == Integer.parseInt("00000000", 2)));

        cpu.setRegisterX(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterS() == Integer.parseInt("10011001", 2)));
    }

    @Test
    void testTYA() {
        cpu.setRegisterY(Integer.parseInt("01000110", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("01000110", 2)));
        assertTrue((cpu.getFlagZ()     == 0));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterY(Integer.parseInt("00000000", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("00000000", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 0));

        cpu.setRegisterY(Integer.parseInt("10011001", 2));
        Opcode.runOpcode("TXA", 0, cpu);
        assertTrue((cpu.getRegisterA() == Integer.parseInt("10011001", 2)));
        assertTrue((cpu.getFlagZ()     == 1));
        assertTrue((cpu.getFlagN()     == 1));
    }
}