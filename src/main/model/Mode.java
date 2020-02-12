package model;

import java.util.HashMap;

// Class Mode:
//     Mode models the 13 Addressing Modes allowed by the 6502 CPU. The Addressing Modes provide the arguments for the
//     Opcodes. Each Addressing Mode is modeled as an interface that takes in some arguments and a cpu and calculates
//     what arguments to pass in to the opcode.

@SuppressWarnings("CodeBlock2Expr")
public class Mode extends HashMap<String, Mode.ModeAction> {
    private static Mode modes;

    public interface ModeAction {
        Address run(Address[] arguments, CPU cpu);
    }

    // REQUIRES: arguments is a valid argument for the specified ModeAction.
    // details given for what a "valid argument" is under each ModeAction definition.
    // EFFECTS: Runs the given Addressing Mode with the given argument, and returns the proper value from the cpu.
    public static Address runMode(String mode, Address[] arguments, CPU cpu) {
        return modes.get(mode).run(arguments, cpu);
    }

    // REQUIRES: arguments has a length of 0.
    // EFFECTS: An instruction with an implicit addressing mode won't use its argument anyway,
    //          so it doesn't matter what's returned here. Nevertheless, returns 0.
    public static ModeAction getImplicit = (Address[] arguments, CPU cpu) -> {
        return new Address(0);
    };

    // REQUIRES: arguments has a length of 0.
    // EFFECTS: returns the value of the accumulator.
    public static ModeAction getAccumulator = (Address[] arguments, CPU cpu) -> {
        return cpu.getRegisterA();
    };

    // REQUIRES: arguments has a length of 0 or 1.
    // EFFECTS: returns the first argument in the list of arguments.
    public static ModeAction getImmediate = (Address[] arguments, CPU cpu) -> {
        return arguments.length == 0 ? new Address(0) : new Address(arguments[0].getValue());
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: returns the value in memory at arguments[0]. Since arguments has a length of 1, this is by default
    // the zero-page and no extra correction is needed.
    public static ModeAction getZeroPage = (Address[] arguments, CPU cpu) -> {
        return cpu.readMemory(arguments[0].getValue());
    };

    // REQUIRES: arguments has a length of 2.
    // EFFECTS: returns the little endian number represented by the two arguments given.
    public static ModeAction getAbsolute = (Address[] arguments, CPU cpu) -> {
        int pointer = arguments[0].getValue() + arguments[1].getValue() * 256;
        return cpu.readMemory(pointer);
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: returns the argument (interpreted as a signed bit) added to the program counter (registerPC)
    public static ModeAction getRelative = (Address[] arguments, CPU cpu) -> {
        int signedArgument = arguments[0].getValue() < 128 ? arguments[0].getValue() : arguments[0].getValue() - 256;
        return new Address(signedArgument + cpu.getRegisterPC().getValue(), 0, 65536);
    };

    // REQUIRES: arguments has a length of 2.
    // EFFECTS: returns the little endian number represented by the two arguments given.
    public static ModeAction getIndirect = (Address[] arguments, CPU cpu) -> {
        int pointerOne = arguments[0].getValue() + arguments[1].getValue() * 256;
        int pointerTwo = pointerOne + 1;

        // Indirect runs incorrectly under certain conditions. See:
        // http://www.obelisk.me.uk/6502/reference.html#JMP
        boolean broken = arguments[0].getValue() == 255;
        if (broken) {
            pointerTwo -= (Integer.parseInt("FF", 16) + 1);
        }

        int addressOne = cpu.readMemory(pointerOne).getValue();
        int addressTwo = cpu.readMemory(pointerTwo).getValue();
        return cpu.readMemory(addressOne + addressTwo * 256);
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: adds the little endian number represented by the two arguments given added to registerX and returns
    //          the associated address on the zero page
    public static ModeAction getZeroPageIndexedX = (Address[] arguments, CPU cpu) -> {
        int rawAddress = (arguments[0].getValue() + cpu.getRegisterX().getValue());
        int zeroPageAddress = rawAddress % Integer.parseInt("100", 16);

        return cpu.readMemory(zeroPageAddress);
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: adds the little endian number represented by the two arguments given added to registerY and returns
    //          the associated address on the zero page
    public static ModeAction getZeroPageIndexedY = (Address[] arguments, CPU cpu) -> {
        int rawAddress = (arguments[0].getValue() + cpu.getRegisterY().getValue());
        int zeroPageAddress = rawAddress % Integer.parseInt("100", 16);

        return cpu.readMemory(zeroPageAddress);
    };

    // REQUIRES: arguments has a length of 2.
    // EFFECTS: returns the little endian number represented by the two arguments given added to registerX.
    public static ModeAction getAbsoluteIndexedX = (Address[] arguments, CPU cpu) -> {
        int pointer = arguments[0].getValue() + arguments[1].getValue() * 256 + cpu.getRegisterX().getValue();
        if (cpu.getRegisterX().getValue() != 0) {
            cpu.incrementCycles(3);
        }

        return cpu.readMemory(pointer % Integer.parseInt("10000", 16));
    };

    // REQUIRES: arguments has a length of 2.
    // EFFECTS: returns the little endian number represented by the two arguments given added to registerY.
    public static ModeAction getAbsoluteIndexedY = (Address[] arguments, CPU cpu) -> {
        int pointer = arguments[0].getValue() + arguments[1].getValue() * 256 + cpu.getRegisterY().getValue();
        if (cpu.getRegisterY().getValue() != 0) {
            cpu.incrementCycles(3);
        }

        return cpu.readMemory(pointer % Integer.parseInt("10000", 16));
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: first fetches the 2-byte value in memory at address (argument + registerX) on the zero page. Then,
    // fetches and returns the value in memory at that 2-byte address.
    public static ModeAction getIndexedIndirect = (Address[] arguments, CPU cpu) -> {
        int pointerOne = (arguments[0].getValue() + cpu.getRegisterX().getValue()) % Integer.parseInt("0100", 16);
        int pointerTwo = (pointerOne + 1) % Integer.parseInt("0100", 16);
        int fullPointer = cpu.readMemory(pointerOne).getValue() + cpu.readMemory(pointerTwo).getValue() * 256;
        return cpu.readMemory(fullPointer);
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: fetches the 2-byte value in memory at the argument on the zero page. Then, fetches and returns the value
    // in memory at (that 2-byte address + registerY).
    public static ModeAction getIndirectIndexed = (Address[] arguments, CPU cpu) -> {
        int addressOne = arguments[0].getValue() % Integer.parseInt("0100", 16);
        int addressTwo = (addressOne + 1) % Integer.parseInt("0100", 16);

        int pointerOne = cpu.readMemory(addressOne).getValue();
        int pointerTwo = cpu.readMemory(addressTwo).getValue();
        int fullPointer = (pointerOne + pointerTwo * 256 + cpu.getRegisterY().getValue());
        if (cpu.getRegisterY().getValue() != 0) {
            cpu.incrementCycles(3);
        }

        return cpu.readMemory(fullPointer % Integer.parseInt("10000", 16));
    };

    static {
        modes = new Mode();
        modes.put("IMPLICIT",            getImplicit);
        modes.put("ACCUMULATOR",         getAccumulator);
        modes.put("IMMEDIATE",           getImmediate);
        modes.put("ZERO_PAGE",           getZeroPage);
        modes.put("ABSOLUTE",            getAbsolute);
        modes.put("RELATIVE",            getRelative);
        modes.put("INDIRECT",            getIndirect);
        modes.put("ZERO_PAGE_INDEXED_X", getZeroPageIndexedX);
        modes.put("ZERO_PAGE_INDEXED_Y", getZeroPageIndexedY);
        modes.put("ABSOLUTE_INDEXED_X",  getAbsoluteIndexedX);
        modes.put("ABSOLUTE_INDEXED_Y",  getAbsoluteIndexedY);
        modes.put("INDEXED_INDIRECT",    getIndexedIndirect);
        modes.put("INDIRECT_INDEXED",    getIndirectIndexed);
    }
}






