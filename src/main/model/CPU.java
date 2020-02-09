package model;

import java.util.Arrays;

public class CPU {
    // Constants
    private static final int RAM_SIZE                 = (int) Math.pow(2, 11);

    public static final int INITIAL_REGISTER_A        = Integer.parseInt("0000", 16);
    public static final int INITIAL_REGISTER_X        = Integer.parseInt("0000", 16);
    public static final int INITIAL_REGISTER_Y        = Integer.parseInt("0000", 16);
    public static final int INITIAL_REGISTER_PC       = Integer.parseInt("C000", 16);
    public static final int INITIAL_REGISTER_S        = Integer.parseInt("00FD", 16);

    public static final int MINIMUM_REGISTER_A        = Integer.parseInt("0000", 16);
    public static final int MINIMUM_REGISTER_X        = Integer.parseInt("0000", 16);
    public static final int MINIMUM_REGISTER_Y        = Integer.parseInt("0000", 16);
    public static final int MINIMUM_REGISTER_PC       = Integer.parseInt("0000", 16);
    public static final int MINIMUM_REGISTER_S        = Integer.parseInt("0000", 16);
    
    public static final int MAXIMUM_REGISTER_A        = Integer.parseInt("00FF", 16);
    public static final int MAXIMUM_REGISTER_X        = Integer.parseInt("00FF", 16);
    public static final int MAXIMUM_REGISTER_Y        = Integer.parseInt("00FF", 16);
    public static final int MAXIMUM_REGISTER_PC       = Integer.parseInt("FFFF", 16);
    public static final int MAXIMUM_REGISTER_S        = Integer.parseInt("00FF", 16);

    public static final int OFFSET_REGISTER_A         = Integer.parseInt("0000", 16);
    public static final int OFFSET_REGISTER_X         = Integer.parseInt("0000", 16);
    public static final int OFFSET_REGISTER_Y         = Integer.parseInt("0000", 16);
    public static final int OFFSET_REGISTER_PC        = Integer.parseInt("C000", 16);
    public static final int OFFSET_REGISTER_S         = Integer.parseInt("0100", 16);

    public static final int INITIAL_CYCLES            = Integer.parseInt("0000", 16);
    public static final int INITIAL_RAM_STATE         = Integer.parseInt("0000", 16);

    // CPU Flags
    protected int flagC;  // Carry
    protected int flagZ;  // Zero
    protected int flagI;  // Interrupt Disable
    protected int flagD;  // Decimal
    protected int flagB;  // Break
    // 7 flags in one byte; position 5 is empty.
    protected int flagV;  // Overflow
    protected int flagN;  // Negative

    // Registers / Cycles
    private Address registerA;  // Accumulator for ALU
    private Address registerX;  // Index
    private Address registerY;  // Index
    private Address registerPC; // The program counter
    private Address registerS;  // The stack pointer
    private int cycles;

    private Mapper mapper;

    // Memory
    // TODO: is it right to make ram have default visibility?
    Address[] ram;

    // EFFECTS: initializes the RAM and STACK and calls reset() to reset all values in the cpu to their default states.
    public CPU() {
        init();
        reset();
    }

    // EFFECTS: initializes the RAM and STACK with their appropriate sizes.
    private void init() {
        ram = new Address[CPU.RAM_SIZE];
    }

    // EFFECTS: resets all values in the cpu (registers, cycles, ram, stack) to their default states.
    private void reset() {
        registerA  = new Address(CPU.INITIAL_REGISTER_A,   CPU.MINIMUM_REGISTER_A,  CPU.MAXIMUM_REGISTER_A);
        registerX  = new Address(CPU.INITIAL_REGISTER_X,   CPU.MINIMUM_REGISTER_X,  CPU.MAXIMUM_REGISTER_X);
        registerY  = new Address(CPU.INITIAL_REGISTER_Y,   CPU.MINIMUM_REGISTER_Y,  CPU.MAXIMUM_REGISTER_Y);
        registerPC = new Address(CPU.INITIAL_REGISTER_PC,  CPU.MINIMUM_REGISTER_PC, CPU.MAXIMUM_REGISTER_PC);
        registerS  = new Address(CPU.INITIAL_REGISTER_S,   CPU.MINIMUM_REGISTER_S,  CPU.MAXIMUM_REGISTER_S);
        cycles     = CPU.INITIAL_CYCLES;

        // Note: ram state and stack pointer considered unreliable after reset.
        for (int i = 0; i < ram.length; i++) {
            ram[i] = new Address(CPU.INITIAL_RAM_STATE, i,0, 255);
        }
    }

    // MODIFIES: All registers, all flags, the ram, the stack, and the mapper may change.
    // EFFECTS: Cycles the cpu through one instruction, and updates the cpu's state as necessary.
    public void cycle() {
        Address valueAtProgramCounter = readMemory(registerPC.getValue());
        Instruction instruction = Instruction.getInstructions()[valueAtProgramCounter.getValue()];
        Address[] modeArguments = new Address[instruction.getNumArguments()];

        for (int i = 0; i < instruction.getNumArguments(); i++) {
            modeArguments[i] = readMemory(registerPC.getValue() + i + 1);
        }

        registerPC.setValue(registerPC.getValue() + instruction.getNumArguments() + 1);
        cycles += instruction.getNumCycles();

        Address opcodeArgument = Mode.runMode(instruction.getMode(), modeArguments, this);
        Opcode.runOpcode(instruction.getOpcode(), opcodeArgument, this);
    }

    // REQUIRES: address is in between 0x0000 and 0xFFFF, inclusive.
    // EFFECTS: returns the value of the memory at the given address.
    // see the table below for a detailed description of what is stored at which address.
    protected Address readMemory(int address) {
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
            return new Address(0); // TODO add when the ppu is implemented. remember to add mirrors.
        } else if (address <= Integer.parseInt("4017", 16)) {       // NES APU and I/O registers
            return new Address(0); // TODO add when the apu is implemented.
        } else if (address <= Integer.parseInt("401F", 16)) {       // APU and I/O functionality that is
                                                                              // normally disabled.
            return new Address(0); // TODO add when the apu is implemented.
        } else {
            return mapper.readMemory(address);
        }
    }

    // REQUIRES: address is in between 0x0000 and 0xFFFF, inclusive.
    // MODIFIES: ram
    // EFFECTS: check the table below for a detailed explanation of what is affected and how.
    protected void writeMemory(int pointer, int rawValue) {
        if (pointer == 0 && rawValue == 255) {
            int x = 2;
        }
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

        if        (pointer <= Integer.parseInt("1FFF",16)) {        // 2KB internal RAM  + its mirrors
            ram[pointer % Integer.parseInt("0800",16)].setValue(value);/*
        } else if (pointer <= Integer.parseInt("3FFF",16)) {        // NES PPU registers + its mirrors
            // TODO add when the ppu is implemented. remember to add mirrors.
        } else if (pointer <= Integer.parseInt("4017", 16)) {       // NES APU and I/O registers
            // TODO add when the apu is implemented.
        } else if (pointer <= Integer.parseInt("401F", 16)) {       // APU and I/O functionality that is
                                                                             // normally disabled.
            // TODO add when the apu is implemented.
            */
        } else {
            mapper.writeMemory(pointer, rawValue);
        }
    }

    // REQUIRES: 0 <= value < 2^8
    // MODIFIES: registerS, stack
    // EFFECTS: value is pushed onto the stack, registerS is decremented.
    public void pushStack(int value) {
        writeMemory(CPU.OFFSET_REGISTER_S + registerS.getValue(), value);

        setRegisterS(getRegisterS().getValue() - 1);
        int x = 2;
        if (registerS.getValue() < 0) {
            setRegisterS(255);
        }
    }

    // MODIFIES: registerS, stack
    // EFFECTS: value is pulled from the stack and returned, registerS is incremented.
    public Address pullStack() {
        setRegisterS(getRegisterS().getValue() + 1);
        if (registerS.getValue() > 255) {
            setRegisterS(0);
        }

        return readMemory(CPU.OFFSET_REGISTER_S + registerS.getValue());
    }

    // EFFECTS: peeks into the stack.
    public Address peekStack() {
        return readMemory(CPU.OFFSET_REGISTER_S + registerS.getValue() + 1);
    }

    // REQUIRES: status can be represented as an 8bit binary integer
    // EFFECTS: use the flags to construct the status by concatenating them like this:
    // VN11DIZC where the 4th and 5th bits (little endian) are 1.
    public int getStatus() {
        return (int) (getFlagC() * Math.pow(2, 0))
             + (int) (getFlagZ() * Math.pow(2, 1))
             + (int) (getFlagI() * Math.pow(2, 2))
             + (int) (getFlagD() * Math.pow(2, 3))
             + (int) (1          * Math.pow(2, 4))
             + (int) (1          * Math.pow(2, 5)) // bit 5 in the flags byte is empty
             + (int) (getFlagV() * Math.pow(2, 6))
             + (int) (getFlagN() * Math.pow(2, 7));
    }

    // REQUIRES: status can be represented as an 8bit binary integer
    // MODIFIES: sets the flags in this way:
    // flagC is the 0th bit of status
    // flagZ is the 1st bit of status
    // flagI is the 2nd bit of status
    // flagD is the 3rd bit of status
    //          the 4th bit is disregarded
    //          the 5th bit is disregarded
    // flagV is the 6th bit of status
    // flagN is the 7th bit of status
    public void setStatus(int status) {
        setFlagC(Util.getNthBit(status, 0));
        setFlagZ(Util.getNthBit(status, 1));
        setFlagI(Util.getNthBit(status, 2));
        setFlagD(Util.getNthBit(status, 3));
        // bit 4 in the flags byte is disregarded
        // bit 5 in the flags byte is disregarded
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
    public Address getRegisterA() {
        return registerA;
    }

    // EFFECTS: returns the X Register
    public Address getRegisterX() {
        return registerX;
    }

    // EFFECTS: returns the Y Register
    public Address getRegisterY() {
        return registerY;
    }

    // EFFECTS: returns the PC Register (program counter)
    public Address getRegisterPC() {
        return registerPC;
    }

    // EFFECTS: returns the S Register (stack pointer)
    public Address getRegisterS() {
        return registerS;
    }

    // EFFECTS: returns the number of cycles
    public int getCycles() {
        return cycles;
    }

    // EFFECTS: returns the mapper
    public Mapper getMapper() {
        return mapper;
    }

    // MODIFIES: registerA
    // EFFECTS: sets registerA to a new value wrapped around (0...MAXIMUM_REGISTER_A_VALUE)
    // example: setRegisterA(256) sets registerS to 0.
    // example: setRegisterA(-1)  sets registerS to MAXIMUM_REGISTER_A_VALUE - 1.
    public void setRegisterA(int registerA) {
        this.registerA.setValue(registerA);
    }

    // MODIFIES: registerX
    // EFFECTS: sets registerX to a new value wrapped around (0...MAXIMUM_REGISTER_X_VALUE)
    // example: setRegisterX(256) sets registerS to 0.
    // example: setRegisterX(-1)  sets registerS to MAXIMUM_REGISTER_X_VALUE - 1.
    public void setRegisterX(int registerX) {
        this.registerX.setValue(registerX);
    }

    // MODIFIES: registerY
    // EFFECTS: sets registerY to a new value wrapped around (0...MAXIMUM_REGISTER_Y_VALUE)
    // example: setRegisterY(256) sets registerY to 0.
    // example: setRegisterY(-1)  sets registerY to MAXIMUM_REGISTER_Y_VALUE - 1.
    public void setRegisterY(int registerY) {
        this.registerY.setValue(registerY);
    }

    // MODIFIES: registerPC
    // EFFECTS: sets registerPC to a new value wrapped around REGISTER_PC_OFFSET + (0...MAXIMUM_REGISTER_PC_VALUE)
    public void setRegisterPC(int registerPC) {
        this.registerPC.setValue(registerPC);
    }

    // MODIFIES: registerS
    // EFFECTS: sets registerS to a new value wrapped around (0...MAXIMUM_REGISTER_S_VALUE)
    // example: setRegisterS(256) sets registerS to 0.
    // example: setRegisterS(-1)  sets registerS to MAXIMUM_REGISTER_S_VALUE - 1.
    public void setRegisterS(int registerS) {
        this.registerS.setValue(registerS);
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

    // MODIFIES: mapper
    // EFFECTS: the mapper is set to the given mapper
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
}
