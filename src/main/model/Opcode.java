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

    // MODIFIES: cpu.getRegisterA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise AND on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation, not changed if otherwise.
    //          flagN set if registerA is negative after the operation, not changed if otherwise.
    private static OpcodeAction runAND = (int argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterA() & argument);

        cpu.flagZ |= (cpu.getRegisterA() == 0)  ? 1 : 0;
        cpu.flagN |= (cpu.getRegisterA() > 127) ? 1 : 0;
    };

    private static OpcodeAction runASL = (int argument, CPU cpu) -> {
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagC is 0.
    private static OpcodeAction runBCC = (int argument, CPU cpu) -> {
        if (cpu.flagC == 0) {
            cpu.setRegisterPC(cpu.getRegisterPC() + argument);
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagC is 1.
    private static OpcodeAction runBCS = (int argument, CPU cpu) -> {
        if (cpu.flagC == 1) {
            cpu.setRegisterPC(cpu.getRegisterPC() + argument);
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagZ is 1.
    private static OpcodeAction runBEQ = (int argument, CPU cpu) -> {
        if (cpu.flagZ == 1) {
            cpu.setRegisterPC(cpu.getRegisterPC() + argument);
        }
    };

    // MODIFIES: cpu.flagZ, cpu.flagV, cpu.flagN
    // EFFECTS: performs bitwise and on register A and the argument.
    // flagZ is set if the result is 0, not changed if otherwise.
    // flagV is set to the 6th bit of the value in memory using argument as the address.
    // flagN is set to the 7th bit of the value in memory using argument as the address.
    private static OpcodeAction runBIT = (int argument, CPU cpu) -> {
        int result = cpu.getRegisterA() & argument;
        cpu.flagZ |= (result == 0) ? 1 : 0;
        cpu.flagV = Util.getNthBit(argument, 6);
        cpu.flagN = Util.getNthBit(argument, 7);
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagN is 1.
    private static OpcodeAction runBMI = (int argument, CPU cpu) -> {
        if (cpu.flagN == 1) {
            cpu.setRegisterPC(cpu.getRegisterPC() + argument);
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagZ is 0.
    private static OpcodeAction runBNE = (int argument, CPU cpu) -> {
        if (cpu.flagZ == 0) {
            cpu.setRegisterPC(cpu.getRegisterPC() + argument);
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagN is 0.
    private static OpcodeAction runBPL = (int argument, CPU cpu) -> {
        if (cpu.flagN == 0) {
            cpu.setRegisterPC(cpu.getRegisterPC() + argument);
        }
    };

    // MODIFIES: cpu.stack, cpu.registerPC, cpu.flagB
    // EFFECTS: registerPC is pushed onto the stack, followed by the cpu status.
    // then, registerPC is set to the value in memory at address "FFFE" and
    // flagB (the break flag) is set to 1.
    private static OpcodeAction runBRK = (int argument, CPU cpu) -> {
        cpu.pushStack(cpu.getRegisterPC());
        cpu.pushStack(cpu.getStatus());
        cpu.setRegisterPC(cpu.readMemory(Integer.parseInt("FFFE", 16)));
        cpu.setFlagB(1);
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagV is 0.
    private static OpcodeAction runBVC = (int argument, CPU cpu) -> {
        if (cpu.flagV == 0) {
            cpu.setRegisterPC(cpu.getRegisterPC() + argument);
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagV is 1.
    private static OpcodeAction runBVS = (int argument, CPU cpu) -> {
        if (cpu.flagV == 1) {
            cpu.setRegisterPC(cpu.getRegisterPC() + argument);
        }
    };

    // MODIFIES: cpu.flagC
    // EFFECTS: sets flagC to 0
    private static OpcodeAction runCLC = (int argument, CPU cpu) -> {
        cpu.flagC = 0;
    };

    // MODIFIES: cpu.flagD
    // EFFECTS: sets flagD to 0
    private static OpcodeAction runCLD = (int argument, CPU cpu) -> {
        cpu.flagD = 0;
    };

    // MODIFIES: cpu.flagI
    // EFFECTS: sets flagI to 0
    private static OpcodeAction runCLI = (int argument, CPU cpu) -> {
        cpu.flagI = 0;
    };

    // MODIFIES: cpu.flagV
    // EFFECTS: sets flagV to 0
    private static OpcodeAction runCLV = (int argument, CPU cpu) -> {
        cpu.flagV = 0;
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: subtracts the argument from registerA and uses it to set the flags using these rules:
    //          if the result is negative,      flagC is set.
    //          if the result is zero,          flagZ is set.
    //          if the result's 7th bit is set, flagN is set.
    private static OpcodeAction runCMP = (int argument, CPU cpu) -> {
        int result = cpu.getRegisterA() - argument;

        cpu.flagC |= (result > 0)  ? 1 : 0;
        cpu.flagZ |= (result == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(result, 7));
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: subtracts the argument from registerX and uses it to set the flags using these rules:
    //          if the result is negative,      flagC is set.
    //          if the result is zero,          flagZ is set.
    //          if the result's 7th bit is set, flagN is set.
    private static OpcodeAction runCPX = (int argument, CPU cpu) -> {
        int result = cpu.getRegisterX() - argument;

        cpu.flagC |= (result > 0)  ? 1 : 0;
        cpu.flagZ |= (result == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(result, 7));
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: subtracts the argument from registerY and uses it to set the flags using these rules:
    //          if the result is negative,      flagC is set.
    //          if the result is zero,          flagZ is set.
    //          if the result's 7th bit is set, flagN is set.
    private static OpcodeAction runCPY = (int argument, CPU cpu) -> {
        int result = cpu.getRegisterY() - argument;

        cpu.flagC |= (result > 0)  ? 1 : 0;
        cpu.flagZ |= (result == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(result, 7));
    };

    // MODIFIES: cpu's memory
    // EFFECTS: decreases the value in the cpu's memory by one, using argument as an address.
    //          flagZ set if the new value in memory is zero     after the operation, not changed if otherwise.
    //          flagN set if the new value in memory is negative after the operation, not changed if otherwise.
    private static OpcodeAction runDEC = (int argument, CPU cpu) -> {
        int newValue = cpu.readMemory(argument) - 1;
        cpu.writeMemory(argument, newValue);

        cpu.flagZ |= (cpu.readMemory(argument) == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(cpu.readMemory(argument), 7));
    };

    // MODIFIES: cpu.getRegisterX
    // EFFECTS: decreases registerX by one.
    //          flagZ set if registerX is zero,               not changed if otherwise.
    //          flagN set if the 7th bit of registerX is set, not changed if otherwise.
    private static OpcodeAction runDEX = (int argument, CPU cpu) -> {
        cpu.setRegisterX(cpu.getRegisterX() - 1);

        cpu.flagZ |= (cpu.getRegisterX() == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(cpu.getRegisterX(), 7));
    };

    // MODIFIES: cpu.registerY
    // EFFECTS: decreases registerY by one.
    //          flagZ set if registerY is zero,               not changed if otherwise.
    //          flagN set if the 7th bit of registerY is set, not changed if otherwise.
    private static OpcodeAction runDEY = (int argument, CPU cpu) -> {
        cpu.setRegisterY(cpu.getRegisterY() - 1);

        cpu.flagZ |= (cpu.getRegisterY() == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(cpu.getRegisterY(), 7));
    };

    // MODIFIES: cpu.registerA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise XOR on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation, not changed if otherwise.
    //          flagN set if registerA is negative after the operation, not changed if otherwise.
    private static OpcodeAction runEOR = (int argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterA() ^ argument);

        cpu.flagZ |= (cpu.getRegisterA() == 0)  ? 1 : 0;
        cpu.flagN |= (cpu.getRegisterA() > 127) ? 1 : 0;
    };

    // MODIFIES: cpu's memory
    // EFFECTS: increases the value in the cpu's memory by one, using argument as an address.
    //          flagZ set if the new value in memory is zero     after the operation, not changed if otherwise.
    //          flagN set if the new value in memory is negative after the operation, not changed if otherwise.
    private static OpcodeAction runINC = (int argument, CPU cpu) -> {
        int newValue = cpu.readMemory(argument) + 1;
        cpu.writeMemory(argument, newValue);

        cpu.flagZ |= (cpu.readMemory(argument) == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(cpu.readMemory(argument), 7));
    };

    // MODIFIES: cpu.registerX
    // EFFECTS: increases registerX by one.
    //          flagZ set if registerX is zero     after the operation, not changed if otherwise.
    //          flagN set if registerX is negative after the operation, not changed if otherwise.
    private static OpcodeAction runINX = (int argument, CPU cpu) -> {
        cpu.setRegisterX(cpu.getRegisterX() + 1);

        cpu.flagZ |= (cpu.getRegisterX() == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(cpu.getRegisterX(), 7));
    };

    // MODIFIES: cpu.registerY
    // EFFECTS: increases registerY by one.
    //          flagZ set if registerY is zero     after the operation, not changed if otherwise.
    //          flagN set if registerY is negative after the operation, not changed if otherwise.
    private static OpcodeAction runINY = (int argument, CPU cpu) -> {
        cpu.setRegisterY(cpu.getRegisterY() + 1);

        cpu.flagZ |= (cpu.getRegisterY() == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(cpu.getRegisterY(), 7));
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: sets registerPC (the program counter) to the argument specified.
    private static OpcodeAction runJMP = (int argument, CPU cpu) -> {
        cpu.setRegisterPC(argument - CPU.REGISTER_PC_OFFSET);
    };

    // MODIFIES: cpu.registerPC, cpu.stack
    // EFFECTS: pushes the current value of registerPC (the program counter) to the stack, minus one.
    //          then, sets registerPC to the argument specified.
    private static OpcodeAction runJSR = (int argument, CPU cpu) -> {
        cpu.pushStack(cpu.getRegisterPC() - 1);
        cpu.setRegisterPC(argument);
    };

    // MODIFIES: cpu.registerA, flagZ, flagN
    // EFFECTS: sets registerA to the argument
    //          sets flagZ if registerA is 0
    //          sets flagN if the 7th bit of registerA is 1
    private static OpcodeAction runLDA = (int argument, CPU cpu) -> {
        cpu.setRegisterA(argument);

        cpu.flagZ |= (cpu.getRegisterA() == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(cpu.getRegisterA(), 7));
    };

    // MODIFIES: cpu.registerX, flagZ, flagN
    // EFFECTS: sets registerX to the argument
    //          sets flagZ if registerA is 0
    //          sets flagN if the 7th bit of registerA is 1
    private static OpcodeAction runLDX = (int argument, CPU cpu) -> {
        cpu.setRegisterX(argument);

        cpu.flagZ |= (cpu.getRegisterX() == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(cpu.getRegisterX(), 7));
    };

    // MODIFIES: cpu.registerY, flagZ, flagN
    // EFFECTS: sets registerY to the argument
    //          sets flagZ if registerA is 0
    //          sets flagN if the 7th bit of registerA is 1
    private static OpcodeAction runLDY = (int argument, CPU cpu) -> {
        cpu.setRegisterY(argument);

        cpu.flagZ |= (cpu.getRegisterY() == 0) ? 1 : 0;
        cpu.flagN |= (Util.getNthBit(cpu.getRegisterY(), 7));
    };

    private static OpcodeAction runLSR = (int argument, CPU cpu) -> {
    };

    // EFFECTS: doesn't modify the cpu in any way.
    private static OpcodeAction runNOP = (int argument, CPU cpu) -> {

    };

    // MODIFIES: cpu.getRegisterA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise OR on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation, not changed if otherwise.
    //          flagN set if registerA is negative after the operation, not changed if otherwise.
    private static OpcodeAction runORA = (int argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterA() | argument);

        cpu.flagZ |= (cpu.getRegisterA() == 0)  ? 1 : 0;
        cpu.flagN |= (cpu.getRegisterA() > 127) ? 1 : 0;
    };

    // MODIFIES: cpu.getRegisterS, cpu.stack
    // EFFECTS: pushes cpu.getRegisterA onto the cpu stack.
    private static OpcodeAction runPHA = (int argument, CPU cpu) -> {
        cpu.pushStack(cpu.getRegisterA());
    };

    // MODIFIES: cpu.stack
    // EFFECTS: concatenates the cpu flags in this order: CZIDB0VN, where bit 5 is empty. pushes the result onto
    //          the stack.
    private static OpcodeAction runPHP = (int argument, CPU cpu) -> {
        cpu.pushStack(cpu.getStatus());
    };

    // MODIFIES: cpu.getRegisterA cpu.getRegisterS, cpu.stack
    // EFFECTS: pulls from the cpu stack and sets the value to cpu.getRegisterA
    private static OpcodeAction runPLA = (int argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.pullStack());
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu,flagI, cpu.flagD, cpu.flagB, cpu.flagV, cpu.flagN
    // EFFECTS: pulls from the stack. assigns the flags in this order: CZIDB0VN, where bit 5 is empty.
    // for example, if 10010110 was pulled from the stack, cpu.flagC would be 0, cpu.flagZ would be 1, etc.
    private static OpcodeAction runPLP = (int argument, CPU cpu) -> {
        cpu.setStatus(cpu.pullStack());
    };

    private static OpcodeAction runROL = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runROR = (int argument, CPU cpu) -> {
    };

    // MODIFIES: cpu.stack, all 7 cpu flags, cpu.registerPC
    // EFFECTS: the cpu flags are pulled from the stack, then the registerPC is pulled from the stack.
    private static OpcodeAction runRTI = (int argument, CPU cpu) -> {
        cpu.setStatus(cpu.pullStack());
        cpu.setRegisterPC(cpu.pullStack());
    };

    // MODIFIES: cpu.registerPC, cpu.stack
    // EFFECTS: returns from the subroutine by pulling from the stack and setting
    // registerPC to that value (minus one to account for argument length)
    private static OpcodeAction runRTS = (int argument, CPU cpu) -> {
        cpu.setRegisterPC(cpu.pullStack() - 1);
    };

    private static OpcodeAction runSBC = (int argument, CPU cpu) -> {
    };

    // MODIFIES: cpu.flagC
    // EFFECTS: sets flagC to 1
    private static OpcodeAction runSEC = (int argument, CPU cpu) -> {
        cpu.flagC = 1;
    };

    // MODIFIES: cpu.flagD
    // EFFECTS: sets flagD to 1
    private static OpcodeAction runSED = (int argument, CPU cpu) -> {
        cpu.flagD = 1;
    };

    // MODIFIES: cpu.flagI
    // EFFECTS: sets flagI to 1
    private static OpcodeAction runSEI = (int argument, CPU cpu) -> {
        cpu.flagI = 1;
    };

    private static OpcodeAction runSHX = (int argument, CPU cpu) -> {
    };

    private static OpcodeAction runSHY = (int argument, CPU cpu) -> {
    };

    // MODIFIES: cpu's memory
    // EFFECTS: writes cpu.registerA in memory using the argument as the address.
    private static OpcodeAction runSTA = (int argument, CPU cpu) -> {
        cpu.writeMemory(argument, cpu.getRegisterA());
    };

    private static OpcodeAction runSTP = (int argument, CPU cpu) -> {
    };

    // MODIFIES: cpu's memory
    // EFFECTS: writes cpu.registerX in memory using the argument as the address.
    private static OpcodeAction runSTX = (int argument, CPU cpu) -> {
        cpu.writeMemory(argument, cpu.getRegisterX());
    };

    // MODIFIES: cpu's memory
    // EFFECTS: writes cpu.registerY in memory using the argument as the address.
    private static OpcodeAction runSTY = (int argument, CPU cpu) -> {
        cpu.writeMemory(argument, cpu.getRegisterY());
    };

    // MODIFIES: cpu.getRegisterA, cpu.getRegisterX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterA to cpu.getRegisterX
    //          flagZ set if registerX is zero     after the operation, not changed if otherwise.
    //          flagN set if registerX is negative after the operation, not changed if otherwise.
    private static OpcodeAction runTAX = (int argument, CPU cpu) -> {
        cpu.setRegisterX(cpu.getRegisterA());

        cpu.flagZ |= (cpu.getRegisterX() == 0) ? 1 : 0;
        cpu.flagN |= (cpu.getRegisterX() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterA, cpu.getRegisterY, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterA to cpu.getRegisterY
    //          flagZ set if registerY is zero     after the operation, not changed if otherwise.
    //          flagN set if registerY is negative after the operation, not changed if otherwise.
    private static OpcodeAction runTAY = (int argument, CPU cpu) -> {
        cpu.setRegisterY(cpu.getRegisterA());

        cpu.flagZ |= (cpu.getRegisterY() == 0) ? 1 : 0;
        cpu.flagN |= (cpu.getRegisterY() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterS, cpu.getRegisterX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterS to cpu.getRegisterX
    //          flagZ set if registerX is zero     after the operation, not changed if otherwise.
    //          flagN set if registerX is negative after the operation, not changed if otherwise.
    private static OpcodeAction runTSX = (int argument, CPU cpu) -> {
        cpu.setRegisterX(cpu.getRegisterS());

        cpu.flagZ |= (cpu.getRegisterX() == 0) ? 1 : 0;
        cpu.flagN |= (cpu.getRegisterX() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterS, cpu.getRegisterX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterX to cpu.getRegisterA
    //          flagZ set if registerA is zero     after the operation, not changed if otherwise.
    //          flagN set if registerA is negative after the operation, not changed if otherwise.
    private static OpcodeAction runTXA = (int argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterX());

        cpu.flagZ |= (cpu.getRegisterA() == 0) ? 1 : 0;
        cpu.flagN |= (cpu.getRegisterA() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterX, cpu.getRegisterS
    // EFFECTS: transfers cpu.getRegisterX to cpu.getRegisterS
    private static OpcodeAction runTXS = (int argument, CPU cpu) -> {
        cpu.setRegisterS(cpu.getRegisterX());
    };

    // MODIFIES: cpu.getRegisterY, cpu.getRegisterA
    // EFFECTS: transfers cpu.getRegisterY to cpu.getRegisterA
    //          flagZ set if registerA is zero     after the operation, not changed if otherwise.
    //          flagN set if registerA is negative after the operation, not changed if otherwise.
    private static OpcodeAction runTYA = (int argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterY());

        cpu.flagZ |= (cpu.getRegisterY() == 0) ? 1 : 0;
        cpu.flagN |= (cpu.getRegisterY() >> 7) & 1; // is 7th bit set?
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
