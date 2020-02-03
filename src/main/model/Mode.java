package model;

import java.util.HashMap;

@SuppressWarnings("CodeBlock2Expr")
public class Mode extends HashMap<String, Mode.ModeAction> {
    private static Mode modes;

    public interface ModeAction {
        int run(int[] arguments, CPU cpu);
    }

    // REQUIRES: arguments is a valid argument for the specified ModeAction.
    // details given for what a "valid argument" is under each ModeAction definition.
    // EFFECTS: Runs the given Addressing Mode with the given argument, and returns the proper value from the cpu.
    public static int runMode(String mode, int[] arguments, CPU cpu) {
        return modes.get(mode).run(arguments, cpu);
    }

    // REQUIRES: arguments has a length of either 0 or 1.
    // EFFECTS: An instruction with an implicit addressing mode won't use its argument anyway,
    //          so it doesn't matter what's returned here. Nevertheless, returns 0.
    public static ModeAction getImplicit = (int[] arguments, CPU cpu) -> {
        return 0;
    };

    // REQUIRES: arguments has a length of 1.
    // EFFECTS: returns the value of the accumulator.
    public static ModeAction getAccumulator = (int[] arguments, CPU cpu) -> {
        return cpu.getRegisterA();
    };

    // REQUIRES: arguments has a length of 1 or 2.
    // EFFECTS: returns the first argument in the list of arguments.
    public static ModeAction getImmediate = (int[] arguments, CPU cpu) -> {
        return arguments[0];
    };

    public static ModeAction getZeroPage = (int[] arguments, CPU cpu) -> {
        return 0; // stub
    };

    public static ModeAction getAbsolute = (int[] arguments, CPU cpu) -> {
        return 0; // stub
    };

    public static ModeAction getRelative = (int[] arguments, CPU cpu) -> {
        return 0; // stub
    };

    public static ModeAction getIndirect = (int[] arguments, CPU cpu) -> {
        return 0; // stub
    };

    public static ModeAction getZeroPageIndexedX = (int[] arguments, CPU cpu) -> {
        return 0; // stub
    };

    public static ModeAction getZeroPageIndexedY = (int[] arguments, CPU cpu) -> {
        return 0; // stub
    };

    public static ModeAction getAbsoluteIndexedX = (int[] arguments, CPU cpu) -> {
        return 0; // stub
    };

    public static ModeAction getAbsoluteIndexedY = (int[] arguments, CPU cpu) -> {
        return 0; // stub
    };

    public static ModeAction getIndexedIndirect = (int[] arguments, CPU cpu) -> {
        return 0; // stub
    };

    public static ModeAction getIndirectIndexed = (int[] arguments, CPU cpu) -> {
        return 0; // stub
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






