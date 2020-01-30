package model;

import java.util.Arrays;

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
    protected void pushStack(int value) {
        stack[registerS] = value;
        registerS--;
    }

    // MODIFIES: registerS, stack
    // EFFECTS: value is pulled from the stack and returned, registerS is incremented.
    protected int pullStack() {
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
        this.registerA = registerA;
    }

    public void setRegisterX(int registerX) {
        this.registerX = registerX;
    }

    public void setRegisterY(int registerY) {
        this.registerY = registerY;
    }

    public void setRegisterPC(int registerPC) {
        this.registerPC = registerPC;
    }

    public void setRegisterS(int registerS) {
        this.registerS = registerS;
    }

    public void setRegisterP(int registerP) {
        this.registerP = registerP;
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
