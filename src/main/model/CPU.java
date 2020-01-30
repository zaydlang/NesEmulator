package model;

public class CPU {
    // Constants
    private static final int RAM_SIZE               = (int) Math.pow(2, 11);
    private static final int STACK_SIZE             = (int) Math.pow(2, 8);

    public static final int INITIAL_REGISTER_A     = 0;
    public static final int INITIAL_REGISTER_X     = 0;
    public static final int INITIAL_REGISTER_Y     = 0;
    public static final int INITIAL_REGISTER_PC    = 0;
    public static final int INITIAL_REGISTER_P     = 34;
    public static final int INITIAL_REGISTER_S     = "FD".getBytes()[0];
    public static final int INITIAL_CYCLES         = 0;
    public static final int INITIAL_RAM_STATE      = 0;
    public static final int INITIAL_STACK_STATE    = 0;

    // CPU Flags
    protected int flagC;  // Carry
    protected int flagZ;  // Zero
    protected int flagI;  // Interrupt Disable
    protected int flagD;  // Decimal
    protected int flagB;  // Break
    // 7 flags in one byte; position 5 is empty.
    protected int flagV;  // Overflow
    protected int flagN;  // Negative

    // Registers
    protected int registerA;  // Accumulator for ALU
    protected int registerX;  // Index
    protected int registerY;  // Index
    protected int registerPC; // The program counter
    protected int registerS;  // The stack pointer
    protected int registerP;  // The status register
    protected int cycles;

    // Stack is decreasing stack.
    private int[] stack;

    // Memory
    int[] ram;

    public int getFlagC() {
        return flagC;
    }

    public int getFlagZ() {
        return flagZ;
    }

    public int getFlagI() {
        return flagI;
    }

    public int getFlagD() {
        return flagD;
    }

    public int getFlagB() {
        return flagB;
    }

    public int getFlagV() {
        return flagV;
    }

    public int getFlagN() {
        return flagN;
    }

    public int getRegisterA() {
        return registerA;
    }

    public int getRegisterX() {
        return registerX;
    }

    public int getRegisterY() {
        return registerY;
    }

    public int getRegisterPC() {
        return registerPC;
    }

    public int getRegisterS() {
        return registerS;
    }

    public int getRegisterP() {
        return registerP;
    }

    public int getCycles() {
        return cycles;
    }

    public int[] getRam() {
        return ram;
    }

    public int peekStack() {
        return 0;
    }
}
