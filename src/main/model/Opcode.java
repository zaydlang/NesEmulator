package model;

import java.util.HashMap;

@SuppressWarnings("CodeBlock2Expr")
public class Opcode extends HashMap<String, Opcode.OpcodeAction> {
    private static Opcode opcodes;

    public interface OpcodeAction {
        void run(int argument, CPU cpu);
    }

    // MODIFIES: cpu
    // EFFECTS: Runs the given opcode with the given argument, modifying the CPU flags/registers/RAM as necessary.
    public static void runOpcode(String opcode, int argument, CPU cpu) {
        opcodes.get(opcode).run(argument, cpu);
    }

    private static OpcodeAction runADC = (int argument, CPU cpu) -> {
    };

    // MODIFIES: cpu.registerA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise AND on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation, not changed if otherwise.
    //          flagN set if registerA is negative after the operation, not changed if otherwise.
    private static OpcodeAction runAND = (int argument, CPU cpu) -> {
        cpu.registerA &= argument;

        cpu.flagZ |= (cpu.registerA == 0)  ? 1 : 0;
        cpu.flagN |= (cpu.registerA > 127) ? 1 : 0;
    };

    private static OpcodeAction runASL = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runBCC = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runBCS = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runBEQ = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runBIT = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runBMI = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runBNE = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runBPL = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runBRK = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runBVC = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runBVS = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runCLC = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runCLD = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runCLI = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runCLV = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runCMP = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runCPX = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runCPY = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runDEC = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runDEX = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runDEY = (int argument, CPU cpu) -> {
    };

    // MODIFIES: cpu.registerA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise XOR on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation, not changed if otherwise.
    //          flagN set if registerA is negative after the operation, not changed if otherwise.
    private static OpcodeAction runEOR = (int argument, CPU cpu) -> {
        cpu.registerA ^= argument;

        cpu.flagZ |= (cpu.registerA == 0)  ? 1 : 0;
        cpu.flagN |= (cpu.registerA > 127) ? 1 : 0;
    };

    private static OpcodeAction runINC = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runINX = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runINY = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runJMP = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runJSR = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runLDA = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runLDX = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runLDY = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runLSR = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runNOP = (int argument, CPU cpu) -> {
    };

    // MODIFIES: cpu.registerA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise OR on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation, not changed if otherwise.
    //          flagN set if registerA is negative after the operation, not changed if otherwise.
    private static OpcodeAction runORA = (int argument, CPU cpu) -> {
        cpu.registerA |= argument;

        cpu.flagZ |= (cpu.registerA == 0)  ? 1 : 0;
        cpu.flagN |= (cpu.registerA > 127) ? 1 : 0;
    };

    // MODIFIES: cpu.registerS, cpu.stack
    // EFFECTS: pushes cpu.registerA onto the cpu stack.
    private static OpcodeAction runPHA = (int argument, CPU cpu) -> {
        cpu.pushStack(cpu.registerA);
    };

    private static OpcodeAction runPHP = (int argument, CPU cpu) -> {
        int status =
                  (int) (cpu.getFlagC() * Math.pow(2, 0))
                + (int) (cpu.getFlagZ() * Math.pow(2, 1))
                + (int) (cpu.getFlagI() * Math.pow(2, 2))
                + (int) (cpu.getFlagD() * Math.pow(2, 3))
                + (int) (cpu.getFlagB() * Math.pow(2, 4))
                + (int) (0              * Math.pow(2, 5)) // bit 5 in the flags byte is empty
                + (int) (cpu.getFlagV() * Math.pow(2, 6))
                + (int) (cpu.getFlagN() * Math.pow(2, 7));

        cpu.pushStack(status);
    };

    // MODIFIES: cpu.registerA cpu.registerS, cpu.stack
    // EFFECTS: pulls from the cpu stack and sets the value to cpu.registerA
    private static OpcodeAction runPLA = (int argument, CPU cpu) -> {
        cpu.registerA = cpu.pullStack();
    };

    private static OpcodeAction runPLP = (int argument, CPU cpu) -> {
        int status = cpu.pullStack();
        cpu.setFlagC(Util.getNthBit(status, 0));
        cpu.setFlagZ(Util.getNthBit(status, 1));
        cpu.setFlagI(Util.getNthBit(status, 2));
        cpu.setFlagD(Util.getNthBit(status, 3));
        cpu.setFlagB(Util.getNthBit(status, 4));
        // bit 5 in the flags byte is empty
        cpu.setFlagV(Util.getNthBit(status, 6));
        cpu.setFlagN(Util.getNthBit(status, 7));
    };

    private static OpcodeAction runROL = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runROR = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runRTI = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runRTS = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSBC = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSEC = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSED = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSEI = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSHX = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSHY = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSTA = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSTP = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSTX = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSTY = (int argument, CPU cpu) -> {
    };

    // MODIFIES: cpu.registerA, cpu.registerX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.registerA to cpu.registerX
    //          flagZ set if registerX is zero     after the operation, not changed if otherwise.
    //          flagN set if registerX is negative after the operation, not changed if otherwise.
    private static OpcodeAction runTAX = (int argument, CPU cpu) -> {
        cpu.registerX = cpu.registerA;

        cpu.flagZ |= (cpu.registerX == 0) ? 1 : 0;
        cpu.flagN |= (cpu.registerX >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.registerA, cpu.registerY, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.registerA to cpu.registerY
    //          flagZ set if registerY is zero     after the operation, not changed if otherwise.
    //          flagN set if registerY is negative after the operation, not changed if otherwise.
    private static OpcodeAction runTAY = (int argument, CPU cpu) -> {
        cpu.registerY = cpu.registerA;

        cpu.flagZ |= (cpu.registerY == 0) ? 1 : 0;
        cpu.flagN |= (cpu.registerY >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.registerS, cpu.registerX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.registerS to cpu.registerX
    //          flagZ set if registerX is zero     after the operation, not changed if otherwise.
    //          flagN set if registerX is negative after the operation, not changed if otherwise.
    private static OpcodeAction runTSX = (int argument, CPU cpu) -> {
        cpu.registerX = cpu.registerS;

        cpu.flagZ |= (cpu.registerX == 0) ? 1 : 0;
        cpu.flagN |= (cpu.registerX >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.registerS, cpu.registerX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.registerX to cpu.registerA
    //          flagZ set if registerA is zero     after the operation, not changed if otherwise.
    //          flagN set if registerA is negative after the operation, not changed if otherwise.
    private static OpcodeAction runTXA = (int argument, CPU cpu) -> {
        cpu.registerA = cpu.registerX;

        cpu.flagZ |= (cpu.registerA == 0) ? 1 : 0;
        cpu.flagN |= (cpu.registerA >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.registerX, cpu.registerS
    // EFFECTS: transfers cpu.registerX to cpu.registerS
    private static OpcodeAction runTXS = (int argument, CPU cpu) -> {
        cpu.registerS = cpu.registerX;
    };

    // MODIFIES: cpu.registerY, cpu.registerA
    // EFFECTS: transfers cpu.registerY to cpu.registerA
    //          flagZ set if registerA is zero     after the operation, not changed if otherwise.
    //          flagN set if registerA is negative after the operation, not changed if otherwise.
    private static OpcodeAction runTYA = (int argument, CPU cpu) -> {
        cpu.registerA = cpu.registerY;

        cpu.flagZ |= (cpu.registerY == 0) ? 1 : 0;
        cpu.flagN |= (cpu.registerY >> 7) & 1; // is 7th bit set?
    };

    static {
        opcodes = new Opcode();
        opcodes.put("ADC", runADC);
        opcodes.put("AND", runAND);
        opcodes.put("ASL", runASL);
        opcodes.put("BCC", runBCC);
        opcodes.put("BCS", runBCS);
        opcodes.put("BEQ", runBEQ);
        opcodes.put("BIT", runBIT);
        opcodes.put("BMI", runBMI);
        opcodes.put("BNE", runBNE);
        opcodes.put("BPL", runBPL);
        opcodes.put("BRK", runBRK);
        opcodes.put("BVC", runBVC);
        opcodes.put("BVS", runBVS);
        opcodes.put("CLC", runCLC);
        opcodes.put("CLD", runCLD);
        opcodes.put("CLI", runCLI);
        opcodes.put("CLV", runCLV);
        opcodes.put("CMP", runCMP);
        opcodes.put("CPX", runCPX);
        opcodes.put("CPY", runCPY);
        opcodes.put("DEC", runDEC);
        opcodes.put("DEX", runDEX);
        opcodes.put("DEY", runDEY);
        opcodes.put("EOR", runEOR);
        opcodes.put("INC", runINC);
        opcodes.put("INX", runINX);
        opcodes.put("INY", runINY);
        opcodes.put("JMP", runJMP);
        opcodes.put("JSR", runJSR);
        opcodes.put("LDA", runLDA);
        opcodes.put("LDX", runLDX);
        opcodes.put("LDY", runLDY);
        opcodes.put("LSR", runLSR);
        opcodes.put("NOP", runNOP);
        opcodes.put("ORA", runORA);
        opcodes.put("PHA", runPHA);
        opcodes.put("PHP", runPHP);
        opcodes.put("PLA", runPLA);
        opcodes.put("PLP", runPLP);
        opcodes.put("ROL", runROL);
        opcodes.put("ROR", runROR);
        opcodes.put("RTI", runRTI);
        opcodes.put("RTS", runRTS);
        opcodes.put("SBC", runSBC);
        opcodes.put("SEC", runSEC);
        opcodes.put("SED", runSED);
        opcodes.put("SEI", runSEI);
        opcodes.put("SHX", runSHX);
        opcodes.put("SHY", runSHY);
        opcodes.put("STA", runSTA);
        opcodes.put("STP", runSTP);
        opcodes.put("STX", runSTX);
        opcodes.put("STY", runSTY);
        opcodes.put("TAX", runTAX);
        opcodes.put("TAY", runTAY);
        opcodes.put("TSX", runTSX);
        opcodes.put("TXA", runTXA);
        opcodes.put("TXS", runTXS);
        opcodes.put("TYA", runTYA);
    }
}
