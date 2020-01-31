package model;

import java.util.Arrays;

public class CPU {
    // Constants
    private static final int RAM_SIZE                 = (int) Math.pow(2, 11);
    private static final int STACK_SIZE               = (int) Math.pow(2, 8);

    public static final int INITIAL_REGISTER_A        = 0;
    public static final int INITIAL_REGISTER_X        = 0;
    public static final int INITIAL_REGISTER_Y        = 0;

    public static final int INITIAL_REGISTER_PC       = 0;
    public static final int INITIAL_REGISTER_P        = 34;
    public static final int INITIAL_REGISTER_S        = "FD".getBytes()[0];
    public static final int INITIAL_CYCLES            = 0;
    public static final int INITIAL_RAM_STATE         = 0;
    public static final int INITIAL_STACK_STATE       = 0;

    public static final int MAXIMUM_REGISTER_A_VALUE  = (int) Math.pow(2, 8 * 1);
    public static final int MAXIMUM_REGISTER_X_VALUE  = (int) Math.pow(2, 8 * 1);
    public static final int MAXIMUM_REGISTER_Y_VALUE  = (int) Math.pow(2, 8 * 1);
    public static final int MAXIMUM_REGISTER_PC_VALUE = (int) Math.pow(2, 8 * 2);
    public static final int MAXIMUM_REGISTER_S_VALUE  = (int) Math.pow(2, 8 * 1);
    public static final int MAXIMUM_REGISTER_P_VALUE  = (int) Math.pow(2, 6);

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
    private int registerA;  // Accumulator for ALU
    private int registerX;  // Index
    private int registerY;  // Index
    private int registerPC; // The program counter
    private int registerS;  // The stack pointer
    private int registerP;  // The status register
    private int cycles;

    // Stack is decreasing stack.
    private int[] stack;

    // Memory
    int[] ram;

    // EFFECTS: initializes the RAM and STACK and calls reset() to reset all values in the cpu to their default states.
    public CPU() {
        init();
        reset();
    }

    // EFFECTS: initializes the RAM and STACK with their appropriate sizes.
    private void init() {
        ram   = new int[CPU.RAM_SIZE];
        stack = new int[CPU.STACK_SIZE];
    }

    // EFFECTS: resets all values in the cpu (registers, cycles, ram, stack) to their default states.
    private void reset() {
        registerA  = CPU.INITIAL_REGISTER_A;
        registerX  = CPU.INITIAL_REGISTER_X;
        registerY  = CPU.INITIAL_REGISTER_Y;
        registerPC = CPU.INITIAL_REGISTER_PC;
        registerP  = CPU.INITIAL_REGISTER_P;
        registerS  = CPU.INITIAL_REGISTER_S;
        cycles     = CPU.INITIAL_CYCLES;

        // Note: ram state and stack pointer considered unreliable after reset.
        Arrays.fill(ram,   CPU.INITIAL_RAM_STATE);
        Arrays.fill(stack, CPU.INITIAL_STACK_STATE);
    }

    // MODIFIES: registerS, stack
    // EFFECTS: value is pushed onto the stack, registerS is decremented.
    public void pushStack(int value) {
        stack[registerS] = value;
        registerS--;
    }

    // MODIFIES: registerS, stack
    // EFFECTS: value is pulled from the stack and returned, registerS is incremented.
    public int pullStack() {
        registerS++;
        return stack[registerS];
    }

    // EFFECTS: peeks into the stack.
    public int peekStack() {
        return stack[registerS + 1];
    }

    // EFFECTS: returns the C flag
    public int getFlagC() {
        return flagC;
    }

    // EFFECTS: returns the Z flag
    public int getFlagZ() {
        return flagZ;
    }

    // EFFECTS: returns the I flag
    public int getFlagI() {
        return flagI;
    }

    // EFFECTS: returns the D flag
    public int getFlagD() {
        return flagD;
    }

    // EFFECTS: returns the B flag
    public int getFlagB() {
        return flagB;
    }

    // EFFECTS: returns the V flag
    public int getFlagV() {
        return flagV;
    }

    // EFFECTS: returns the N flag
    public int getFlagN() {
        return flagN;
    }

    // EFFECTS: returns the A Register
    public int getRegisterA() {
        return registerA;
    }

    // EFFECTS: returns the X Register
    public int getRegisterX() {
        return registerX;
    }

    // EFFECTS: returns the Y Register
    public int getRegisterY() {
        return registerY;
    }

    // EFFECTS: returns the PC Register (program counter)
    public int getRegisterPC() {
        return registerPC;
    }

    // EFFECTS: returns the S Register (stack pointer)
    public int getRegisterS() {
        return registerS;
    }

    // EFFECTS: returns the P Register
    public int getRegisterP() {
        return registerP;
    }

    // EFFECTS: returns the number of cycles
    public int getCycles() {
        return cycles;
    }

    // EFFECTS: returns the RAM
    public int[] getRam() {
        return ram;
    }

    public void setRegisterA(int registerA) {
        this.registerA = registerA % CPU.MAXIMUM_REGISTER_A_VALUE;
        if (registerA < 0) {
            this.registerA += CPU.MAXIMUM_REGISTER_A_VALUE;
        }
    }

    public void setRegisterX(int registerX) {
        this.registerX = registerX % CPU.MAXIMUM_REGISTER_X_VALUE;
        if (registerX < 0) {
            this.registerX += CPU.MAXIMUM_REGISTER_X_VALUE;
        }
    }

    public void setRegisterY(int registerY) {
        this.registerY = registerY % CPU.MAXIMUM_REGISTER_Y_VALUE;
        if (registerY < 0) {
            this.registerY += CPU.MAXIMUM_REGISTER_Y_VALUE;
        }
    }

    public void setRegisterPC(int registerPC) {
        this.registerPC = registerPC % CPU.MAXIMUM_REGISTER_PC_VALUE;
        if (registerPC < 0) {
            this.registerPC += CPU.MAXIMUM_REGISTER_PC_VALUE;
        }
    }

    public void setRegisterS(int registerS) {
        this.registerS = registerS % CPU.MAXIMUM_REGISTER_S_VALUE;
        if (registerS < 0) {
            this.registerS += CPU.MAXIMUM_REGISTER_S_VALUE;
        }
    }

    public void setRegisterP(int registerP) {
        this.registerP = registerP % CPU.MAXIMUM_REGISTER_P_VALUE;
        if (registerP < 0) {
            this.registerP += CPU.MAXIMUM_REGISTER_P_VALUE;
        }
    }

    public void setFlagC(int flagC) {
        this.flagC = flagC;
    }

    public void setFlagZ(int flagZ) {
        this.flagZ = flagZ;
    }

    public void setFlagI(int flagI) {
        this.flagI = flagI;
    }

    public void setFlagD(int flagD) {
        this.flagD = flagD;
    }

    public void setFlagB(int flagB) {
        this.flagB = flagB;
    }

    public void setFlagV(int flagV) {
        this.flagV = flagV;
    }

    public void setFlagN(int flagN) {
        this.flagN = flagN;
    }
}
