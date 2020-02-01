package model;

import java.util.Arrays;

@SuppressWarnings("PointlessArithmeticExpression")
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
    // TODO: is it right to make ram have default visibility?
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

    // REQUIRES: address is in between 0x0000 and 0xFFFF, inclusive.
    // EFFECTS: returns the value of the memory at the given address.
    // see the table below for a detailed description of what is stored at which address.
    protected int readMemory(int address) {
        // https://wiki.nesdev.com/w/index.php/CPU_memory_map
        // ADDRESS RANGE | SIZE  | DEVICE
        // $0000 - $07FF | $0800 | 2KB internal RAM
        // $0800 - $0FFF | $0800 |
        // $1000 - $17FF | $0800 | Mirrors of $0000-$07FF
        // $1800 - $1FFF | $0800 |
        // $2000 - $2007 | $0008 | NES PPU registers
        // $2008 - $3FFF | $1FF8 | Mirrors of $2000-$2007 (repeats every 8 bytes)
        // $4000 - $4017 | $0018 | NES APU and I/O registers
        // $4018 - $401F | $0008 | APU and I/O functionality that is normally disabled.
        // $4020 - $FFFF | $BFE0 | Cartridge space: PRG ROM, PRG RAM, and mapper registers

        if        (address <= Integer.parseInt("1FFF",16)) {        // 2KB internal RAM  + its mirrors
            return ram[address % Integer.parseInt("0800",16)];
        } else if (address <= Integer.parseInt("3FFF",16)) {        // NES PPU registers + its mirrors
            return 0; // TODO add when the ppu is implemented. remember to add mirrors.
        } else if (address <= Integer.parseInt("4017", 16)) {       // NES APU and I/O registers
            return 0; // TODO add when the apu is implemented.
        } else if (address <= Integer.parseInt("401F", 16)) {       // APU and I/O functionality that is
                                                                             // normally disabled.
            return 0; // TODO add when the apu is implemented.
        } else {
            return 0; // TODO will complete when mapper added
        }
    }

    // REQUIRES: address is in between 0x0000 and 0xFFFF, inclusive.
    // MODIFIES: ram
    // EFFECTS: check the table below for a detailed explanation of what is affected and how.
    protected void writeMemory(int address, int rawValue) {
        // https://wiki.nesdev.com/w/index.php/CPU_memory_map
        // ADDRESS RANGE | SIZE  | DEVICE
        // $0000 - $07FF | $0800 | 2KB internal RAM
        // $0800 - $0FFF | $0800 |
        // $1000 - $17FF | $0800 | Mirrors of $0000-$07FF
        // $1800 - $1FFF | $0800 |
        // $2000 - $2007 | $0008 | NES PPU registers
        // $2008 - $3FFF | $1FF8 | Mirrors of $2000-$2007 (repeats every 8 bytes)
        // $4000 - $4017 | $0018 | NES APU and I/O registers
        // $4018 - $401F | $0008 | APU and I/O functionality that is normally disabled.
        // $4020 - $FFFF | $BFE0 | Cartridge space: PRG ROM, PRG RAM, and mapper registers
        int value = rawValue % 256;
        if (value < 0) {
            value += 256;
        }

        if        (address <= Integer.parseInt("1FFF",16)) {        // 2KB internal RAM  + its mirrors
            ram[address % Integer.parseInt("0800",16)] = value;
        } else if (address <= Integer.parseInt("3FFF",16)) {        // NES PPU registers + its mirrors
            // TODO add when the ppu is implemented. remember to add mirrors.
        } else if (address <= Integer.parseInt("4017", 16)) {       // NES APU and I/O registers
            // TODO add when the apu is implemented.
        } else if (address <= Integer.parseInt("401F", 16)) {       // APU and I/O functionality that is
                                                                             // normally disabled.
            // TODO add when the apu is implemented.
        } else {
            // TODO will complete when mapper added
        }
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

    // REQUIRES: status can be represented as an 8bit binary integer
    // EFFECTS: use the flags to construct the status by concatenating them like this:
    // VN0BDIZC where the 5th bit (little endian) is 0.
    @SuppressWarnings({"checkstyle:Indentation", "CheckStyle"})
    public int getStatus() {
       return (int) (getFlagC() * Math.pow(2, 0))
            + (int) (getFlagZ() * Math.pow(2, 1))
            + (int) (getFlagI() * Math.pow(2, 2))
            + (int) (getFlagD() * Math.pow(2, 3))
            + (int) (getFlagB() * Math.pow(2, 4))
            + (int) (0          * Math.pow(2, 5)) // bit 5 in the flags byte is empty
            + (int) (getFlagV() * Math.pow(2, 6))
            + (int) (getFlagN() * Math.pow(2, 7));
    }

    // REQUIRES: status can be represented as an 8bit binary integer
    // MODIFIES: sets the flags in this way:
    // flagC is the 0th bit of status
    // flagZ is the 1st bit of status
    // flagI is the 2nd bit of status
    // flagD is the 3rd bit of status
    // flagB is the 4th bit of status
    //          the 5th bit is not used
    // flagV is the 6th bit of status
    // flagN is the 7th bit of status
    public void setStatus(int status) {
        setFlagC(Util.getNthBit(status, 0));
        setFlagZ(Util.getNthBit(status, 1));
        setFlagI(Util.getNthBit(status, 2));
        setFlagD(Util.getNthBit(status, 3));
        setFlagB(Util.getNthBit(status, 4));
        // bit 5 in the flags byte is empty
        setFlagV(Util.getNthBit(status, 6));
        setFlagN(Util.getNthBit(status, 7));
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

    // MODIFIES: registerA
    // EFFECTS: sets registerA to a new value wrapped around (0...MAXIMUM_REGISTER_A_VALUE)
    // example: setRegisterA(256) sets registerS to 0.
    // example: setRegisterA(-1)  sets registerS to MAXIMUM_REGISTER_A_VALUE - 1.
    public void setRegisterA(int registerA) {
        this.registerA = registerA % CPU.MAXIMUM_REGISTER_A_VALUE;
        if (registerA < 0) {
            this.registerA += CPU.MAXIMUM_REGISTER_A_VALUE;
        }
    }

    // MODIFIES: registerX
    // EFFECTS: sets registerX to a new value wrapped around (0...MAXIMUM_REGISTER_X_VALUE)
    // example: setRegisterX(256) sets registerS to 0.
    // example: setRegisterX(-1)  sets registerS to MAXIMUM_REGISTER_X_VALUE - 1.
    public void setRegisterX(int registerX) {
        this.registerX = registerX % CPU.MAXIMUM_REGISTER_X_VALUE;
        if (registerX < 0) {
            this.registerX += CPU.MAXIMUM_REGISTER_X_VALUE;
        }
    }

    // MODIFIES: registerY
    // EFFECTS: sets registerY to a new value wrapped around (0...MAXIMUM_REGISTER_Y_VALUE)
    // example: setRegisterY(256) sets registerY to 0.
    // example: setRegisterY(-1)  sets registerY to MAXIMUM_REGISTER_Y_VALUE - 1.
    public void setRegisterY(int registerY) {
        this.registerY = registerY % CPU.MAXIMUM_REGISTER_Y_VALUE;
        if (registerY < 0) {
            this.registerY += CPU.MAXIMUM_REGISTER_Y_VALUE;
        }
    }

    // MODIFIES: registerPC
    // EFFECTS: sets registerPC to a new value wrapped around (0...MAXIMUM_REGISTER_PC_VALUE)
    // example: setRegisterPC(256) sets registerA to 0.
    // example: setRegisterPC(-1)  sets registerA to MAXIMUM_REGISTER_PC_VALUE - 1.
    public void setRegisterPC(int registerPC) {
        this.registerPC = registerPC % CPU.MAXIMUM_REGISTER_PC_VALUE;
        if (registerPC < 0) {
            this.registerPC += CPU.MAXIMUM_REGISTER_PC_VALUE;
        }
    }

    // MODIFIES: registerS
    // EFFECTS: sets registerS to a new value wrapped around (0...MAXIMUM_REGISTER_S_VALUE)
    // example: setRegisterS(256) sets registerS to 0.
    // example: setRegisterS(-1)  sets registerS to MAXIMUM_REGISTER_S_VALUE - 1.
    public void setRegisterS(int registerS) {
        this.registerS = registerS % CPU.MAXIMUM_REGISTER_S_VALUE;
        if (registerS < 0) {
            this.registerS += CPU.MAXIMUM_REGISTER_S_VALUE;
        }
    }

    // MODIFIES: registerP
    // EFFECTS: sets registerP to a new value wrapped around (0...MAXIMUM_REGISTER_P_VALUE)
    // example: setRegisterP(256) sets registerP to 0.
    // example: setRegisterP(-1)  sets registerP to MAXIMUM_REGISTER_P_VALUE - 1.
    public void setRegisterP(int registerP) {
        this.registerP = registerP % CPU.MAXIMUM_REGISTER_P_VALUE;
        if (registerP < 0) {
            this.registerP += CPU.MAXIMUM_REGISTER_P_VALUE;
        }
    }

    // MODIFIES: flagC
    // REQUIRES: flagC is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // EFFECTS: sets flagC to the given value
    public void setFlagC(int flagC) {
        this.flagC = flagC;
    }

    // MODIFIES: flagZ
    // REQUIRES: flagZ is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // EFFECTS: sets flagZ to the given value
    public void setFlagZ(int flagZ) {
        this.flagZ = flagZ;
    }

    // MODIFIES: flagI
    // REQUIRES: flagI is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // EFFECTS: sets flagI to the given value
    public void setFlagI(int flagI) {
        this.flagI = flagI;
    }

    // MODIFIES: flagD
    // REQUIRES: flagD is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // EFFECTS: sets flagD to the given value
    public void setFlagD(int flagD) {
        this.flagD = flagD;
    }

    // MODIFIES: flagB
    // REQUIRES: flagB is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // EFFECTS: sets flagB to the given value
    public void setFlagB(int flagB) {
        this.flagB = flagB;
    }

    // MODIFIES: flagV
    // REQUIRES: flagV is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // EFFECTS: sets flagV to the given value
    public void setFlagV(int flagV) {
        this.flagV = flagV;
    }

    // MODIFIES: flagN
    // REQUIRES: flagN is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // EFFECTS: sets flagN to the given value
    public void setFlagN(int flagN) {
        this.flagN = flagN;
    }
}
