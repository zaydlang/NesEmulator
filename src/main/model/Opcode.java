package model;

import util.Util;

import java.util.HashMap;

// Class Opcode:
//     Opcode models
@SuppressWarnings("CodeBlock2Expr")
public class Opcode extends HashMap<String, Opcode.OpcodeAction> {
    private static Opcode opcodes;

    public interface OpcodeAction {
        void run(Address argument, CPU cpu);
    }

    // MODIFIES: cpu
    // EFFECTS: Runs the given opcode with the given argument, modifying the CPU flags/registers/RAM as necessary.
    public static void runOpcode(String opcode, Address argument, CPU cpu) {
        opcodes.get(opcode).run(argument, cpu);
    }

    // MODIFIES: cpu.registerA, cpu.flagV, cpu.flagZ, cpu.flagC, cpu.flagN
    // EFFECTS: adds the argument to cpu.registerA, and sets the flags according to these rules:
    //          flagV: if the sign is incorrect.
    //          flagZ: if the result is zero.
    //          flagC: if there was overflow.
    //          flagN: if the result is negative.
    private static OpcodeAction runADC = (Address argument, CPU cpu) -> {
        int oldRegisterA = cpu.getRegisterA().getValue();
        int newValueRaw  = cpu.getRegisterA().getValue() + argument.getValue() + cpu.getFlagC();
        cpu.setRegisterA(newValueRaw);

        int oldRegisterASign = Util.getNthBit(oldRegisterA,       7);
        int registerASign    = Util.getNthBit(cpu.getRegisterA().getValue(), 7);
        int argumentSign     = Util.getNthBit(argument.getValue(),7);
        cpu.flagV = (oldRegisterASign == argumentSign && oldRegisterASign != registerASign) ? 1 : 0;
        cpu.flagZ = (cpu.getRegisterA().getValue() == 0)           ? 1 : 0;
        cpu.flagC = (newValueRaw > cpu.getRegisterA().getValue())  ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterA().getValue(), 7));
    };

    // MODIFIES: cpu.getRegisterA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise AND on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation.
    //          flagN set if registerA is negative after the operation.
    private static OpcodeAction runAND = (Address argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterA().getValue() & argument.getValue());

        cpu.flagZ = (cpu.getRegisterA().getValue() == 0)  ? 1 : 0;
        cpu.flagN = (cpu.getRegisterA().getValue() > 127) ? 1 : 0;
    };

    // MODIFIES: the argument, cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: performs an arithmetic shift leftwards on the argument. Sets the flags according to these rules:
    //          flagC set if to the lost bit in the argument (bit 7)
    //          flagZ set if the result is zero
    //          flagN set if the result is negative.
    private static OpcodeAction runASL = (Address argument, CPU cpu) -> {
        int oldValue = argument.getValue();
        argument.setValue(argument.getValue() * 2);

        cpu.flagC = Util.getNthBit(oldValue, 7);
        cpu.flagZ = (argument.getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(argument.getValue(), 7));
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagC is 0.
    private static OpcodeAction runBCC = (Address argument, CPU cpu) -> {
        if (cpu.flagC == 0) {
            cpu.incrementCycles(3);
            cpu.setRegisterPC(argument.getValue());
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagC is 1.
    private static OpcodeAction runBCS = (Address argument, CPU cpu) -> {
        if (cpu.flagC == 1) {
            cpu.incrementCycles(3);
            cpu.setRegisterPC(argument.getValue());
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagZ is 1.
    private static OpcodeAction runBEQ = (Address argument, CPU cpu) -> {
        if (cpu.flagZ == 1) {
            cpu.incrementCycles(3);
            cpu.setRegisterPC(argument.getValue());
        }
    };

    // MODIFIES: cpu.flagZ, cpu.flagV, cpu.flagN
    // EFFECTS: performs bitwise and on register A and the argument.
    // flagZ is set if the result is 0.
    // flagV is set to the 6th bit of the value in memory using argument as the address.
    // flagN is set to the 7th bit of the value in memory using argument as the address.
    private static OpcodeAction runBIT = (Address argument, CPU cpu) -> {
        int result = cpu.getRegisterA().getValue() & argument.getValue();
        cpu.flagZ = (result == 0) ? 1 : 0;
        cpu.flagV = Util.getNthBit(argument.getValue(), 6);
        cpu.flagN = Util.getNthBit(argument.getValue(), 7);
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagN is 1.
    private static OpcodeAction runBMI = (Address argument, CPU cpu) -> {
        if (cpu.flagN == 1) {
            cpu.incrementCycles(3);
            cpu.setRegisterPC(argument.getValue());
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagZ is 0.
    private static OpcodeAction runBNE = (Address argument, CPU cpu) -> {
        if (cpu.flagZ == 0) {
            cpu.incrementCycles(3);
            cpu.setRegisterPC(argument.getValue());
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagN is 0.
    private static OpcodeAction runBPL = (Address argument, CPU cpu) -> {
        if (cpu.flagN == 0) {
            cpu.incrementCycles(3);
            cpu.setRegisterPC(argument.getValue());
        }
    };

    // MODIFIES: cpu.stack, cpu.registerPC, cpu.flagB
    // EFFECTS: registerPC is pushed onto the stack, followed by the cpu status.
    // then, registerPC is set to the value in memory at address "FFFE" and
    // flagB (the break flag) is set to 1.
    private static OpcodeAction runBRK = (Address argument, CPU cpu) -> {
        int byteOne = ((cpu.getRegisterPC().getValue() + 3) & Integer.parseInt("0000000011111111", 2));
        int byteTwo = ((cpu.getRegisterPC().getValue() + 3) & Integer.parseInt("1111111100000000", 2)) >> 8;
        cpu.pushStack(byteOne);
        cpu.pushStack(byteTwo);
        cpu.pushStack(cpu.getStatus());

        byteOne = cpu.readMemory(Integer.parseInt("FFFE", 16)).getValue();
        byteTwo = cpu.readMemory(Integer.parseInt("FFFF", 16)).getValue();
        cpu.setRegisterPC(byteOne * 256 + byteTwo);
        cpu.setFlagB(1);
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagV is 0.
    private static OpcodeAction runBVC = (Address argument, CPU cpu) -> {
        if (cpu.flagV == 0) {
            cpu.incrementCycles(3);
            cpu.setRegisterPC(argument.getValue());
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagV is 1.
    private static OpcodeAction runBVS = (Address argument, CPU cpu) -> {
        if (cpu.flagV == 1) {
            cpu.incrementCycles(3);
            cpu.setRegisterPC(argument.getValue());
        }
    };

    // MODIFIES: cpu.flagC
    // EFFECTS: sets flagC to 0
    private static OpcodeAction runCLC = (Address argument, CPU cpu) -> {
        cpu.flagC = 0;
    };

    // MODIFIES: cpu.flagD
    // EFFECTS: sets flagD to 0
    private static OpcodeAction runCLD = (Address argument, CPU cpu) -> {
        cpu.flagD = 0;
    };

    // MODIFIES: cpu.flagI
    // EFFECTS: sets flagI to 0
    private static OpcodeAction runCLI = (Address argument, CPU cpu) -> {
        cpu.flagI = 0;
    };

    // MODIFIES: cpu.flagV
    // EFFECTS: sets flagV to 0
    private static OpcodeAction runCLV = (Address argument, CPU cpu) -> {
        cpu.flagV = 0;
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: subtracts the argument from registerA and uses it to set the flags using these rules:
    //          if the result is negative,      flagC is set.
    //          if the result is zero,          flagZ is set.
    //          if the result's 7th bit is set, flagN is set.
    private static OpcodeAction runCMP = (Address argument, CPU cpu) -> {
        int result = cpu.getRegisterA().getValue() - argument.getValue();

        cpu.flagC = (result >= 0) ? 1 : 0;
        cpu.flagZ = (result == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(result, 7));
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: subtracts the argument from registerX and uses it to set the flags using these rules:
    //          if the result is negative,      flagC is set.
    //          if the result is zero,          flagZ is set.
    //          if the result's 7th bit is set, flagN is set.
    private static OpcodeAction runCPX = (Address argument, CPU cpu) -> {
        int result = cpu.getRegisterX().getValue() - argument.getValue();

        cpu.flagC = (result >= 0) ? 1 : 0;
        cpu.flagZ = (result == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(result, 7));
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: subtracts the argument from registerY and uses it to set the flags using these rules:
    //          if the result is negative,      flagC is set.
    //          if the result is zero,          flagZ is set.
    //          if the result's 7th bit is set, flagN is set.
    private static OpcodeAction runCPY = (Address argument, CPU cpu) -> {
        int result = cpu.getRegisterY().getValue() - argument.getValue();

        cpu.flagC = (result >= 0) ? 1 : 0;
        cpu.flagZ = (result == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(result, 7));
    };

    // MODIFIES: cpu's memory
    // EFFECTS: decreases the value in the cpu's memory by one, using argument as an address.
    //          flagZ set if the new value in memory is zero     after the operation.
    //          flagN set if the new value in memory is negative after the operation.
    private static OpcodeAction runDEC = (Address argument, CPU cpu) -> {
        argument.setValue(argument.getValue() - 1);

        cpu.flagZ = (argument.getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(argument.getValue(), 7));
    };

    // MODIFIES: cpu.getRegisterX
    // EFFECTS: decreases registerX by one.
    //          flagZ set if registerX is zero,
    //          flagN set if the 7th bit of registerX is set.
    private static OpcodeAction runDEX = (Address argument, CPU cpu) -> {
        cpu.setRegisterX(cpu.getRegisterX().getValue() - 1);

        cpu.flagZ = (cpu.getRegisterX().getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterX().getValue(), 7));
    };

    // MODIFIES: cpu.registerY
    // EFFECTS: decreases registerY by one.
    //          flagZ set if registerY is zero,
    //          flagN set if the 7th bit of registerY is set.
    private static OpcodeAction runDEY = (Address argument, CPU cpu) -> {
        cpu.setRegisterY(cpu.getRegisterY().getValue() - 1);

        cpu.flagZ = (cpu.getRegisterY().getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterY().getValue(), 7));
    };

    // MODIFIES: cpu.registerA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise XOR on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation.
    //          flagN set if registerA is negative after the operation.
    private static OpcodeAction runEOR = (Address argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterA().getValue() ^ argument.getValue());

        cpu.flagZ = (cpu.getRegisterA().getValue() == 0)  ? 1 : 0;
        cpu.flagN = (cpu.getRegisterA().getValue() > 127) ? 1 : 0;
    };

    // MODIFIES: cpu's memory
    // EFFECTS: increases the value in the cpu's memory by one, using argument as an address.
    //          flagZ set if the new value in memory is zero     after the operation.
    //          flagN set if the new value in memory is negative after the operation.
    private static OpcodeAction runINC = (Address argument, CPU cpu) -> {
        argument.setValue(argument.getValue() + 1);

        cpu.flagZ = (argument.getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(argument.getValue(), 7));
    };

    // MODIFIES: cpu.registerX
    // EFFECTS: increases registerX by one.
    //          flagZ set if registerX is zero     after the operation.
    //          flagN set if registerX is negative after the operation.
    private static OpcodeAction runINX = (Address argument, CPU cpu) -> {
        cpu.setRegisterX(cpu.getRegisterX().getValue() + 1);

        cpu.flagZ = (cpu.getRegisterX().getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterX().getValue(), 7));
    };

    // MODIFIES: cpu.registerY
    // EFFECTS: increases registerY by one.
    //          flagZ set if registerY is zero     after the operation.
    //          flagN set if registerY is negative after the operation.
    private static OpcodeAction runINY = (Address argument, CPU cpu) -> {
        cpu.setRegisterY(cpu.getRegisterY().getValue() + 1);

        cpu.flagZ = (cpu.getRegisterY().getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterY().getValue(), 7));
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: sets registerPC (the program counter) to the argument specified, minus 3.
    private static OpcodeAction runJMP = (Address argument, CPU cpu) -> {
        cpu.setRegisterPC(argument.getPointer());
    };

    // MODIFIES: cpu.registerPC, cpu.stack
    // EFFECTS: pushes the current value of registerPC (the program counter) to the stack, minus one.
    //          then, sets registerPC to the argument specified, minus 3.
    private static OpcodeAction runJSR = (Address argument, CPU cpu) -> {
        int byteOne = ((cpu.getRegisterPC().getValue() - 1) & Integer.parseInt("1111111100000000", 2)) >> 8;
        int byteTwo = ((cpu.getRegisterPC().getValue() - 1) & Integer.parseInt("0000000011111111", 2));
        cpu.pushStack(byteOne);
        cpu.pushStack(byteTwo);
        cpu.setRegisterPC(argument.getPointer());
    };

    // MODIFIES: cpu.registerA, flagZ, flagN
    // EFFECTS: sets registerA to the argument
    //          sets flagZ if registerA is 0
    //          sets flagN if the 7th bit of registerA is 1
    private static OpcodeAction runLDA = (Address argument, CPU cpu) -> {
        cpu.setRegisterA(argument.getValue());

        cpu.flagZ = (cpu.getRegisterA().getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterA().getValue(), 7));
    };

    // MODIFIES: cpu.registerX, flagZ, flagN
    // EFFECTS: sets registerX to the argument
    //          sets flagZ if registerA is 0
    //          sets flagN if the 7th bit of registerA is 1
    private static OpcodeAction runLDX = (Address argument, CPU cpu) -> {
        cpu.setRegisterX(argument.getValue());

        cpu.flagZ = (cpu.getRegisterX().getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterX().getValue(), 7));
    };

    // MODIFIES: cpu.registerY, flagZ, flagN
    // EFFECTS: sets registerY to the argument
    //          sets flagZ if registerA is 0
    //          sets flagN if the 7th bit of registerA is 1
    private static OpcodeAction runLDY = (Address argument, CPU cpu) -> {
        cpu.setRegisterY(argument.getValue());

        cpu.flagZ = (cpu.getRegisterY().getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterY().getValue(), 7));
    };

    // MODIFIES: the argument, cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: performs an arithmetic shift rightwards on the argument. Sets the flags according to these rules:
    //          flagC set if to the lost bit in the argument (bit 0)
    //          flagZ set if the result is zero
    //          flagN set if the result is negative.
    private static OpcodeAction runLSR = (Address argument, CPU cpu) -> {
        int oldValue = argument.getValue();
        argument.setValue(argument.getValue() / 2);

        cpu.flagC = Util.getNthBit(oldValue, 0);
        cpu.flagZ = (argument.getValue() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(argument.getValue(), 7));
    };

    // EFFECTS: doesn't modify the cpu in any way.
    private static OpcodeAction runNOP = (Address argument, CPU cpu) -> {
        int x = 2;
    };

    // MODIFIES: cpu.getRegisterA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise OR on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation.
    //          flagN set if registerA is negative after the operation.
    private static OpcodeAction runORA = (Address argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterA().getValue() | argument.getValue());

        cpu.flagZ = (cpu.getRegisterA().getValue() == 0)  ? 1 : 0;
        cpu.flagN = (cpu.getRegisterA().getValue() > 127) ? 1 : 0;
    };

    // MODIFIES: cpu.getRegisterS, cpu.stack
    // EFFECTS: pushes cpu.getRegisterA onto the cpu stack.
    private static OpcodeAction runPHA = (Address argument, CPU cpu) -> {
        cpu.pushStack(cpu.getRegisterA().getValue());
    };

    // MODIFIES: cpu.stack
    // EFFECTS: concatenates the cpu flags in this order: CZID11VN, where bits 4 and 5 is 1. pushes the result onto
    //          the stack.
    private static OpcodeAction runPHP = (Address argument, CPU cpu) -> {
        cpu.pushStack(cpu.getStatus());
    };

    // MODIFIES: cpu.getRegisterA cpu.getRegisterS, cpu.stack
    // EFFECTS: pulls from the cpu stack and sets the value to cpu.getRegisterA
    private static OpcodeAction runPLA = (Address argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.pullStack().getValue());

        cpu.flagZ = (cpu.getRegisterA().getValue() == 0)  ? 1 : 0;
        cpu.flagN = (cpu.getRegisterA().getValue() > 127) ? 1 : 0;
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu,flagI, cpu.flagD, cpu.flagB, cpu.flagV, cpu.flagN
    // EFFECTS: pulls from the stack. assigns the flags in this order: CZID11VN, where bits 4 and 5 are 1.
    // for example, if 10110110 was pulled from the stack, cpu.flagC would be 0, cpu.flagZ would be 1, etc.
    private static OpcodeAction runPLP = (Address argument, CPU cpu) -> {
        cpu.setStatus(cpu.pullStack().getValue());
    };

    // MODIFIES: the argument, cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: performs a roll leftwards on the argument with the carry bit. Sets the flags according to these rules:
    //          flagC set if to the lost bit in the argument (bit 7)
    //          flagZ set if the result is zero
    //          flagN set if the result is negative.
    private static OpcodeAction runROL = (Address argument, CPU cpu) -> {
        int oldValue = argument.getValue();
        argument.setValue(argument.getValue() << 1 | cpu.getFlagC());

        // TODO: change all flags to setFlag
        cpu.setFlagC((Util.getNthBit(oldValue, 7)));
        cpu.setFlagZ((argument.getValue() == 0) ? 1 : 0);
        cpu.setFlagN((Util.getNthBit(argument.getValue(), 7)));
    };

    // MODIFIES: the argument, cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: performs a roll rightwards on the argument with the carry bit. Sets the flags according to these rules:
    //          flagC set if to the lost bit in the argument (bit 0)
    //          flagZ set if the result is zero
    //          flagN set if the result is negative.
    private static OpcodeAction runROR = (Address argument, CPU cpu) -> {
        int oldValue = argument.getValue();
        argument.setValue(argument.getValue() >> 1 | cpu.getFlagC() << 7);

        cpu.setFlagC((Util.getNthBit(oldValue, 0)));
        cpu.setFlagZ((argument.getValue() == 0) ? 1 : 0);
        cpu.setFlagN((Util.getNthBit(argument.getValue(), 7)));
    };

    // MODIFIES: cpu.stack, all 7 cpu flags, cpu.registerPC
    // EFFECTS: the cpu flags are pulled from the stack, then the registerPC is pulled from the stack.
    private static OpcodeAction runRTI = (Address argument, CPU cpu) -> {
        cpu.setStatus(cpu.pullStack().getValue());
        int byteTwo = cpu.pullStack().getValue();
        int byteOne = cpu.pullStack().getValue();
        int fullByte = byteOne * 256 + byteTwo;
        cpu.setRegisterPC(fullByte);
    };

    // MODIFIES: cpu.registerPC, cpu.stack
    // EFFECTS: returns from the subroutine by pulling from the stack and setting
    // registerPC to that value (minus one to account for argument length)
    private static OpcodeAction runRTS = (Address argument, CPU cpu) -> {
        int byteTwo = cpu.pullStack().getValue();
        int byteOne = cpu.pullStack().getValue();
        int fullByte = byteOne * 256 + byteTwo;
        cpu.setRegisterPC(fullByte + 1);
    };

    // MODIFIES: cpu.registerA, cpu.flagV, cpu.flagZ, cpu.flagC, cpu.flagN
    // EFFECTS: subtracts the argument from cpu.registerA, and sets the flags according to these rules:
    //          flagV: if the sign is incorrect.
    //          flagZ: if the result is zero.
    //          flagC: if there was overflow.
    //          flagN: if the result is negative.
    private static OpcodeAction runSBC = (Address argument, CPU cpu) -> {
        int oldRegisterA = cpu.getRegisterA().getValue();
        int newValueRaw  = cpu.getRegisterA().getValue() - argument.getValue() - (1 - cpu.getFlagC());
        cpu.setRegisterA(newValueRaw);

        int oldRegisterASign = Util.getNthBit(oldRegisterA,7);
        int registerASign    = Util.getNthBit(cpu.getRegisterA().getValue(),7);
        int argumentSign     = Util.getNthBit(-(argument.getValue() + 1),7);
        cpu.flagV = (oldRegisterASign == argumentSign && oldRegisterASign != registerASign) ? 1 : 0;
        cpu.flagZ = (cpu.getRegisterA().getValue() == 0) ? 1 : 0;
        cpu.flagC = (newValueRaw >= 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterA().getValue(), 7));
    };

    // MODIFIES: cpu.flagC
    // EFFECTS: sets flagC to 1
    private static OpcodeAction runSEC = (Address argument, CPU cpu) -> {
        cpu.flagC = 1;
    };

    // MODIFIES: cpu.flagD
    // EFFECTS: sets flagD to 1
    private static OpcodeAction runSED = (Address argument, CPU cpu) -> {
        cpu.flagD = 1;
    };

    // MODIFIES: cpu.flagI
    // EFFECTS: sets flagI to 1
    private static OpcodeAction runSEI = (Address argument, CPU cpu) -> {
        cpu.flagI = 1;
    };

    // MODIFIES: cpu's memory
    // EFFECTS: writes cpu.registerA in memory using the argument as the address.
    private static OpcodeAction runSTA = (Address argument, CPU cpu) -> {
        cpu.writeMemory(argument.getPointer(), cpu.getRegisterA().getValue());
    };

    // MODIFIES: cpu.enabled
    // EFFECTS: disables the cpu.
    private static OpcodeAction runSTP = (Address argument, CPU cpu) -> {
        cpu.setEnabled(false);
    };

    // MODIFIES: cpu's memory
    // EFFECTS: writes cpu.registerX in memory using the argument as the address.
    private static OpcodeAction runSTX = (Address argument, CPU cpu) -> {
        cpu.writeMemory(argument.getPointer(), cpu.getRegisterX().getValue());
    };

    // MODIFIES: cpu's memory
    // EFFECTS: writes cpu.registerY in memory using the argument as the address.
    private static OpcodeAction runSTY = (Address argument, CPU cpu) -> {
        cpu.writeMemory(argument.getPointer(), cpu.getRegisterY().getValue());
    };

    // MODIFIES: cpu.getRegisterA, cpu.getRegisterX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterA to cpu.getRegisterX
    //          flagZ set if registerX is zero     after the operation.
    //          flagN set if registerX is negative after the operation.
    private static OpcodeAction runTAX = (Address argument, CPU cpu) -> {
        cpu.setRegisterX(cpu.getRegisterA().getValue());

        cpu.flagZ = (cpu.getRegisterX().getValue() == 0) ? 1 : 0;
        cpu.flagN = (cpu.getRegisterX().getValue() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterA, cpu.getRegisterY, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterA to cpu.getRegisterY
    //          flagZ set if registerY is zero     after the operation.
    //          flagN set if registerY is negative after the operation.
    private static OpcodeAction runTAY = (Address argument, CPU cpu) -> {
        cpu.setRegisterY(cpu.getRegisterA().getValue());

        cpu.flagZ = (cpu.getRegisterY().getValue() == 0) ? 1 : 0;
        cpu.flagN = (cpu.getRegisterY().getValue() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterS, cpu.getRegisterX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterS to cpu.getRegisterX
    //          flagZ set if registerX is zero     after the operation.
    //          flagN set if registerX is negative after the operation.
    private static OpcodeAction runTSX = (Address argument, CPU cpu) -> {
        cpu.setRegisterX(cpu.getRegisterS().getValue());

        cpu.flagZ = (cpu.getRegisterX().getValue() == 0) ? 1 : 0;
        cpu.flagN = (cpu.getRegisterX().getValue() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterS, cpu.getRegisterX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterX to cpu.getRegisterA
    //          flagZ set if registerA is zero     after the operation.
    //          flagN set if registerA is negative after the operation.
    private static OpcodeAction runTXA = (Address argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterX().getValue());

        cpu.flagZ = (cpu.getRegisterA().getValue() == 0) ? 1 : 0;
        cpu.flagN = (cpu.getRegisterA().getValue() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterX, cpu.getRegisterS
    // EFFECTS: transfers cpu.getRegisterX to cpu.getRegisterS
    private static OpcodeAction runTXS = (Address argument, CPU cpu) -> {
        cpu.setRegisterS(cpu.getRegisterX().getValue());
    };

    // MODIFIES: cpu.getRegisterY, cpu.getRegisterA
    // EFFECTS: transfers cpu.getRegisterY to cpu.getRegisterA
    //          flagZ set if registerA is zero     after the operation.
    //          flagN set if registerA is negative after the operation.
    private static OpcodeAction runTYA = (Address argument, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterY().getValue());

        cpu.flagZ = (cpu.getRegisterY().getValue() == 0) ? 1 : 0;
        cpu.flagN = (cpu.getRegisterY().getValue() >> 7) & 1; // is 7th bit set?
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
        opcodes.put("SHX", runNOP); // Change later
        opcodes.put("SHY", runNOP); // Change later
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
