package ppu;

import model.Address;

import java.util.ArrayList;

public class PPU {
    // Constants
    private static final int PATTERN_TABLE_SIZE    = Integer.parseInt("1000", 16);
    private static final int NAME_TABLE_SIZE       = Integer.parseInt("0400", 16);
    private static final int PALETTE_RAM_SIZE      = Integer.parseInt("0020", 16);
    private static final int OAM_SIZE              = Integer.parseInt("0100", 16);

    private static final int PPUCTRL_ADDRESS       = Integer.parseInt("2000", 16);
    private static final int PPUMASK_ADDRESS       = Integer.parseInt("2001", 16);
    private static final int PPUSTATUS_ADDRESS     = Integer.parseInt("2002", 16);
    private static final int OAMADDR_ADDRESS       = Integer.parseInt("2003", 16);
    private static final int OAMDATA_ADDRESS       = Integer.parseInt("2004", 16);
    private static final int PPUSCROLL_ADDRESS     = Integer.parseInt("2005", 16);
    private static final int PPUADDR_ADDRESS       = Integer.parseInt("2006", 16);
    private static final int PPUDATA_ADDRESS       = Integer.parseInt("2007", 16);
    private static final int OAMDMA_ADDRESS        = Integer.parseInt("4014", 16);

    // Memory
    private Address[] patternTable0;
    private Address[] patternTable1;
    private Address[] nameTable0;
    private Address[] nameTable1;
    private Address[] nameTable2;
    private Address[] nameTable3;
    private Address[] paletteRamIndexes;
    private Address[] OAM;

    // State Machine
    private ArrayList<PpuAction> states;
    private int cycle;

    // Flags
    private boolean vblank;

    public PPU() {
        // TODO: initialize states somehow
        patternTable0     = new Address[PATTERN_TABLE_SIZE];
        patternTable1     = new Address[PATTERN_TABLE_SIZE];
        nameTable0        = new Address[NAME_TABLE_SIZE];
        nameTable1        = new Address[NAME_TABLE_SIZE];
        nameTable2        = new Address[NAME_TABLE_SIZE];
        nameTable3        = new Address[NAME_TABLE_SIZE];
        paletteRamIndexes = new Address[PALETTE_RAM_SIZE];
        OAM               = new Address[OAM_SIZE];

        vblank = false;

        cycle = 0;
    }

    public void cycle() {
        states.get(cycle).run();
        cycle++;
    }

    public Address readMemory(int pointer) {
        // https://wiki.nesdev.com/w/index.php/PPU_memory_map
        // ADDRESS RANGE | SIZE  | DEVICE
        // $0000 - $0FFF | $1000 | Pattern Table 0
        // $1000 - $1FFF | $0800 | Pattern Table 1
        // $2000 - $23FF | $0800 | Nametable 0
        // $2400 - $27FF | $0800 | Nametable 1
        // $2800 - $2BFF | $0008 | Nametable 2
        // $2C00 - $2FFF | $1FF8 | Nametable 3
        // $3000 - $3EFF | $0018 | Mirrors of $2000-$2EFF
        // $3F00 - $3F1F | $0008 | Palette RAM indexes
        // $3F20 - $3FFF | $BFE0 | Mirrors of $3F00-$3F1F

        if        (pointer <= Integer.parseInt("0FFF", 16)) {
            return patternTable0[pointer];
        } else if (pointer <= Integer.parseInt("1FFF", 16)) {
            return patternTable1[pointer - Integer.parseInt("1000", 16)];
        } else if (pointer <= Integer.parseInt("23FF", 16)) {
            return nameTable0[pointer - Integer.parseInt("2000", 16)];
        } else if (pointer <= Integer.parseInt("27FF", 16)) {
            return nameTable1[pointer - Integer.parseInt("2400", 16)];
        } else if (pointer <= Integer.parseInt("2BFF", 16)) {
            return nameTable2[pointer - Integer.parseInt("2800", 16)];
        } else if (pointer <= Integer.parseInt("2FFF", 16)) {
            return nameTable3[pointer - Integer.parseInt("2C00", 16)];
        } else if (pointer <= Integer.parseInt("3EFF", 16)) {
            return readMemory(pointer - Integer.parseInt("2000", 16));
        } else {
            return paletteRamIndexes[(pointer - Integer.parseInt("3F00")) % PALETTE_RAM_SIZE];
        }
    }

    public Address getPpuCtrl() {
        return readMemory(PPUCTRL_ADDRESS);
    }

    public Address getPpuMask() {
        return readMemory(PPUMASK_ADDRESS);
    }

    public Address getPpuStatus() {
        return readMemory(PPUSTATUS_ADDRESS);
    }

    public Address getOamAddr() {
        return readMemory(PPUADDR_ADDRESS);
    }

    public Address getOamData() {
        return readMemory(OAMADDR_ADDRESS);
    }

    public Address getPpuScroll() {
        return readMemory(PPUSCROLL_ADDRESS);
    }

    public Address getPpuAddr() {
        return readMemory(PPUADDR_ADDRESS);
    }

    public Address getPpuData() {
        return readMemory(PPUDATA_ADDRESS);
    }

    public Address getOamDma() {
        return readMemory(OAMDMA_ADDRESS);
    }
}
