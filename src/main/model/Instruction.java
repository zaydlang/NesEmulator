package model;

public class Instruction {
    private String opcode;
    private String mode;
    private int numArguments;
    private int numCycles;

    public Instruction(String opcode, String mode, int numArguments, int numCycles) {
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

    public static final Instruction[] instructions = new Instruction[]{
            new Instruction("BRK", "IMPLICIT",            1, 7),
            new Instruction("ORA", "INDEXED_INDIRECT",    2, 6),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE",           2, 3),
            new Instruction("ORA", "ZERO_PAGE",           2, 3),
            new Instruction("ASL", "ZERO_PAGE",           2, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("PHP", "IMPLICIT",            1, 3),
            new Instruction("ORA", "IMMEDIATE",           2, 2),
            new Instruction("ASL", "ACCUMULATOR",         1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE",            3, 4),
            new Instruction("ORA", "ABSOLUTE",            3, 4),
            new Instruction("ASL", "ABSOLUTE",            3, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BPL", "RELATIVE",            2, 2),
            new Instruction("ORA", "INDIRECT_INDEXED",    2, 5),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("ORA", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("ASL", "ZERO_PAGE_INDEXED_X", 2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CLC", "IMPLICIT",            1, 2),
            new Instruction("ORA", "ABSOLUTE_INDEXED_Y",  3, 4),
            new Instruction("NOP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("ORA", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("ASL", "ABSOLUTE_INDEXED_X",  3, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("JSR", "ABSOLUTE",            3, 6),
            new Instruction("AND", "INDEXED_INDIRECT",    2, 6),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BIT", "ZERO_PAGE",           2, 3),
            new Instruction("AND", "ZERO_PAGE",           2, 3),
            new Instruction("ROL", "ZERO_PAGE",           2, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("PLP", "IMPLICIT",            1, 4),
            new Instruction("AND", "IMMEDIATE",           2, 2),
            new Instruction("ROL", "ACCUMULATOR",         1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BIT", "ABSOLUTE",            3, 4),
            new Instruction("AND", "ABSOLUTE",            3, 4),
            new Instruction("ROL", "ABSOLUTE",            3, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BMI", "RELATIVE",            2, 2),
            new Instruction("AND", "INDIRECT_INDEXED",    2, 5),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("AND", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("ROL", "ZERO_PAGE_INDEXED_X", 2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("SEC", "IMPLICIT",            1, 2),
            new Instruction("AND", "ABSOLUTE_INDEXED_Y",  3, 4),
            new Instruction("NOP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("AND", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("ROL", "ABSOLUTE_INDEXED_X",  3, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("RTI", "IMPLICIT",            1, 6),
            new Instruction("EOR", "INDEXED_INDIRECT",    2, 6),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE",           2, 3),
            new Instruction("EOR", "ZERO_PAGE",           2, 3),
            new Instruction("LSR", "ZERO_PAGE",           2, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("PHA", "IMPLICIT",            1, 3),
            new Instruction("EOR", "IMMEDIATE",           2, 2),
            new Instruction("LSR", "ACCUMULATOR",         1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("JMP", "ABSOLUTE",            3, 3),
            new Instruction("EOR", "ABSOLUTE",            3, 4),
            new Instruction("LSR", "ABSOLUTE",            3, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BVC", "RELATIVE",            2, 2),
            new Instruction("EOR", "INDIRECT_INDEXED",    2, 5),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("EOR", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("LSR", "ZERO_PAGE_INDEXED_X", 2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CLI", "IMPLICIT",            1, 2),
            new Instruction("EOR", "ABSOLUTE_INDEXED_Y",  3, 4),
            new Instruction("NOP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("EOR", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("LSR", "ABSOLUTE_INDEXED_X",  3, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("RTS", "IMPLICIT",            1, 6),
            new Instruction("ADC", "INDEXED_INDIRECT",    2, 6),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE",           2, 3),
            new Instruction("ADC", "ZERO_PAGE",           2, 3),
            new Instruction("ROR", "ZERO_PAGE",           2, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("PLA", "IMPLICIT",            1, 4),
            new Instruction("ADC", "IMMEDIATE",           2, 2),
            new Instruction("ROR", "ACCUMULATOR",         1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("JMP", "INDIRECT",            3, 5),
            new Instruction("ADC", "ABSOLUTE",            3, 4),
            new Instruction("ROR", "ABSOLUTE",            3, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BVS", "RELATIVE",            2, 2),
            new Instruction("ADC", "INDIRECT_INDEXED",    2, 5),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("ADC", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("ROR", "ZERO_PAGE_INDEXED_X", 2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("SEI", "IMPLICIT",            1, 2),
            new Instruction("ADC", "ABSOLUTE_INDEXED_Y",  3, 4),
            new Instruction("NOP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("ADC", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("ROR", "ABSOLUTE_INDEXED_X",  3, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "IMMEDIATE",           2, 2),
            new Instruction("STA", "INDEXED_INDIRECT",    2, 6),
            new Instruction("NOP", "IMMEDIATE",           1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("STY", "ZERO_PAGE",           2, 3),
            new Instruction("STA", "ZERO_PAGE",           2, 3),
            new Instruction("STX", "ZERO_PAGE",           2, 3),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("DEY", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMMEDIATE",           1, 2),
            new Instruction("TXA", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("STY", "ABSOLUTE",            3, 4),
            new Instruction("STA", "ABSOLUTE",            3, 4),
            new Instruction("STX", "ABSOLUTE",            3, 4),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BCC", "RELATIVE",            2, 2),
            new Instruction("STA", "INDIRECT_INDEXED",    2, 6),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("STY", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("STA", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("STX", "ZERO_PAGE_INDEXED_Y", 2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("TYA", "IMPLICIT",            1, 2),
            new Instruction("STA", "ABSOLUTE_INDEXED_Y",  3, 5),
            new Instruction("TXS", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("SHY", "ABSOLUTE_INDEXED_X",  1, 5),
            new Instruction("STA", "ABSOLUTE_INDEXED_X",  3, 5),
            new Instruction("SHX", "ABSOLUTE_INDEXED_Y",  1, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("LDY", "IMMEDIATE",           2, 2),
            new Instruction("LDA", "INDEXED_INDIRECT",    2, 6),
            new Instruction("LDX", "IMMEDIATE",           2, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("LDY", "ZERO_PAGE",           2, 3),
            new Instruction("LDA", "ZERO_PAGE",           2, 3),
            new Instruction("LDX", "ZERO_PAGE",           2, 3),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("TAY", "IMPLICIT",            1, 2),
            new Instruction("LDA", "IMMEDIATE",           2, 2),
            new Instruction("TAX", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("LDY", "ABSOLUTE",            3, 4),
            new Instruction("LDA", "ABSOLUTE",            3, 4),
            new Instruction("LDX", "ABSOLUTE",            3, 4),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BCS", "RELATIVE",            2, 2),
            new Instruction("LDA", "INDIRECT_INDEXED",    2, 5),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("LDY", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("LDA", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("LDX", "ZERO_PAGE_INDEXED_Y", 2, 4),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CLV", "IMPLICIT",            1, 2),
            new Instruction("LDA", "ABSOLUTE_INDEXED_Y",  3, 4),
            new Instruction("TSX", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("LDY", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("LDA", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("LDX", "ABSOLUTE_INDEXED_Y",  3, 4),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPY", "IMMEDIATE",           2, 2),
            new Instruction("CMP", "INDEXED_INDIRECT",    2, 6),
            new Instruction("NOP", "IMMEDIATE",           1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPY", "ZERO_PAGE",           2, 3),
            new Instruction("CMP", "ZERO_PAGE",           2, 3),
            new Instruction("DEC", "ZERO_PAGE",           2, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("INY", "IMPLICIT",            1, 2),
            new Instruction("CMP", "IMMEDIATE",           2, 2),
            new Instruction("DEX", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPY", "ABSOLUTE",            3, 4),
            new Instruction("CMP", "ABSOLUTE",            3, 4),
            new Instruction("DEC", "ABSOLUTE",            3, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BNE", "RELATIVE",            2, 2),
            new Instruction("CMP", "INDIRECT_INDEXED",    2, 5),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("CMP", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("DEC", "ZERO_PAGE_INDEXED_X", 2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CLD", "IMPLICIT",            1, 2),
            new Instruction("CMP", "ABSOLUTE_INDEXED_Y",  3, 4),
            new Instruction("NOP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("CMP", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("DEC", "ABSOLUTE_INDEXED_X",  3, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPX", "IMMEDIATE",           2, 2),
            new Instruction("SBC", "INDEXED_INDIRECT",    2, 6),
            new Instruction("NOP", "IMMEDIATE",           1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPX", "ZERO_PAGE",           2, 3),
            new Instruction("SBC", "ZERO_PAGE",           2, 3),
            new Instruction("INC", "ZERO_PAGE",           2, 5),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("INX", "IMPLICIT",            1, 2),
            new Instruction("SBC", "IMMEDIATE",           2, 2),
            new Instruction("NOP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("CPX", "ABSOLUTE",            3, 4),
            new Instruction("SBC", "ABSOLUTE",            3, 4),
            new Instruction("INC", "ABSOLUTE",            3, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("BEQ", "RELATIVE",            2, 2),
            new Instruction("SBC", "INDIRECT_INDEXED",    2, 5),
            new Instruction("STP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("SBC", "ZERO_PAGE_INDEXED_X", 2, 4),
            new Instruction("INC", "ZERO_PAGE_INDEXED_X", 2, 6),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("SED", "IMPLICIT",            1, 2),
            new Instruction("SBC", "ABSOLUTE_INDEXED_Y",  3, 4),
            new Instruction("NOP", "IMPLICIT",            1, 2),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
            new Instruction("NOP", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("SBC", "ABSOLUTE_INDEXED_X",  3, 4),
            new Instruction("INC", "ABSOLUTE_INDEXED_X",  3, 7),
            new Instruction("NOP", "IMPLICIT",            0, 0), // unused instruction
    };
}