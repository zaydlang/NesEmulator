package model;

import java.util.HashMap;

// Class Mode:
//     Mode models the 13 Addressing Modes allowed by the 6502 CPU. The Addressing Modes provide the arguments for the
//     Opcodes. Each Addressing Mode is modeled as an interface that takes in some arguments and a cpu and calculates
//     what arguments to pass in to the opcode.

@SuppressWarnings("CodeBlock2Expr")
public class Mode {
    private static ModeAction[] modes;

    public interface ModeAction {
        void run(int[] arguments, int argumentsSize, CPU cpu);
    }

    // REQUIRES: arguments is a valid argument for the specified ModeAction.
    // details given for what a "valid argument" is under each ModeAction definition.
    // EFFECTS: Runs the given Addressing Mode with the given argument, and returns the proper value from the cpu.
    public static void runMode(int mode, int[] arguments, int argumentsSize, CPU cpu) {
        modes[mode].run(arguments, argumentsSize, cpu);
    }

    // REQUIRES: arguments has a length of 0.
    // EFFECTS: An instruction with an implicit addressing mode won't use its argument anyway,
    //          so it doesn't matter what's returned here. Nevertheless, returns 0.
    public static ModeAction getImplicit = (int[] arguments, int argumentsSize, CPU cpu) -> {
        cpu.setCurrentInstructionPointer(-1);
        cpu.setCurrentInstructionValue(0);
    };

    // REQUIRES: arguments has a length of 0.
    // EFFECTS: returns the value of the accumulator.
    public static ModeAction getAccumulator = (int[] arguments, int argumentsSize, CPU cpu) -> {
        cpu.setCurrentInstructionPointer(cpu.REGISTER_A_ADDRESS);
    };

    // REQUIRES: arguments has a length of 0 or 1.
    // EFFECTS: returns the first argument in the list of arguments.
    public static ModeAction getImmediate = (int[] arguments, int argumentsSize, CPU cpu) -> {
        if (argumentsSize == 0) {
            cpu.setCurrentInstructionPointer(-1);
            cpu.setCurrentInstructionValue(0);
        } else {
            cpu.setCurrentInstructionPointer(-1);
            cpu.setCurrentInstructionValue(arguments[0]);
        }
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: returns the value in memory at arguments[0]. Since arguments has a length of 1, this is by default
    // the zero-page and no extra correction is needed.
    public static ModeAction getZeroPage = (int[] arguments, int argumentsSize, CPU cpu) -> {
        cpu.setCurrentInstructionPointer(arguments[0]);
    };

    // REQUIRES: arguments has a length of 2.
    // EFFECTS: returns the little endian number represented by the two arguments given.
    public static ModeAction getAbsolute = (int[] arguments, int argumentsSize, CPU cpu) -> {
        int pointer = arguments[0] + arguments[1] * 256;
        cpu.setCurrentInstructionPointer(pointer);
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: returns the argument (interpreted as a signed bit) added to the program counter (registerPC)
    public static ModeAction getRelative = (int[] arguments, int argumentsSize, CPU cpu) -> {
        int signedArgument = arguments[0] < 128 ? arguments[0] : arguments[0] - 256;
        int possiblePointer = signedArgument + cpu.getRegisterPC();

        while (possiblePointer > 65536) {
            possiblePointer -= 65536 + 1;
        }

        while (possiblePointer < 0) {
            possiblePointer += 65536 + 1;
        }

        cpu.setCurrentInstructionPointer(possiblePointer);
    };

    // REQUIRES: arguments has a length of 2.
    // EFFECTS: returns the little endian number represented by the two arguments given.
    public static ModeAction getIndirect = (int[] arguments, int argumentsSize, CPU cpu) -> {
        int pointerOne = arguments[0] + arguments[1] * 256;
        int pointerTwo = pointerOne + 1;

        // Indirect runs incorrectly under certain conditions. See:
        // http://www.obelisk.me.uk/6502/reference.html#JMP
        boolean broken = arguments[0] == 255;
        if (broken) {
            pointerTwo -= (0xFF + 1);
        }

        int addressOne = cpu.readMemory(pointerOne);
        int addressTwo = cpu.readMemory(pointerTwo);
        int pointer    = addressOne + addressTwo * 256;
        cpu.setCurrentInstructionPointer(pointer);
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: adds the little endian number represented by the two arguments given added to registerX and returns
    //          the associated address on the zero page
    public static ModeAction getZeroPageIndexedX = (int[] arguments, int argumentsSize, CPU cpu) -> {
        int rawAddress = (arguments[0] + cpu.getRegisterX());
        int zeroPageAddress = rawAddress & 0xFF;

        cpu.setCurrentInstructionPointer(zeroPageAddress);
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: adds the little endian number represented by the two arguments given added to registerY and returns
    //          the associated address on the zero page
    public static ModeAction getZeroPageIndexedY = (int[] arguments, int argumentsSize, CPU cpu) -> {
        int rawAddress = (arguments[0] + cpu.getRegisterY());
        int zeroPageAddress = rawAddress & 0xFF;

        cpu.setCurrentInstructionPointer(zeroPageAddress);
    };

    // REQUIRES: arguments has a length of 2.
    // EFFECTS: returns the little endian number represented by the two arguments given added to registerX.
    public static ModeAction getAbsoluteIndexedX = (int[] arguments, int argumentsSize, CPU cpu) -> {
        int pointer = arguments[0] + arguments[1] * 256 + cpu.getRegisterX();
        if (cpu.getRegisterX() != 0) {
            cpu.incrementCyclesRemaining(1);
        }

        int mirroredPointer = pointer & 0xFFFF;
        cpu.setCurrentInstructionPointer(mirroredPointer);
    };

    // REQUIRES: arguments has a length of 2.
    // EFFECTS: returns the little endian number represented by the two arguments given added to registerY.
    public static ModeAction getAbsoluteIndexedY = (int[] arguments, int argumentsSize, CPU cpu) -> {
        int pointer = arguments[0] + arguments[1] * 256 + cpu.getRegisterY();
        if (cpu.getRegisterY() != 0) {
            cpu.incrementCyclesRemaining(1);
        }

        int mirroredPointer = pointer & 0xFFFF;
        cpu.setCurrentInstructionPointer(mirroredPointer);
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: first fetches the 2-byte value in memory at address (argument + registerX) on the zero page. Then,
    // fetches and returns the value in memory at that 2-byte address.
    public static ModeAction getIndexedIndirect = (int[] arguments, int argumentsSize, CPU cpu) -> {
        int pointerOne = (arguments[0] + cpu.getRegisterX()) & 0x00FF;
        int pointerTwo = (pointerOne + 1) & 0xFF;
        int fullPointer = cpu.readMemory(pointerOne) + cpu.readMemory(pointerTwo) * 256;
        cpu.setCurrentInstructionPointer(fullPointer);
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: fetches the 2-byte value in memory at the argument on the zero page. Then, fetches and returns the value
    // in memory at (that 2-byte address + registerY).
    public static ModeAction getIndirectIndexed = (int[] arguments, int argumentsSize, CPU cpu) -> {
        int addressOne = arguments[0] & 0xFF;
        int addressTwo = (addressOne + 1) & 0xFF;

        int pointerOne = cpu.readMemory(addressOne);
        int pointerTwo = cpu.readMemory(addressTwo);
        int fullPointer = (pointerOne + pointerTwo * 256 + cpu.getRegisterY());
        if (cpu.getRegisterY() != 0) {
            cpu.incrementCyclesRemaining(1);
        }

        int mirroredPointer = fullPointer & 0xFFFF;
        cpu.setCurrentInstructionPointer(mirroredPointer);
    };

    public static final int IMPLICIT = 0;
    public static final int ACCUMULATOR = 1;
    public static final int IMMEDIATE = 2;
    public static final int ZERO_PAGE = 3;
    public static final int ABSOLUTE = 4;
    public static final int RELATIVE = 5;
    public static final int INDIRECT = 6;
    public static final int ZERO_PAGE_INDEXED_X = 7;
    public static final int ZERO_PAGE_INDEXED_Y = 8;
    public static final int ABSOLUTE_INDEXED_X = 9;
    public static final int ABSOLUTE_INDEXED_Y = 10;
    public static final int INDEXED_INDIRECT = 11;
    public static final int INDIRECT_INDEXED = 12;

    static {
        modes = new ModeAction[13];
        modes[IMPLICIT] = getImplicit;
        modes[ACCUMULATOR] = getAccumulator;
        modes[IMMEDIATE] = getImmediate;
        modes[ZERO_PAGE] = getZeroPage;
        modes[ABSOLUTE] = getAbsolute;
        modes[RELATIVE] = getRelative;
        modes[INDIRECT] = getIndirect;
        modes[ZERO_PAGE_INDEXED_X] = getZeroPageIndexedX;
        modes[ZERO_PAGE_INDEXED_Y] = getZeroPageIndexedY;
        modes[ABSOLUTE_INDEXED_X] = getAbsoluteIndexedX;
        modes[ABSOLUTE_INDEXED_Y] = getAbsoluteIndexedY;
        modes[INDEXED_INDIRECT] = getIndexedIndirect;
        modes[INDIRECT_INDEXED] = getIndirectIndexed;
    }
}






