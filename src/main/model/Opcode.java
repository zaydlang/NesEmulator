package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Class Opcode:
//     Opcode models
@SuppressWarnings("CodeBlock2Expr")
public class Opcode {
    private static OpcodeAction[] opcodes;
    static int currentOpcode = 0;

    public interface OpcodeAction {
        void run(int pointer, CPU cpu);
    }


    // MODIFIES: cpu
    // EFFECTS: Runs the given opcode with the given argument, modifying the CPU flags/registers/RAM as necessary.
    public static void runOpcode(int opcode, int pointer, CPU cpu) {
        //List<Entry<String, OpcodeAction>> list = new ArrayList<>();
        //list.addAll(opcodes.entrySet());
        //list.get(currentOpcode).getValue().run(argument, cpu);
        opcodes[opcode].run(pointer, cpu);
    }

    // MODIFIES: cpu.registerA, cpu.flagV, cpu.flagZ, cpu.flagC, cpu.flagN
    // EFFECTS: adds the argument to cpu.registerA, and sets the flags according to these rules:
    //          flagV: if the sign is incorrect.
    //          flagZ: if the result is zero.
    //          flagC: if there was overflow.
    //          flagN: if the result is negative.
    private static OpcodeAction runADC = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        int oldRegisterA = cpu.getRegisterA();
        int newValueRaw  = cpu.getRegisterA() + value + cpu.getFlagC();
        cpu.setRegisterA(newValueRaw & 0xFF);

        int oldRegisterASign = Util.getNthBit(oldRegisterA,       7);
        int registerASign    = Util.getNthBit(cpu.getRegisterA(), 7);
        int argumentSign     = Util.getNthBit(value,7);
        cpu.flagV = (oldRegisterASign == argumentSign && oldRegisterASign != registerASign) ? 1 : 0;
        cpu.flagZ = (cpu.getRegisterA() == 0)           ? 1 : 0;
        cpu.flagC = (newValueRaw > cpu.getRegisterA())  ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterA(), 7));
    };

    // MODIFIES: cpu.getRegisterA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise AND on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation.
    //          flagN set if registerA is negative after the operation.
    private static OpcodeAction runAND = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        cpu.setRegisterA(cpu.getRegisterA() & value);

        cpu.flagZ = (cpu.getRegisterA() == 0)  ? 1 : 0;
        cpu.flagN = (cpu.getRegisterA() > 127) ? 1 : 0;
    };

    // MODIFIES: the argument, cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: performs an arithmetic shift leftwards on the argument. Sets the flags according to these rules:
    //          flagC set if to the lost bit in the argument (bit 7)
    //          flagZ set if the result is zero
    //          flagN set if the result is negative.
    private static OpcodeAction runASL = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        //argument = argument.getReference();
        //int oldValue = value;
        //argument.setValue(value * 2);

        int newValue = value * 2;
        newValue = Util.wrapInt(newValue, 0, 255);

        cpu.writeMemory(pointer, newValue);
        cpu.flagC = Util.getNthBit(value, 7);
        cpu.flagZ = ((newValue * 2) == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(newValue, 7));
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagC is 0.
    private static OpcodeAction runBCC = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionPointer();
        if (cpu.flagC == 0) {
            cpu.incrementCyclesRemaining(1);
            cpu.setRegisterPC(value);
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagC is 1.
    private static OpcodeAction runBCS = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionPointer();
        if (cpu.flagC == 1) {
            cpu.incrementCyclesRemaining(1);
            cpu.setRegisterPC(value);
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagZ is 1.
    private static OpcodeAction runBEQ = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionPointer();
        if (cpu.flagZ == 1) {
            cpu.incrementCyclesRemaining(1);
            cpu.setRegisterPC(value);
        }
    };

    // MODIFIES: cpu.flagZ, cpu.flagV, cpu.flagN
    // EFFECTS: performs bitwise and on register A and the argument.
    // flagZ is set if the result is 0.
    // flagV is set to the 6th bit of the value in memory using argument as the address.
    // flagN is set to the 7th bit of the value in memory using argument as the address.
    private static OpcodeAction runBIT = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        int result = cpu.getRegisterA() & value;
        cpu.flagZ = (result == 0) ? 1 : 0;
        cpu.flagV = Util.getNthBit(value, 6);
        cpu.flagN = Util.getNthBit(value, 7);
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagN is 1.
    private static OpcodeAction runBMI = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionPointer();
        if (cpu.flagN == 1) {
            cpu.incrementCyclesRemaining(1);
            cpu.setRegisterPC(value);
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagZ is 0.
    private static OpcodeAction runBNE = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionPointer();
        if (cpu.flagZ == 0) {
            cpu.incrementCyclesRemaining(1);
            cpu.setRegisterPC(value);
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagN is 0.
    private static OpcodeAction runBPL = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionPointer();
        if (cpu.flagN == 0) {
            cpu.incrementCyclesRemaining(1);
            cpu.setRegisterPC(value);
        }
    };

    // MODIFIES: cpu.stack, cpu.registerPC, cpu.flagB
    // EFFECTS: registerPC is pushed onto the stack, followed by the cpu status.
    // then, registerPC is set to the value in memory at address "FFFE" and
    // flagB (the break flag) is set to 1.
    private static OpcodeAction runBRK = (int pointer, CPU cpu) -> {
        int byteOne = ((cpu.getRegisterPC() + 3) & 0b0000000011111111);
        int byteTwo = ((cpu.getRegisterPC() + 3) & 0b1111111100000000) >> 8;
        cpu.pushStack(byteOne);
        cpu.pushStack(byteTwo);
        cpu.pushStack(cpu.getStatus());

        byteOne = cpu.readMemory(0xFFFE);
        byteTwo = cpu.readMemory(0xFFFF);
        cpu.setRegisterPC(byteOne * 256 + byteTwo);
        cpu.setFlagB(1);
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagV is 0.
    private static OpcodeAction runBVC = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionPointer();
        if (cpu.flagV == 0) {
            cpu.incrementCyclesRemaining(1);
            cpu.setRegisterPC(value);
        }
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: adds argument to registerPC to cause a branch to a new location if:
    //          flagV is 1.
    private static OpcodeAction runBVS = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionPointer();
        if (cpu.flagV == 1) {
            cpu.incrementCyclesRemaining(1);
            cpu.setRegisterPC(value);
        }
    };

    // MODIFIES: cpu.flagC
    // EFFECTS: sets flagC to 0
    private static OpcodeAction runCLC = (int pointer, CPU cpu) -> {
        cpu.flagC = 0;
    };

    // MODIFIES: cpu.flagD
    // EFFECTS: sets flagD to 0
    private static OpcodeAction runCLD = (int pointer, CPU cpu) -> {
        cpu.flagD = 0;
    };

    // MODIFIES: cpu.flagI
    // EFFECTS: sets flagI to 0
    private static OpcodeAction runCLI = (int pointer, CPU cpu) -> {
        cpu.flagI = 0;
    };

    // MODIFIES: cpu.flagV
    // EFFECTS: sets flagV to 0
    private static OpcodeAction runCLV = (int pointer, CPU cpu) -> {
        cpu.flagV = 0;
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: subtracts the argument from registerA and uses it to set the flags using these rules:
    //          if the result is negative,      flagC is set.
    //          if the result is zero,          flagZ is set.
    //          if the result's 7th bit is set, flagN is set.
    private static OpcodeAction runCMP = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        int result = cpu.getRegisterA() - value;

        //result = Util.wrapInt(result, 0, 255);
        cpu.flagC = (result >= 0) ? 1 : 0;
        cpu.flagZ = (result == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(result, 7));
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: subtracts the argument from registerX and uses it to set the flags using these rules:
    //          if the result is negative,      flagC is set.
    //          if the result is zero,          flagZ is set.
    //          if the result's 7th bit is set, flagN is set.
    private static OpcodeAction runCPX = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        int result = cpu.getRegisterX() - value;

        cpu.flagC = (result >= 0) ? 1 : 0;
        cpu.flagZ = (result == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(result, 7));
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: subtracts the argument from registerY and uses it to set the flags using these rules:
    //          if the result is negative,      flagC is set.
    //          if the result is zero,          flagZ is set.
    //          if the result's 7th bit is set, flagN is set.
    private static OpcodeAction runCPY = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        int result = cpu.getRegisterY() - value;

        cpu.flagC = (result >= 0) ? 1 : 0;
        cpu.flagZ = (result == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(result, 7));
    };

    // MODIFIES: cpu's memory
    // EFFECTS: decreases the value in the cpu's memory by one, using argument as an address.
    //          flagZ set if the new value in memory is zero     after the operation.
    //          flagN set if the new value in memory is negative after the operation.
    private static OpcodeAction runDEC = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        int newValue = value - 1;
        newValue = Util.wrapInt(newValue, 0, 255);
        //argument = argument.getReference();
        //argument.setValue(value + 1);
        cpu.writeMemory(pointer, newValue);

        cpu.flagZ = (newValue == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(newValue, 7));
    };

    // MODIFIES: cpu.getRegisterX
    // EFFECTS: decreases registerX by one.
    //          flagZ set if registerX is zero,
    //          flagN set if the 7th bit of registerX is set.
    private static OpcodeAction runDEX = (int pointer, CPU cpu) -> {
        cpu.setRegisterX((cpu.getRegisterX() - 1) & 0xFF);

        cpu.flagZ = (cpu.getRegisterX() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterX(), 7));
    };

    // MODIFIES: cpu.registerY
    // EFFECTS: decreases registerY by one.
    //          flagZ set if registerY is zero,
    //          flagN set if the 7th bit of registerY is set.
    private static OpcodeAction runDEY = (int pointer, CPU cpu) -> {
        cpu.setRegisterY((cpu.getRegisterY() - 1) & 0xFF);

        cpu.flagZ = (cpu.getRegisterY() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterY(), 7));
    };

    // MODIFIES: cpu.registerA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise XOR on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation.
    //          flagN set if registerA is negative after the operation.
    private static OpcodeAction runEOR = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        cpu.setRegisterA(cpu.getRegisterA() ^ value);

        cpu.flagZ = (cpu.getRegisterA() == 0)  ? 1 : 0;
        cpu.flagN = (cpu.getRegisterA() > 127) ? 1 : 0;
    };

    // MODIFIES: cpu's memory
    // EFFECTS: increases the value in the cpu's memory by one, using argument as an address.
    //          flagZ set if the new value in memory is zero     after the operation.
    //          flagN set if the new value in memory is negative after the operation.
    private static OpcodeAction runINC = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        int newValue = value + 1;
        newValue = Util.wrapInt(newValue, 0, 255);
        //argument = argument.getReference();
        //argument.setValue(value + 1);
        cpu.writeMemory(pointer, newValue);

        cpu.flagZ = (newValue == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(newValue, 7));
    };

    // MODIFIES: cpu.registerX
    // EFFECTS: increases registerX by one.
    //          flagZ set if registerX is zero     after the operation.
    //          flagN set if registerX is negative after the operation.
    private static OpcodeAction runINX = (int pointer, CPU cpu) -> {
        cpu.setRegisterX((cpu.getRegisterX() + 1) & 0xFF);

        cpu.flagZ = (cpu.getRegisterX() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterX(), 7));
    };

    // MODIFIES: cpu.registerY
    // EFFECTS: increases registerY by one.
    //          flagZ set if registerY is zero     after the operation.
    //          flagN set if registerY is negative after the operation.
    private static OpcodeAction runINY = (int pointer, CPU cpu) -> {
        cpu.setRegisterY((cpu.getRegisterY() + 1) & 0xFF);

        cpu.flagZ = (cpu.getRegisterY() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterY(), 7));
    };

    // MODIFIES: cpu.registerPC
    // EFFECTS: sets registerPC (the program counter) to the argument specified, minus 3.
    private static OpcodeAction runJMP = (int pointer, CPU cpu) -> {
        cpu.setRegisterPC(pointer);
    };

    // MODIFIES: cpu.registerPC, cpu.stack
    // EFFECTS: pushes the current value of registerPC (the program counter) to the stack, minus one.
    //          then, sets registerPC to the argument specified, minus 3.
    private static OpcodeAction runJSR = (int pointer, CPU cpu) -> {
        int byteOne = ((cpu.getRegisterPC() - 1) & 0b1111111100000000) >> 8;
        int byteTwo = ((cpu.getRegisterPC() - 1) & 0b0000000011111111);
        cpu.pushStack(byteOne);
        cpu.pushStack(byteTwo);
        cpu.setRegisterPC(pointer);
    };

    // MODIFIES: cpu.registerA, flagZ, flagN
    // EFFECTS: sets registerA to the argument
    //          sets flagZ if registerA is 0
    //          sets flagN if the 7th bit of registerA is 1
    private static OpcodeAction runLDA = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        cpu.setRegisterA(value);

        cpu.flagZ = (cpu.getRegisterA() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterA(), 7));
    };

    // MODIFIES: cpu.registerX, flagZ, flagN
    // EFFECTS: sets registerX to the argument
    //          sets flagZ if registerA is 0
    //          sets flagN if the 7th bit of registerA is 1
    private static OpcodeAction runLDX = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        cpu.setRegisterX(value);

        cpu.flagZ = (cpu.getRegisterX() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterX(), 7));
    };

    // MODIFIES: cpu.registerY, flagZ, flagN
    // EFFECTS: sets registerY to the argument
    //          sets flagZ if registerA is 0
    //          sets flagN if the 7th bit of registerA is 1
    private static OpcodeAction runLDY = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        cpu.setRegisterY(value);

        cpu.flagZ = (cpu.getRegisterY() == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterY(), 7));
    };

    // MODIFIES: the argument, cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: performs an arithmetic shift rightwards on the argument. Sets the flags according to these rules:
    //          flagC set if to the lost bit in the argument (bit 0)
    //          flagZ set if the result is zero
    //          flagN set if the result is negative.
    private static OpcodeAction runLSR = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        //argument = argument.getReference();
        //int oldValue = value;
        //argument.setValue(value / 2);

        int newValue = value / 2;
        newValue = Util.wrapInt(newValue, 0, 255);

        cpu.writeMemory(pointer, newValue);
        cpu.flagC = Util.getNthBit(value, 0);
        cpu.flagZ = (newValue == 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(newValue, 7));
    };

    // EFFECTS: doesn't modify the cpu in any way.
    private static OpcodeAction runNOP = (int pointer, CPU cpu) -> {
    };

    // MODIFIES: cpu.getRegisterA, cpu.flagZ, cpu.flagN
    // EFFECTS: does a bitwise OR on registerA and the argument, and sets registerA to that value
    //          flagZ set if registerA is zero     after the operation.
    //          flagN set if registerA is negative after the operation.
    private static OpcodeAction runORA = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        cpu.setRegisterA(cpu.getRegisterA() | value);

        cpu.flagZ = (cpu.getRegisterA() == 0)  ? 1 : 0;
        cpu.flagN = (cpu.getRegisterA() > 127) ? 1 : 0;
    };

    // MODIFIES: cpu.getRegisterS, cpu.stack
    // EFFECTS: pushes cpu.getRegisterA onto the cpu stack.
    private static OpcodeAction runPHA = (int pointer, CPU cpu) -> {
        cpu.pushStack(cpu.getRegisterA());
    };

    // MODIFIES: cpu.stack
    // EFFECTS: concatenates the cpu flags in this order: CZID11VN, where bits 4 and 5 is 1. pushes the result onto
    //          the stack.
    private static OpcodeAction runPHP = (int pointer, CPU cpu) -> {
        cpu.pushStack(cpu.getStatus());
    };

    // MODIFIES: cpu.getRegisterA cpu.getRegisterS, cpu.stack
    // EFFECTS: pulls from the cpu stack and sets the value to cpu.getRegisterA
    private static OpcodeAction runPLA = (int pointer, CPU cpu) -> {
        cpu.setRegisterA(cpu.pullStack());

        cpu.flagZ = (cpu.getRegisterA() == 0)  ? 1 : 0;
        cpu.flagN = (cpu.getRegisterA() > 127) ? 1 : 0;
    };

    // MODIFIES: cpu.flagC, cpu.flagZ, cpu,flagI, cpu.flagD, cpu.flagB, cpu.flagV, cpu.flagN
    // EFFECTS: pulls from the stack. assigns the flags in this order: CZID11VN, where bits 4 and 5 are 1.
    // for example, if 10110110 was pulled from the stack, cpu.flagC would be 0, cpu.flagZ would be 1, etc.
    private static OpcodeAction runPLP = (int pointer, CPU cpu) -> {
        cpu.setStatus(cpu.pullStack());
    };

    // MODIFIES: the argument, cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: performs a roll leftwards on the argument with the carry bit. Sets the flags according to these rules:
    //          flagC set if to the lost bit in the argument (bit 7)
    //          flagZ set if the result is zero
    //          flagN set if the result is negative.
    private static OpcodeAction runROL = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        //argument = argument.getReference();
        //int oldValue = value;
        //argument.setValue(value << 1 | cpu.getFlagC());

        int newValue = value << 1 | cpu.getFlagC();
        cpu.writeMemory(pointer, newValue & 0xFF);
        // TODO: change all flags to setFlag
        cpu.setFlagC((Util.getNthBit(value, 7)));
        cpu.setFlagZ((newValue == 0) ? 1 : 0);
        cpu.setFlagN((Util.getNthBit(newValue, 7)));
    };

    // MODIFIES: the argument, cpu.flagC, cpu.flagZ, cpu.flagN
    // EFFECTS: performs a roll rightwards on the argument with the carry bit. Sets the flags according to these rules:
    //          flagC set if to the lost bit in the argument (bit 0)
    //          flagZ set if the result is zero
    //          flagN set if the result is negative.
    private static OpcodeAction runROR = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        //argument = argument.getReference();
        //int oldValue = value;
        //argument.setValue(value >> 1 | cpu.getFlagC() << 7);

        int newValue = value >> 1 | cpu.getFlagC() << 7;
        cpu.writeMemory(pointer, newValue & 0xFF);
        cpu.setFlagC((Util.getNthBit(value, 0)));
        cpu.setFlagZ((newValue == 0) ? 1 : 0);
        cpu.setFlagN((Util.getNthBit(newValue, 7)));
    };

    // MODIFIES: cpu.stack, all 7 cpu flags, cpu.registerPC
    // EFFECTS: the cpu flags are pulled from the stack, then the registerPC is pulled from the stack.
    private static OpcodeAction runRTI = (int pointer, CPU cpu) -> {
        cpu.setStatus(cpu.pullStack());
        int byteTwo = cpu.pullStack();
        int byteOne = cpu.pullStack();
        int fullByte = byteOne * 256 + byteTwo;
        cpu.setRegisterPC(fullByte);
    };

    // MODIFIES: cpu.registerPC, cpu.stack
    // EFFECTS: returns from the subroutine by pulling from the stack and setting
    // registerPC to that value (minus one to account for argument length)
    private static OpcodeAction runRTS = (int pointer, CPU cpu) -> {
        int byteTwo = cpu.pullStack();
        int byteOne = cpu.pullStack();
        int fullByte = byteOne * 256 + byteTwo;
        cpu.setRegisterPC(fullByte + 1);
    };

    // MODIFIES: cpu.registerA, cpu.flagV, cpu.flagZ, cpu.flagC, cpu.flagN
    // EFFECTS: subtracts the argument from cpu.registerA, and sets the flags according to these rules:
    //          flagV: if the sign is incorrect.
    //          flagZ: if the result is zero.
    //          flagC: if there was overflow.
    //          flagN: if the result is negative.
    private static OpcodeAction runSBC = (int pointer, CPU cpu) -> {
        int value = cpu.getCurrentInstructionValue();
        int oldRegisterA = cpu.getRegisterA();
        int newValueRaw  = cpu.getRegisterA() - value - (1 - cpu.getFlagC());
        cpu.setRegisterA(newValueRaw & 0xFF);

        int oldRegisterASign = Util.getNthBit(oldRegisterA,7);
        int registerASign    = Util.getNthBit(cpu.getRegisterA(),7);
        int argumentSign     = Util.getNthBit(-(value + 1),7);
        cpu.flagV = (oldRegisterASign == argumentSign && oldRegisterASign != registerASign) ? 1 : 0;
        cpu.flagZ = (cpu.getRegisterA() == 0) ? 1 : 0;
        cpu.flagC = (newValueRaw >= 0) ? 1 : 0;
        cpu.flagN = (Util.getNthBit(cpu.getRegisterA(), 7));
    };

    // MODIFIES: cpu.flagC
    // EFFECTS: sets flagC to 1
    private static OpcodeAction runSEC = (int pointer, CPU cpu) -> {
        cpu.flagC = 1;
    };

    // MODIFIES: cpu.flagD
    // EFFECTS: sets flagD to 1
    private static OpcodeAction runSED = (int pointer, CPU cpu) -> {
        cpu.flagD = 1;
    };

    // MODIFIES: cpu.flagI
    // EFFECTS: sets flagI to 1
    private static OpcodeAction runSEI = (int pointer, CPU cpu) -> {
        cpu.flagI = 1;
    };

    // MODIFIES: cpu's memory
    // EFFECTS: writes cpu.registerA in memory using the argument as the address.
    private static OpcodeAction runSTA = (int pointer, CPU cpu) -> {
        cpu.writeMemory(pointer, cpu.getRegisterA());
    };

    // MODIFIES: cpu.enabled
    // EFFECTS: disables the cpu.
    private static OpcodeAction runSTP = (int pointer, CPU cpu) -> {
        cpu.setEnabled(false);
    };

    // MODIFIES: cpu's memory
    // EFFECTS: writes cpu.registerX in memory using the argument as the address.
    private static OpcodeAction runSTX = (int pointer, CPU cpu) -> {
        cpu.writeMemory(pointer, cpu.getRegisterX());
    };

    // MODIFIES: cpu's memory
    // EFFECTS: writes cpu.registerY in memory using the argument as the address.
    private static OpcodeAction runSTY = (int pointer, CPU cpu) -> {
        cpu.writeMemory(pointer, cpu.getRegisterY());
    };

    // MODIFIES: cpu.getRegisterA, cpu.getRegisterX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterA to cpu.getRegisterX
    //          flagZ set if registerX is zero     after the operation.
    //          flagN set if registerX is negative after the operation.
    private static OpcodeAction runTAX = (int pointer, CPU cpu) -> {
        cpu.setRegisterX(cpu.getRegisterA());

        cpu.flagZ = (cpu.getRegisterX() == 0) ? 1 : 0;
        cpu.flagN = (cpu.getRegisterX() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterA, cpu.getRegisterY, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterA to cpu.getRegisterY
    //          flagZ set if registerY is zero     after the operation.
    //          flagN set if registerY is negative after the operation.
    private static OpcodeAction runTAY = (int pointer, CPU cpu) -> {
        cpu.setRegisterY(cpu.getRegisterA());

        cpu.flagZ = (cpu.getRegisterY() == 0) ? 1 : 0;
        cpu.flagN = (cpu.getRegisterY() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterS, cpu.getRegisterX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterS to cpu.getRegisterX
    //          flagZ set if registerX is zero     after the operation.
    //          flagN set if registerX is negative after the operation.
    private static OpcodeAction runTSX = (int pointer, CPU cpu) -> {
        cpu.setRegisterX(cpu.getRegisterS());

        cpu.flagZ = (cpu.getRegisterX() == 0) ? 1 : 0;
        cpu.flagN = (cpu.getRegisterX() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterS, cpu.getRegisterX, cpu.flagZ, cpu.flagN
    // EFFECTS: transfers cpu.getRegisterX to cpu.getRegisterA
    //          flagZ set if registerA is zero     after the operation.
    //          flagN set if registerA is negative after the operation.
    private static OpcodeAction runTXA = (int pointer, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterX());

        cpu.flagZ = (cpu.getRegisterA() == 0) ? 1 : 0;
        cpu.flagN = (cpu.getRegisterA() >> 7) & 1; // is 7th bit set?
    };

    // MODIFIES: cpu.getRegisterX, cpu.getRegisterS
    // EFFECTS: transfers cpu.getRegisterX to cpu.getRegisterS
    private static OpcodeAction runTXS = (int pointer, CPU cpu) -> {
        cpu.setRegisterS(cpu.getRegisterX());
    };

    // MODIFIES: cpu.getRegisterY, cpu.getRegisterA
    // EFFECTS: transfers cpu.getRegisterY to cpu.getRegisterA
    //          flagZ set if registerA is zero     after the operation.
    //          flagN set if registerA is negative after the operation.
    private static OpcodeAction runTYA = (int pointer, CPU cpu) -> {
        cpu.setRegisterA(cpu.getRegisterY());

        cpu.flagZ = (cpu.getRegisterY() == 0) ? 1 : 0;
        cpu.flagN = (cpu.getRegisterY() >> 7) & 1; // is 7th bit set?
    };

    public static final int ADC = 0;
    public static final int AND = 1;
    public static final int ASL = 2;
    public static final int BCC = 3;
    public static final int BCS = 4;
    public static final int BEQ = 5;
    public static final int BIT = 6;
    public static final int BMI = 7;
    public static final int BNE = 8;
    public static final int BPL = 9;
    public static final int BRK = 10;
    public static final int BVC = 11;
    public static final int BVS = 12;
    public static final int CLC = 13;
    public static final int CLD = 14;
    public static final int CLI = 15;
    public static final int CLV = 16;
    public static final int CMP = 17;
    public static final int CPX = 18;
    public static final int CPY = 19;
    public static final int DEC = 20;
    public static final int DEX = 21;
    public static final int DEY = 22;
    public static final int EOR = 23;
    public static final int INC = 24;
    public static final int INX = 25;
    public static final int INY = 26;
    public static final int JMP = 27;
    public static final int JSR = 28;
    public static final int LDA = 29;
    public static final int LDX = 30;
    public static final int LDY = 31;
    public static final int LSR = 32;
    public static final int NOP = 33;
    public static final int ORA = 34;
    public static final int PHA = 35;
    public static final int PHP = 36;
    public static final int PLA = 37;
    public static final int PLP = 38;
    public static final int ROL = 39;
    public static final int ROR = 40;
    public static final int RTI = 41;
    public static final int RTS = 42;
    public static final int SBC = 43;
    public static final int SEC = 44;
    public static final int SED = 45;
    public static final int SEI = 46;
    public static final int SHX = 47;
    public static final int SHY = 48;
    public static final int STA = 49;
    public static final int STP = 50;
    public static final int STX = 51;
    public static final int STY = 52;
    public static final int TAX = 53;
    public static final int TAY = 54;
    public static final int TSX = 55;
    public static final int TXA = 56;
    public static final int TXS = 57;
    public static final int TYA = 58;
    
    static {
        opcodes = new OpcodeAction[59];
        opcodes[ADC] = runADC;
        opcodes[AND] = runAND;
        opcodes[ASL] = runASL;
        opcodes[BCC] = runBCC;
        opcodes[BCS] = runBCS;
        opcodes[BEQ] = runBEQ;
        opcodes[BIT] = runBIT;
        opcodes[BMI] = runBMI;
        opcodes[BNE] = runBNE;
        opcodes[BPL] = runBPL;
        opcodes[BRK] = runBRK;
        opcodes[BVC] = runBVC;
        opcodes[BVS] = runBVS;
        opcodes[CLC] = runCLC;
        opcodes[CLD] = runCLD;
        opcodes[CLI] = runCLI;
        opcodes[CLV] = runCLV;
        opcodes[CMP] = runCMP;
        opcodes[CPX] = runCPX;
        opcodes[CPY] = runCPY;
        opcodes[DEC] = runDEC;
        opcodes[DEX] = runDEX;
        opcodes[DEY] = runDEY;
        opcodes[EOR] = runEOR;
        opcodes[INC] = runINC;
        opcodes[INX] = runINX;
        opcodes[INY] = runINY;
        opcodes[JMP] = runJMP;
        opcodes[JSR] = runJSR;
        opcodes[LDA] = runLDA;
        opcodes[LDX] = runLDX;
        opcodes[LDY] = runLDY;
        opcodes[LSR] = runLSR;
        opcodes[NOP] = runNOP;
        opcodes[ORA] = runORA;
        opcodes[PHA] = runPHA;
        opcodes[PHP] = runPHP;
        opcodes[PLA] = runPLA;
        opcodes[PLP] = runPLP;
        opcodes[ROL] = runROL;
        opcodes[ROR] = runROR;
        opcodes[RTI] = runRTI;
        opcodes[RTS] = runRTS;
        opcodes[SBC] = runSBC;
        opcodes[SEC] = runSEC;
        opcodes[SED] = runSED;
        opcodes[SEI] = runSEI;
        opcodes[SHX] = runNOP; // Change later
        opcodes[SHY] = runNOP; // Change later
        opcodes[STA] = runSTA;
        opcodes[STP] = runSTP;
        opcodes[STX] = runSTX;
        opcodes[STY] = runSTY;
        opcodes[TAX] = runTAX;
        opcodes[TAY] = runTAY;
        opcodes[TSX] = runTSX;
        opcodes[TXA] = runTXA;
        opcodes[TXS] = runTXS;
        opcodes[TYA] = runTYA;
    }
}
