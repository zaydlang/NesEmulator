package model;

public class Instruction {
    private String opcode;
    private String mode;
    private int numArguments;
    private int numCycles;

    private Instruction(String opcode, String mode, int numArguments, int numCycles) {
        this.opcode       = opcode;
        this.mode         = mode;
        this.numArguments = numArguments;
        this.numCycles    = numCycles;
    }

    // EFFECTS: returns the opcode
    public String getOpcode() {
        return opcode;
    }

    // EFFECTS: returns the mode
    public String getMode() {
        return mode;
    }

    // EFFECTS: returns the number of bytes
    public int getNumArguments() {
        return numArguments;
    }

    // EFFECTS: returns the number of cycles
    public int getNumCycles() {
        return numCycles;
    }

    public static Instruction[] getInstructions() {
        return instructions;
    }

    private static final Instruction[] instructions = new Instruction[]{
            new Instruction("BRK", "IMPLICIT",            0, 7),
            new Instruction("ORA", "INDEXED_INDIRECT",    1, 6),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            1, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE",           1, 3),
            new Instruction("ORA", "ZERO_PAGE",           1, 3),
            new Instruction("ASL", "ZERO_PAGE",           1, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("PHP", "IMPLICIT",            0, 3),
            new Instruction("ORA", "IMMEDIATE",           1, 2),
            new Instruction("ASL", "ACCUMULATOR",         0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE",            2, 4),
            new Instruction("ORA", "ABSOLUTE",            2, 4),
            new Instruction("ASL", "ABSOLUTE",            2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BPL", "RELATIVE",            1, 2),
            new Instruction("ORA", "INDIRECT_INDEXED",    1, 5),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("ORA", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("ASL", "ZERO_PAGE_INDEXED_X", 1, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CLC", "IMPLICIT",            0, 2),
            new Instruction("ORA", "ABSOLUTE_INDEXED_Y",  2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("ORA", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("ASL", "ABSOLUTE_INDEXED_X",  2, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("JSR", "ABSOLUTE",            2, 6),
            new Instruction("AND", "INDEXED_INDIRECT",    1, 6),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BIT", "ZERO_PAGE",           1, 3),
            new Instruction("AND", "ZERO_PAGE",           1, 3),
            new Instruction("ROL", "ZERO_PAGE",           1, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("PLP", "IMPLICIT",            0, 4),
            new Instruction("AND", "IMMEDIATE",           1, 2),
            new Instruction("ROL", "ACCUMULATOR",         0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BIT", "ABSOLUTE",            2, 4),
            new Instruction("AND", "ABSOLUTE",            2, 4),
            new Instruction("ROL", "ABSOLUTE",            2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BMI", "RELATIVE",            1, 2),
            new Instruction("AND", "INDIRECT_INDEXED",    1, 5),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("AND", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("ROL", "ZERO_PAGE_INDEXED_X", 1, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("SEC", "IMPLICIT",            0, 2),
            new Instruction("AND", "ABSOLUTE_INDEXED_Y",  2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("AND", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("ROL", "ABSOLUTE_INDEXED_X",  2, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("RTI", "IMPLICIT",            0, 6),
            new Instruction("EOR", "INDEXED_INDIRECT",    1, 6),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE",           1, 3),
            new Instruction("EOR", "ZERO_PAGE",           1, 3),
            new Instruction("LSR", "ZERO_PAGE",           1, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("PHA", "IMPLICIT",            0, 3),
            new Instruction("EOR", "IMMEDIATE",           1, 2),
            new Instruction("LSR", "ACCUMULATOR",         0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("JMP", "ABSOLUTE",            2, 3),
            new Instruction("EOR", "ABSOLUTE",            2, 4),
            new Instruction("LSR", "ABSOLUTE",            2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BVC", "RELATIVE",            1, 2),
            new Instruction("EOR", "INDIRECT_INDEXED",    1, 5),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("EOR", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("LSR", "ZERO_PAGE_INDEXED_X", 1, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CLI", "IMPLICIT",            0, 2),
            new Instruction("EOR", "ABSOLUTE_INDEXED_Y",  2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("EOR", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("LSR", "ABSOLUTE_INDEXED_X",  2, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("RTS", "IMPLICIT",            0, 6),
            new Instruction("ADC", "INDEXED_INDIRECT",    1, 6),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE",           1, 3),
            new Instruction("ADC", "ZERO_PAGE",           1, 3),
            new Instruction("ROR", "ZERO_PAGE",           1, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("PLA", "IMPLICIT",            0, 4),
            new Instruction("ADC", "IMMEDIATE",           1, 2),
            new Instruction("ROR", "ACCUMULATOR",         0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("JMP", "INDIRECT",            2, 5),
            new Instruction("ADC", "ABSOLUTE",            2, 4),
            new Instruction("ROR", "ABSOLUTE",            2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BVS", "RELATIVE",            1, 2),
            new Instruction("ADC", "INDIRECT_INDEXED",    1, 5),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("ADC", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("ROR", "ZERO_PAGE_INDEXED_X", 1, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("SEI", "IMPLICIT",            0, 2),
            new Instruction("ADC", "ABSOLUTE_INDEXED_Y",  2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("ADC", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("ROR", "ABSOLUTE_INDEXED_X",  2, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "IMMEDIATE",           1, 2),
            new Instruction("STA", "INDEXED_INDIRECT",    1, 6),
            new Instruction("NOP", "IMMEDIATE",           0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("STY", "ZERO_PAGE",           1, 3),
            new Instruction("STA", "ZERO_PAGE",           1, 3),
            new Instruction("STX", "ZERO_PAGE",           1, 3),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("DEY", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMMEDIATE",           0, 2),
            new Instruction("TXA", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("STY", "ABSOLUTE",            2, 4),
            new Instruction("STA", "ABSOLUTE",            2, 4),
            new Instruction("STX", "ABSOLUTE",            2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BCC", "RELATIVE",            1, 2),
            new Instruction("STA", "INDIRECT_INDEXED",    1, 6),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("STY", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("STA", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("STX", "ZERO_PAGE_INDEXED_Y", 1, 4),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("TYA", "IMPLICIT",            0, 2),
            new Instruction("STA", "ABSOLUTE_INDEXED_Y",  2, 5),
            new Instruction("TXS", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("SHX", "ABSOLUTE_INDEXED_X",  2, 5), // TODO: CHANGE TO 0 ARG WHEN IMPLEMENTED
            new Instruction("SHY", "ABSOLUTE_INDEXED_X",  2, 5), // TODO: CHANGE TO 0 ARG WHEN IMPLEMENTED
            new Instruction("SHX", "ABSOLUTE_INDEXED_Y",  2, 5), // TODO: CHANGE TO 0 ARG WHEN IMPLEMENTED
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("LDY", "IMMEDIATE",           1, 2),
            new Instruction("LDA", "INDEXED_INDIRECT",    1, 6),
            new Instruction("LDX", "IMMEDIATE",           1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("LDY", "ZERO_PAGE",           1, 3),
            new Instruction("LDA", "ZERO_PAGE",           1, 3),
            new Instruction("LDX", "ZERO_PAGE",           1, 3),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("TAY", "IMPLICIT",            0, 2),
            new Instruction("LDA", "IMMEDIATE",           1, 2),
            new Instruction("TAX", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("LDY", "ABSOLUTE",            2, 4),
            new Instruction("LDA", "ABSOLUTE",            2, 4),
            new Instruction("LDX", "ABSOLUTE",            2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BCS", "RELATIVE",            1, 2),
            new Instruction("LDA", "INDIRECT_INDEXED",    1, 5),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("LDY", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("LDA", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("LDX", "ZERO_PAGE_INDEXED_Y", 1, 4),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CLV", "IMPLICIT",            0, 2),
            new Instruction("LDA", "ABSOLUTE_INDEXED_Y",  2, 4),
            new Instruction("TSX", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("LDY", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("LDA", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("LDX", "ABSOLUTE_INDEXED_Y",  2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPY", "IMMEDIATE",           1, 2),
            new Instruction("CMP", "INDEXED_INDIRECT",    1, 6),
            new Instruction("NOP", "IMMEDIATE",           0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPY", "ZERO_PAGE",           1, 3),
            new Instruction("CMP", "ZERO_PAGE",           1, 3),
            new Instruction("DEC", "ZERO_PAGE",           1, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("INY", "IMPLICIT",            0, 2),
            new Instruction("CMP", "IMMEDIATE",           1, 2),
            new Instruction("DEX", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPY", "ABSOLUTE",            2, 4),
            new Instruction("CMP", "ABSOLUTE",            2, 4),
            new Instruction("DEC", "ABSOLUTE",            2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BNE", "RELATIVE",            1, 2),
            new Instruction("CMP", "INDIRECT_INDEXED",    1, 5),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("CMP", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("DEC", "ZERO_PAGE_INDEXED_X", 1, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CLD", "IMPLICIT",            0, 2),
            new Instruction("CMP", "ABSOLUTE_INDEXED_Y",  2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("CMP", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("DEC", "ABSOLUTE_INDEXED_X",  2, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPX", "IMMEDIATE",           1, 2),
            new Instruction("SBC", "INDEXED_INDIRECT",    1, 6),
            new Instruction("NOP", "IMMEDIATE",           0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPX", "ZERO_PAGE",           1, 3),
            new Instruction("SBC", "ZERO_PAGE",           1, 3),
            new Instruction("INC", "ZERO_PAGE",           1, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("INX", "IMPLICIT",            0, 2),
            new Instruction("SBC", "IMMEDIATE",           1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPX", "ABSOLUTE",            2, 4),
            new Instruction("SBC", "ABSOLUTE",            2, 4),
            new Instruction("INC", "ABSOLUTE",            2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BEQ", "RELATIVE",            1, 2),
            new Instruction("SBC", "INDIRECT_INDEXED",    1, 5),
            new Instruction("STP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("SBC", "ZERO_PAGE_INDEXED_X", 1, 4),
            new Instruction("INC", "ZERO_PAGE_INDEXED_X", 1, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("SED", "IMPLICIT",            0, 2),
            new Instruction("SBC", "ABSOLUTE_INDEXED_Y",  2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("SBC", "ABSOLUTE_INDEXED_X",  2, 4),
            new Instruction("INC", "ABSOLUTE_INDEXED_X",  2, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
    };
}