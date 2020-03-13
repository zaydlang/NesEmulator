package model;

import ui.window.CpuOutput;
import ui.window.CpuViewer;

import java.util.ArrayList;

// Class CPU:
//     Models the 6502 CPU in the NES. Performs all the legal opcodes that are provided with the NES, and completes
//     them in a cycle-accurate manner. One deficiency, though, is that cycles are after the instruction is completed,
//     rather than after each read/write access, like the actual CPU. This can be solved with a state machine but would
//     require a major code rewrite. As this is a major class in the file, I'll provide a detailed description below
//     of what the class contains.
//
// Contains:
//     Registers:
//         registerA     (the Accumulator)
//         registerX     (the X Register)
//         registerY     (the Y Register)
//         registerPC    (the Program Counter)
//         registerS     (the Stack Pointer)
//
//     Flags:
//         flagC         (the carry flag)
//         flagZ         (the zero flag)
//         flagI         (the interrupt disable flag)
//         flagD         (the decimal mode flag)
//         flagB         (the break flag)
//         flagV         (the overflow flag)
//         flagN         (the negative flag)
//
//     Memory:
//         ram
//         mapper        (the exact type of mapper is cartridge-specific)
//
//     Other:
//         cycles        (the current cycle # of the CPU)
//         enabled       (true if enabled, disabled by STP)

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
    public static final int MINIMUM_CYCLES            = 0;
    
    public static final int MAXIMUM_REGISTER_A        = Integer.parseInt("00FF", 16);
    public static final int MAXIMUM_REGISTER_X        = Integer.parseInt("00FF", 16);
    public static final int MAXIMUM_REGISTER_Y        = Integer.parseInt("00FF", 16);
    public static final int MAXIMUM_REGISTER_PC       = Integer.parseInt("FFFF", 16);
    public static final int MAXIMUM_REGISTER_S        = Integer.parseInt("00FF", 16);
    public static final int MAXIMUM_CYCLES            = 340;

    public static final int OFFSET_REGISTER_A         = Integer.parseInt("0000", 16);
    public static final int OFFSET_REGISTER_X         = Integer.parseInt("0000", 16);
    public static final int OFFSET_REGISTER_Y         = Integer.parseInt("0000", 16);
    public static final int OFFSET_REGISTER_PC        = Integer.parseInt("C000", 16);
    public static final int OFFSET_REGISTER_S         = Integer.parseInt("0100", 16);

    public static final int INITIAL_CYCLES            = Integer.parseInt("0007", 16);
    public static final int INITIAL_RAM_STATE         = Integer.parseInt("0000", 16);

    // CPU Flags
    protected int flagC;  // Carry
    protected int flagZ;  // Zero
    protected int flagI;  // Interrupt Disable
    protected int flagD;  // Decimal
    protected int flagB;  // Break
    // 7 flags in one byte; positions 4/5 are empty. flagB is not included in the status.
    protected int flagV;  // Overflow
    protected int flagN;  // Negative

    // Registers / Cycles
    private Address registerA;  // Accumulator for ALU
    private Address registerX;  // Index
    private Address registerY;  // Index
    private Address registerPC; // The program counter
    private Address registerS;  // The stack pointer

    private boolean enabled;
    private int cycle;
    int cyclesRemaining;
    private ArrayList<Address> breakpoints;
    protected boolean nmi;

    // Memory
    protected Address[] ram;
    private Bus bus;

    private CpuOutput loggingOutput;

    // EFFECTS: initializes the RAM and STACK and calls reset() to reset all values in the cpu to their default states.
    public CPU(Bus bus) {
        init(bus);
    }

    // MODIFIES: ram
    // EFFECTS: initializes the RAM and STACK with their appropriate sizes.
    private void init(Bus bus) {
        ram = new Address[CPU.RAM_SIZE];
        this.bus = bus;
    }

    // MODIFIES: registerA, registerX, registerY, registerPC, registerS, cycles, ram
    // EFFECTS: resets all values in the cpu (registers, cycles, ram, stack) to their default states. Enables the CPU.
    void reset() {
        registerA   = new Address(CPU.INITIAL_REGISTER_A,   CPU.MINIMUM_REGISTER_A,  CPU.MAXIMUM_REGISTER_A);
        registerX   = new Address(CPU.INITIAL_REGISTER_X,   CPU.MINIMUM_REGISTER_X,  CPU.MAXIMUM_REGISTER_X);
        registerY   = new Address(CPU.INITIAL_REGISTER_Y,   CPU.MINIMUM_REGISTER_Y,  CPU.MAXIMUM_REGISTER_Y);
        registerPC  = new Address(CPU.INITIAL_REGISTER_PC,  CPU.MINIMUM_REGISTER_PC, CPU.MAXIMUM_REGISTER_PC);
        registerS   = new Address(CPU.INITIAL_REGISTER_S,   CPU.MINIMUM_REGISTER_S,  CPU.MAXIMUM_REGISTER_S);

        cyclesRemaining = 0;
        cycle = CPU.INITIAL_CYCLES;
        breakpoints = new ArrayList<>();

        // Note: ram state and stack pointer considered unreliable after reset.
        for (int i = 0; i < ram.length; i++) {
            ram[i] = new Address(CPU.INITIAL_RAM_STATE, i,0, 255);
        }

        int byteOne = readMemory(Integer.parseInt("FFFC", 16)).getValue();
        int byteTwo = readMemory(Integer.parseInt("FFFD", 16)).getValue();
        setRegisterPC(byteOne + byteTwo * 256);
        //setRegisterPC(Integer.parseInt("C000", 16));     // Uncomment for nestest
        enabled = true;
    }

    // MODIFIES: All registers, all flags, the ram, the stack, and the mapper may change.
    // EFFECTS: Cycles the cpu through one instruction, and updates the cpu's state as necessary.
    public void cycle() {
        handleNMI();

        if (cyclesRemaining <= 1) {
            processInstruction();
        } else {
            cyclesRemaining--;
        }

        incrementCycles(1);
    }

    public void incrementCyclesRemaining(int increment) {
        // System.out.println("Incremented +" + increment + " to " + cyclesRemaining + "!");
        cyclesRemaining += increment;
    }

    private void processInstruction() {
        if (isBreakpoint(registerPC)) {
            setEnabled(false);
        }

        Address valueAtProgramCounter = readMemory(registerPC.getValue());
        Instruction instruction = Instruction.getInstructions()[valueAtProgramCounter.getValue()];
        cyclesRemaining = instruction.getNumCycles();

        Address[] modeArguments = new Address[instruction.getNumArguments()];

        for (int i = 0; i < instruction.getNumArguments(); i++) {
            modeArguments[i] = readMemory(registerPC.getValue() + i + 1);
        }
        String preStatus = getInstructionStatus(instruction, modeArguments);

        registerPC.setValue(registerPC.getValue() + instruction.getNumArguments() + 1);

        //System.out.println(preStatus);
        String log = registerPC.toString() + " " + (bus.getPpu().ppuStatus.getValue() + " " + bus.getPpu().ppuData.getValue());
        Address opcodeArgument = Mode.runMode(instruction.getMode(), modeArguments, this);
        Opcode.runOpcode(instruction.getOpcode(), opcodeArgument, this);
        //incrementCycles(instruction.getNumCycles());

        try {
            //System.out.println(log);
            //loggingOutput.log(log);
        } catch (NullPointerException e) {
            // Do nothing
        }

        //incrementCyclesRemaining(instruction.getNumCycles());
    }

    private void resetCyclesRemaining() {
        Address valueAtProgramCounter = readMemory(registerPC.getValue());
        Instruction instruction = Instruction.getInstructions()[valueAtProgramCounter.getValue()];
        cyclesRemaining = instruction.getNumCycles();
    }

    private void handleNMI() {
        if (nmi) {
            int byteOne = ((getRegisterPC().getValue()) & Integer.parseInt("1111111100000000", 2)) >> 8;
            int byteTwo = ((getRegisterPC().getValue()) & Integer.parseInt("0000000011111111", 2));
            pushStack(byteOne);
            pushStack(byteTwo);
            pushStack(getStatus());

            byteOne = readMemory(Integer.parseInt("FFFA", 16)).getValue();
            byteTwo = readMemory(Integer.parseInt("FFFB", 16)).getValue();
            setRegisterPC(byteTwo * 256 + byteOne);

            nmi = false;
            cyclesRemaining = 8;
        }
    }

    // REQUIRES: arguments.length == instructions.getNumArguments()
    // EFFECTS: returns the current status of a cpu for purposes of logging or printing.
    public String getInstructionStatus(Instruction instruction, Address[] arguments) {
        StringBuilder status = new StringBuilder();               // Examples:
        status.append(instruction.getOpcode()).append(" : ");     // JMP

        for (int i = 0; i < 3; i++) {                             // JMP C0 00
            if (i >= arguments.length) {
                status.append("   ");
            } else {
                status.append(arguments[i]);
                status.append(" ");
            }
        }

        status.append("A: ").append(getRegisterA()).append(" ");  // JMP C0 00 A: 3D
        status.append("X: ").append(getRegisterX()).append(" ");  // JMP C0 00 A: 3D X: C5
        status.append("Y: ").append(getRegisterY()).append(" ");  // JMP C0 00 A: 3D X: C5 Y: 25
        status.append("PC: ").append(getRegisterPC()).append(" ");// JMP C0 00 A: 3D X: C5 Y: 25 PC: C000
        status.append("S: ").append(getRegisterS()).append(" ");  // JMP C0 00 A: 3D X: C5 Y: 25 PC: C000 S: 4E

        status.append("Cycle: ").append(getCycles()).append(" "); // JMP C0 00 A: 3D X: C5 Y: 25 PC: C000 S: 4E Cycle: 0

        return status.toString();
    }

    // REQUIRES: address is in between 0x0000 and 0xFFFF, inclusive.
    // EFFECTS: returns the value of the memory at the given address.
    //          see the table below for a detailed description of what is stored at which address.
    protected Address readMemory(int pointer) {
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

        if        (pointer <= Integer.parseInt("1FFF",16)) {        // 2KB internal RAM  + its mirrors
            return ram[pointer % Integer.parseInt("0800",16)];
        } else if (pointer <= Integer.parseInt("3FFF",16)) {        // NES PPU registers + its mirrors
            pointer = (pointer - Integer.parseInt("2000", 16)) % 8 + Integer.parseInt("2000", 16);
            return bus.ppuRead(pointer); // TODO add when the ppu is implemented. remember to add mirrors.
        } else if (pointer <= Integer.parseInt("4017", 16)) {       // NES APU and I/O registers
            return bus.controllerRead(pointer);
        } else if (pointer <= Integer.parseInt("401F", 16)) {       // APU and I/O functionality that is
            // normally disabled.
            return new Address(0); // TODO add when the apu is implemented.
        } else {
            return bus.mapperReadCpu(pointer);
        }
    }

    // REQUIRES: address is in between 0x0000 and 0xFFFF, inclusive.
    // MODIFIES: ram
    // EFFECTS: check the table below for a detailed explanation of what is affected and how.
    protected void writeMemory(int pointer, int rawValue) {
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
            ram[pointer % Integer.parseInt("0800",16)].setValue(value);
        } else if (pointer <= Integer.parseInt("3FFF",16)) {        // NES PPU registers + its mirrors
            bus.ppuWrite((pointer - Integer.parseInt("2000", 16) % Integer.parseInt("0008", 16)), value);
        } else if (pointer <= Integer.parseInt("4017", 16)) {       // NES APU and I/O registers.
            bus.controllerWrite(pointer, value);
        } else if (pointer <= Integer.parseInt("401F", 16)) {       // APU and I/O functionality that is
                                                                             // normally disabled
            // TODO add when the apu is implemented.
        } else {
            bus.mapperWrite(pointer, rawValue);
        }
    }
/*
    private void writeIORegisters(int pointer, int value) {
        System.out.println("Wrote " + Integer.toBinaryString(value) + " to 0x" + Integer.toHexString(pointer));
        if (pointer == Integer.parseInt("4016", 16)) {
            controller.setPolling(value == 1);
        }
    }*/

    // REQUIRES: 0 <= value < 2^8
    // MODIFIES: registerS, stack
    // EFFECTS: value is pushed onto the stack, registerS is decremented.
    public void pushStack(int value) {
        writeMemory(CPU.OFFSET_REGISTER_S + registerS.getValue(), value);

        setRegisterS(getRegisterS().getValue() - 1);
    }

    // MODIFIES: registerS, stack
    // EFFECTS: value is pulled from the stack and returned, registerS is incremented.
    public Address pullStack() {
        setRegisterS(getRegisterS().getValue() + 1);

        return readMemory(CPU.OFFSET_REGISTER_S + registerS.getValue());
    }

    // EFFECTS: peeks into the stack.
    public Address peekStack() {
        return readMemory(CPU.OFFSET_REGISTER_S + registerS.getValue() + 1);
    }

    // REQUIRES: status can be represented as an 8bit binary integer
    // EFFECTS: use the flags to construct the status by concatenating them like this:
    //          VN11DIZC where the 4th and 5th bits (little endian) are 1.
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

    // EFFECTS: returns whether or not the address is a breakpoint
    public boolean isBreakpoint(Address breakpoint) {
        for (Address address : breakpoints) {
            if (address.getValue().equals(breakpoint.getValue())) {
                return true;
            }
        }

        return false;
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

    // EFFECTS: returns whether or not the CPU is enabled.
    public boolean isEnabled() {
        return enabled;
    }

    // EFFECTS: returns the number of cycles
    public int getCycles() {
        return cycle;
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

    // REQUIRES: flagC is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // MODIFIES: flagC
    // EFFECTS: sets flagC to the given value
    public void setFlagC(int flagC) {
        this.flagC = flagC;
    }

    // REQUIRES: flagZ is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // MODIFIES: flagZ
    // EFFECTS: sets flagZ to the given value
    public void setFlagZ(int flagZ) {
        this.flagZ = flagZ;
    }

    // REQUIRES: flagI is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // MODIFIES: flagI
    // EFFECTS: sets flagI to the given value
    public void setFlagI(int flagI) {
        this.flagI = flagI;
    }

    // REQUIRES: flagD is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // MODIFIES: flagD
    // EFFECTS: sets flagD to the given value
    public void setFlagD(int flagD) {
        this.flagD = flagD;
    }

    // REQUIRES: flagB is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // MODIFIES: flagB
    // EFFECTS: sets flagB to the given value
    public void setFlagB(int flagB) {
        this.flagB = flagB;
    }

    // REQUIRES: flagV is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // MODIFIES: flagV
    // EFFECTS: sets flagV to the given value
    public void setFlagV(int flagV) {
        this.flagV = flagV;
    }

    // REQUIRES: flagN is either 0 or 1. Note: boolean is not used because calculations are more readable when
    // the flags are either 0 or 1.
    // MODIFIES: flagN
    // EFFECTS: sets flagN to the given value
    public void setFlagN(int flagN) {
        this.flagN = flagN;
    }

    // MODIFIES: enabled
    // EFFECTS: sets enabled to the given value.
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // MODIFIES: cycles
    // EFFECTS: increments the cycles by the given amount and wraps it around MINIMUM_CYCLES and MAXIMUM_CYCLES
    public void incrementCycles(int numCycles) {
        cycle += numCycles;
        //cycle = (cycle - MINIMUM_CYCLES) % (MAXIMUM_CYCLES - MINIMUM_CYCLES + 1) + MINIMUM_CYCLES;
    }

    // REQUIRES: breakpoint is an Address bounded between 0x0000 and 0xFFFF inclusive.
    // MODIFIES: breakpoints
    // EFFECTS: adds the breakpoint.
    public void addBreakpoint(Address breakpoint) {
        breakpoints.add(breakpoint);
    }

    public void setLoggingOutput(CpuOutput cpuOutput) {
        this.loggingOutput = cpuOutput;
    }
}
