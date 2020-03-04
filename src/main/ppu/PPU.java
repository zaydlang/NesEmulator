package ppu;

import mapper.Mapper;
import model.Address;
import model.Util;
import ui.Pixels;

import java.awt.*;
import java.util.ArrayList;

// Lots of information about how the PPU works comes from this video:
// https://www.youtube.com/watch?v=-THeUXqR3zY

public class PPU {
    // Constants
    private static final int PATTERN_TABLE_SIZE        = Integer.parseInt("1000",          16);
    private static final int NAMETABLE_SIZE            = Integer.parseInt("0400",          16);
    private static final int PALETTE_RAM_SIZE          = Integer.parseInt("0020",          16);
    private static final int OAM_SIZE                  = Integer.parseInt("0100",          16);

    private static final int PPUCTRL_ADDRESS           = Integer.parseInt("2000",          16);
    private static final int PPUMASK_ADDRESS           = Integer.parseInt("2001",          16);
    private static final int PPUSTATUS_ADDRESS         = Integer.parseInt("2002",          16);
    private static final int OAMADDR_ADDRESS           = Integer.parseInt("2003",          16);
    private static final int OAMDATA_ADDRESS           = Integer.parseInt("2004",          16);
    private static final int PPUSCROLL_ADDRESS         = Integer.parseInt("2005",          16);
    private static final int PPUADDR_ADDRESS           = Integer.parseInt("2006",          16);
    private static final int PPUDATA_ADDRESS           = Integer.parseInt("2007",          16);
    private static final int OAMDMA_ADDRESS            = Integer.parseInt("4014",          16);

    private static final int REGISTER_V_SIZE           = 15; // bits
    private static final int REGISTER_T_SIZE           = 15; // bits
    private static final int REGISTER_X_SIZE           = 3;  // bits
    private static final int REGISTER_W_SIZE           = 1;  // bits

    private static final int SHIFT_REGISTER_SMALL_SIZE = 8;
    private static final int SHIFT_REGISTER_LARGE_SIZE = 16;

    private static final int INITIAL_REGISTER_V        = Integer.parseInt("00000000000000", 2);
    private static final int INITIAL_REGISTER_T        = Integer.parseInt("00000000000000", 2);
    private static final int INITIAL_REGISTER_X        = Integer.parseInt("000",            2);
    private static final int INITIAL_REGISTER_W        = Integer.parseInt("0",              2);

    private static final int NUM_TILES                 = 64;
    private static final int NUM_NAMETABLES            = 4;
    private static final int NUM_PATTERNTABLES         = 2;
    private static final int NUM_LATCHES               = 4;
    private static final int NUM_CYCLES                = 341;
    private static final int NUM_SCANLINES             = 261;

    private Address latchNametable;
    private Address latchAttributeTable;
    private Address latchPatternTableLow;
    private Address latchPatternTableHigh;

    // Registers and Latches
    private Address registerV;
    private Address registerT;
    private Address registerX;
    private Address registerW;

    private ShiftRegister shiftRegisterSmall0;
    private ShiftRegister shiftRegisterSmall1;
    private ShiftRegister shiftRegisterLarge0;
    private ShiftRegister shiftRegisterLarge1;

    private Address ppuCtrl;
    private Address ppuMask;
    private Address ppuStatus;
    private Address oamAddr;
    private Address oamData;
    private Address ppuScroll;
    private Address ppuAddr;
    private Address ppuData;
    private Address oamDma;

    private Address[] latches;

    private boolean nmi;
    private Address ppuDataBuffer;

    // Memory
    private PatternTable[] patternTables;
    private Address[] nametable;
    private PaletteRamIndexes paletteRamIndexes;
    private Address[] oam;
    private Mapper mapper;

    // Cycling
    private int cycle;
    private int scanline;
    private int drawX;
    private int drawY;

    private Pixels pixels;

    public PPU(Mapper mapper) {
        // TODO: initialize states somehow
        // TODO: just figure out how to initialize everything honestly
        nametable             = new Address[NUM_NAMETABLES * NAMETABLE_SIZE];
        patternTables         = new PatternTable[NUM_PATTERNTABLES];
        paletteRamIndexes     = new PaletteRamIndexes();
        oam                   = new Address[OAM_SIZE];

        registerV             = new Address(INITIAL_REGISTER_V, 0, (int) Math.pow(2, REGISTER_V_SIZE) - 1);
        registerT             = new Address(INITIAL_REGISTER_T, 0, (int) Math.pow(2, REGISTER_T_SIZE) - 1);
        registerX             = new Address(INITIAL_REGISTER_X, 0, (int) Math.pow(2, REGISTER_X_SIZE) - 1);
        registerW             = new Address(INITIAL_REGISTER_W, 0, (int) Math.pow(2, REGISTER_W_SIZE) - 1);

        shiftRegisterSmall0   = new ShiftRegister(SHIFT_REGISTER_SMALL_SIZE);
        shiftRegisterSmall1   = new ShiftRegister(SHIFT_REGISTER_SMALL_SIZE);
        shiftRegisterLarge0   = new ShiftRegister(SHIFT_REGISTER_LARGE_SIZE);
        shiftRegisterLarge1   = new ShiftRegister(SHIFT_REGISTER_LARGE_SIZE);
        latches               = new Address[NUM_LATCHES];
        nmi                   = false;

        latchNametable        = new Address(0);
        latchAttributeTable   = new Address(0);
        latchPatternTableLow  = new Address(0);
        latchPatternTableHigh = new Address(0);

        cycle                 = 0;
        scanline              = 0;

        setupInternalRegisters();

        for (int i = 0; i < nametable.length; i++) {
            nametable[i] = new Address(0, Integer.parseInt("2000", 16) + i);
        }

        for (int i = 0; i < latches.length; i++) {
            latches[i] = new Address(0, 0, 65536);
        }

        ppuDataBuffer = new Address(0);

        this.mapper = mapper;
        applyMapper();
    }

    private void setupInternalRegisters() {
        ppuCtrl   = new Address(0, PPUCTRL_ADDRESS);
        ppuMask   = new Address(0, PPUMASK_ADDRESS);
        ppuStatus = new Address(0, PPUSTATUS_ADDRESS);
        oamAddr   = new Address(0, OAMADDR_ADDRESS);
        oamData   = new Address(0, OAMDATA_ADDRESS);
        ppuScroll = new Address(0, PPUSCROLL_ADDRESS);
        ppuAddr   = new Address(0, PPUADDR_ADDRESS, 0, (int) Math.pow(2, 14) - 1);
        ppuData   = new Address(0, PPUDATA_ADDRESS);
    }

    private void applyMapper() {
        for (int i = 0; i < patternTables.length; i++) {
            patternTables[i] = new PatternTable();
            for (int j = 0; j < Integer.parseInt("1000", 16); j++) {
                int address = Integer.parseInt("1000", 16) * i + j;
                int value = mapper.readChrRom(address).getValue();
                patternTables[i].writeMemory(j, value);
            }
        }

        mapper.mirrorNametables(nametable);
    }





    // Cycling
    public void cycle() {
        if        (scanline <= -1) { // Pre-Render Scanlines
            runPreRenderScanline();
        } else if (scanline <= 239) { // Visible Scanlines
            runVisibleScanline();
        } else if (scanline <= 240) { // Post-Render Scanlines
            runPostRenderScanline();
        } else if (scanline <= 260) { // Vertical Blanking Scanline
            runVerticalBlankingScanline();
        }

        cycle++;
        if (cycle == 341) {
            cycle = 0;
            scanline++;
        }
        if (scanline == 261) {
            scanline = -1;
        }
    }

    private void runVisibleScanline() {
        if        (cycle <= 0) {   // Idle Cycle
            drawX = 0;

            if (scanline != 0) {
                incrementFineY();
                drawY++;
            }
        } else if (cycle <= 256) { // Memory fetches for current scanline
            runVisibleScanlineRenderingCycles();
        } else if (cycle <= 320) { // Mostly Garbage memory fetches
            runHorizontalBlankCycles();
        } else if (cycle <= 336) { // Memory fetches for next scanline
            runVisibleScanlineFutureCycles();
        } else if (cycle <= 340) { // Garbage memory fetches
            return;
        }
    }

    private void runVisibleScanlineRenderingCycles() {
        switch ((cycle - 1) % 8) {
            case 0:
                break;
            case 1:
                fetchNametableByte();
                break;
            case 3:
                fetchAttributeTableByte();
                break;
            case 5:
                fetchPatternTableLowByte();
                break;
            case 7:
                fetchPatternTableHighByte();
                loadShiftRegisters();
                break;
        }

        renderShiftRegisters();
        incrementFineX();
    }

    private void fetchNametableByte() {
        int address = (registerV.getValue() & Integer.parseInt("000111111111111", 2)) >> 0;
        latchNametable = nametable[address];
    }

    private void fetchAttributeTableByte() {
        int coarseX          = (registerV.getValue() & Integer.parseInt("000000000011111", 2)) >> 0;
        int coarseY          = (registerV.getValue() & Integer.parseInt("000001111100000", 2)) >> 5;
        int nametableAddress = (registerV.getValue() & Integer.parseInt("000110000000000", 2)) >> 10;

        int offset = nametableAddress * NAMETABLE_SIZE + Integer.parseInt("03C0", 16);
        latchAttributeTable = nametable[offset + (coarseX >> 2) + 8 * (coarseY >> 2)];
    }

    private void fetchPatternTableLowByte() {
        int address = latchNametable.getValue();
        int fineY   = (registerV.getValue() & Integer.parseInt("111000000000000", 2)) >> 12;

        int patternTableSelect = ppuCtrl.getValue() & Integer.parseInt("00001000", 2) >> 3;
        int patternTableLow = Util.reverse(patternTables[patternTableSelect].getTileLow(address)[fineY].getValue(), 8);
        latchPatternTableLow.setValue(patternTableLow);
    }

    private void fetchPatternTableHighByte() {
        int address = latchNametable.getValue();
        int fineY   = (registerV.getValue() & Integer.parseInt("111000000000000", 2)) >> 12;

        int patternTableSelect = ppuCtrl.getValue() & Integer.parseInt("00001000", 2) >> 3;
        int patternTableHigh = Util.reverse(patternTables[patternTableSelect].getTileHigh(address)[fineY].getValue(), 8);

        latchPatternTableHigh.setValue(patternTableHigh);
    }

    private void loadShiftRegisters() {
        int coarseX0 = (registerV.getValue() & Integer.parseInt("000000000000001", 2)) >> 0;
        int coarseY0 = (registerV.getValue() & Integer.parseInt("000000000100000", 2)) >> 5;
        int attributeTableLow  = Util.getNthBit(latchAttributeTable.getValue(), ((coarseY0 * 2 + coarseX0) << 1) + 0);
        int attributeTableHigh = Util.getNthBit(latchAttributeTable.getValue(), ((coarseY0 * 2 + coarseX0) << 1) + 1);

        shiftRegisterSmall0.setNthBits(0, 8,  attributeTableLow);
        shiftRegisterSmall1.setNthBits(0, 8,  attributeTableHigh);
        shiftRegisterLarge0.setNthBits(8, 16, latchPatternTableLow.getValue());
        shiftRegisterLarge1.setNthBits(8, 16, latchPatternTableHigh.getValue());
    }

    private void incrementFineX() {
        //registerX.setValue(registerX.getValue() + 1); // Increment fineX
        //int fineX = registerX.getValue();

        if (drawX % 8 == 0) {
            incrementCoarseX();
        }
    }

    private void incrementFineY() {
        registerV.setValue(registerV.getValue() + Integer.parseInt("001000000000000", 2));  // Increment fineY
        int fineY = (registerV.getValue() & Integer.parseInt("111000000000000", 2)) >> 12;

        if (fineY == 0) {
            incrementCoarseY();
        }
    }

    private void incrementCoarseX() {
        int newCoarseX = ((registerV.getValue() & Integer.parseInt("000000000011111", 2)) >> 0) + 1;
        if (newCoarseX >= Math.pow(2, 5)) {
            newCoarseX = 0;
        }

        registerV.setValue(Util.maskNthBits(newCoarseX, registerV.getValue(), 0, 0, 5));
    }

    private void incrementCoarseY() {
        int newCoarseY = ((registerV.getValue() & Integer.parseInt("000001111100000", 2)) >> 5) + 1;
        if (newCoarseY >= Math.pow(2, 5)) {
            newCoarseY = 0;
        }

        registerV.setValue(Util.maskNthBits(newCoarseY, registerV.getValue(), 0, 5, 5));
    }

    private void renderShiftRegisters() {
        int fineX    = registerX.getValue();
        int bitOne   = Util.getNthBit(shiftRegisterSmall0.getValue(), fineX);
        int bitTwo   = Util.getNthBit(shiftRegisterSmall1.getValue(), fineX);
        int bitThree = Util.getNthBit(shiftRegisterLarge0.getValue(), fineX);
        int bitFour  = Util.getNthBit(shiftRegisterLarge1.getValue(), fineX);
        int fullByte = bitOne * 4 + bitTwo * 8 + bitThree * 1 + bitFour * 2;

        Color color = getColor(fullByte);
        pixels.setPixel(drawX, drawY, color);

        drawX++;
        shiftRegisterSmall0.shiftLeft(1);
        shiftRegisterSmall1.shiftLeft(1);
        shiftRegisterLarge0.shiftLeft(1);
        shiftRegisterLarge1.shiftLeft(1);
    }

    private Color getColor(int address) {
        if (address % 4 == 3) {
            return ColorPalette.getColor(paletteRamIndexes.readMemory(3).getValue());
        } else {
            return ColorPalette.getColor(paletteRamIndexes.readMemory(address).getValue());
        }
    }

    private void runHorizontalBlankCycles() {
        if (cycle == 257) {                 // Restore the CoarseX
            int newCoarseX = (registerT.getValue() & Integer.parseInt("000000000011111", 2)) >> 0;
            registerV.setValue(Util.maskNthBits(newCoarseX, registerV.getValue(), 0, 0, 5));
        }
    }

    private void runVisibleScanlineFutureCycles() {
        switch ((cycle - 1) % 8) {
            case 0:
                loadShiftRegisters();

                //incrementCoarseX();
               // if (cycle == 256) {
                //    incrementCoarseY();
                //}

                break;
            case 1:
                fetchNametableByte();
                break;
            case 3:
                fetchAttributeTableByte();
                break;
            case 5:
                fetchPatternTableLowByte();
                break;
            case 7:
                fetchPatternTableHighByte();
                break;
        }
    }

    private void runPostRenderScanline() {

    }

    private void runVerticalBlankingScanline() {
        if (cycle == 1 && Util.getNthBit(ppuCtrl.getValue(), 7) == 1) {
            nmi = true;
        }
    }


    private void runPreRenderScanline() {
        drawY = 0;

        if (280 <= cycle && cycle <= 304) { // Restore the CoarseY
            int newCoarseY = (registerT.getValue() & Integer.parseInt("000001111100000", 2)) >> 5;
            registerV.setValue(Util.maskNthBits(newCoarseY, registerV.getValue(), 5, 0, 5));
        }
    }







    public void writeRegister(int pointer, int value) {
        if        (pointer == PPUCTRL_ADDRESS) {
            setPpuCtrl(value);
        } else if (pointer == PPUMASK_ADDRESS) {
            setPpuMask(value);
        } else if (pointer == PPUSTATUS_ADDRESS) {
            setPpuStatus(value);
        } else if (pointer == OAMADDR_ADDRESS) {
            setOamAddr(value);
        } else if (pointer == OAMDATA_ADDRESS) {
            setOamData(value);
        } else if (pointer == PPUSCROLL_ADDRESS) {
            setPpuScroll(value);
        } else if (pointer == PPUADDR_ADDRESS) {
            setPpuAddr(value);
        } else if (pointer == PPUDATA_ADDRESS) {
            setPpuData(value);
        }
    }

    private void setPpuCtrl(int value) {
        ppuCtrl.setValue(value);
        registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 0, 10, 2));
    }

    private void setPpuMask(int value) {
        ppuMask.setValue(value);
    }

    private void setPpuStatus(int value) {
        ppuStatus.setValue(value);
    }

    private void setOamAddr(int value) {
        oamAddr.setValue(value);
    }

    private void setOamData(int value) {
        oamData.setValue(value);
    }

    private void setPpuScroll(int value) {
        if (registerW.getValue() == 0) { // First Write
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 3, 0, 5));
            registerX.setValue(Util.maskNthBits(value, registerX.getValue(), 0, 0, 3));
        } else {                         // Second Write
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 0, 12, 3));
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 3, 8,  2));
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 6, 5,  3));
        }

        registerW.setValue(registerW.getValue() ^ 1);
    }

    private void setPpuAddr(int value) {
        if (registerW.getValue() == 0) { // First Write
            ppuAddr.setValue(Util.maskNthBits(value, ppuAddr.getValue(), 0, 8, 6));
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 0, 8,  6));
            registerT.setValue(Util.maskNthBits(0, registerT.getValue(), 0, 14, 1));
        } else {                         // Second Write
            ppuAddr.setValue(Util.maskNthBits(value, ppuAddr.getValue(), 0, 0, 8));
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 0, 0,  8));
            registerV.setValue(registerT.getValue());
        }

        registerW.setValue(registerW.getValue() ^ 1);
    }

    private void setPpuData(int value) {
        writeMemory(ppuAddr.getValue(), value);

        if (Util.getNthBit(ppuCtrl.getValue(), 2) == 1) {
            ppuAddr.setValue(ppuAddr.getValue() + 32);
        } else {
            ppuAddr.setValue(ppuAddr.getValue() + 1);
        }
    }

    public Address readRegister(int pointer) {
        if        (pointer == PPUCTRL_ADDRESS) {
            return getPpuCtrl();
        } else if (pointer == PPUMASK_ADDRESS) {
            return getPpuMask();
        } else if (pointer == PPUSTATUS_ADDRESS) {
            return getPpuStatus();
        } else if (pointer == OAMADDR_ADDRESS) {
            return getOamAddr();
        } else if (pointer == OAMDATA_ADDRESS) {
            return getOamData();
        } else if (pointer == PPUSCROLL_ADDRESS) {
            return getPpuScroll();
        } else if (pointer == PPUADDR_ADDRESS) {
            return getPpuAddr();
        } else if (pointer == PPUDATA_ADDRESS) {
            return getPpuData();
        }

        return null;
    }

    private Address getPpuCtrl() {
        return ppuCtrl;
    }

    private Address getPpuMask() {
        return ppuMask;
    }

    private Address getPpuStatus() {
        ppuStatus.setValue(ppuStatus.getValue() | Integer.parseInt("10000000", 2));
        registerW.setValue(0);

        int value1 = ppuStatus.getValue() & Integer.parseInt("11100000", 2);
        int value2 = ppuDataBuffer.getValue() & Integer.parseInt("00011111", 2);
        return new Address(value1 | value2);
    }

    private Address getOamAddr() {
        return oamAddr;
    }

    private Address getOamData() {
        return oamData;
    }

    private Address getPpuScroll() {
        return ppuScroll;
    }

    private Address getPpuAddr() {
        return new Address(0); // Cannot be read from!
    }

    private Address getPpuData() {
        ppuData.setValue(ppuDataBuffer.getValue());
        ppuDataBuffer.setValue(readMemory(ppuAddr.getValue()).getValue());
        if (ppuAddr.getValue() >= Integer.parseInt("3F00", 16)) {
            ppuData.setValue(readMemory(ppuAddr.getValue()).getValue());
        }

        ppuAddr.setValue(ppuAddr.getValue() + 1);
        return ppuData;
    }


    public Address peekRegister(int pointer) {
        if        (pointer == PPUCTRL_ADDRESS) {
            return ppuCtrl;
        } else if (pointer == PPUMASK_ADDRESS) {
            return ppuMask;
        } else if (pointer == PPUSTATUS_ADDRESS) {
            return ppuStatus;
        } else if (pointer == OAMADDR_ADDRESS) {
            return oamAddr;
        } else if (pointer == OAMDATA_ADDRESS) {
            return oamData;
        } else if (pointer == PPUSCROLL_ADDRESS) {
            return ppuScroll;
        } else if (pointer == PPUADDR_ADDRESS) {
            return ppuAddr;
        } else if (pointer == PPUDATA_ADDRESS) {
            return ppuData;
        }

        return null;
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
            return patternTables[0].readMemory(pointer);
        } else if (pointer <= Integer.parseInt("1FFF", 16)) {
            return patternTables[1].readMemory(pointer - Integer.parseInt("1000", 16));
        } else if (pointer <= Integer.parseInt("2FFF", 16)) {
            return nametable[pointer - Integer.parseInt("2000", 16)];
        } else if (pointer <= Integer.parseInt("3EFF", 16)) {
            return nametable[pointer - Integer.parseInt("3000", 16)];
        } else {
            return paletteRamIndexes.readMemory((pointer - Integer.parseInt("3F00", 16)) % PALETTE_RAM_SIZE);
        }
    }

    private void writeMemory(int pointer, int value) {
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
            patternTables[0].writeMemory(pointer, value);
        } else if (pointer <= Integer.parseInt("1FFF", 16)) {
            patternTables[1].writeMemory(pointer - Integer.parseInt("1000", 16), value);
        } else if (pointer <= Integer.parseInt("2FFF", 16)) {
            if (value != 0) {
                int x = 2;
            }
            nametable[pointer - Integer.parseInt("2000", 16)].setValue(value);
        } else if (pointer <= Integer.parseInt("3EFF", 16)) {
            if (value != 0) {
                int x = 2;
            }
            nametable[pointer - Integer.parseInt("3000", 16)].setValue(value);
        } else {
            int mirroredAddress = (pointer - Integer.parseInt("3F00", 16)) % PALETTE_RAM_SIZE;
            System.out.print(Integer.toHexString(pointer) + " -> ");
            System.out.print(Integer.toHexString(Integer.parseInt("3F00", 16) + mirroredAddress) + " : ");
            System.out.println(value);
            paletteRamIndexes.writeMemory(mirroredAddress, value);
            if (value != 0) {
                int u = 2;
            }
        }
    }












    // Getters and Setters
    private void setLatch(int latch, Address value) {
        latches[latch] = value;
    }

    public Address getRegisterV() {
        return registerV;
    }

    public Address getRegisterT() {
        return registerT;
    }

    public Address getRegisterX() {
        return registerX;
    }

    public Address getRegisterW() {
        return registerW;
    }

    public void setRegisterV(int registerV) {
        this.registerV.setValue(registerV);
    }

    public void setRegisterT(int registerT) {
        this.registerT.setValue(registerT);
    }

    public void setRegisterX(int registerX) {
        this.registerX.setValue(registerX);
    }

    public void setRegisterW(int registerW) {
        this.registerW.setValue(registerW);
    }

    public Pixels getPixels() {
        return pixels;
    }



    public void testcycle() {
        cycle++;
        if (cycle == 340) {
            cycle = 0;
            scanline++;
        }
        if (scanline == 260) {
            scanline = 0;
        }

        if (cycle == 1) {
            nmi = true;
        }
    }


    public void renderPatternTables(Pixels pixels, int basePalette) {
        renderPatternTable(pixels, 0, 0,   0, basePalette);
        renderPatternTable(pixels, 1, 128, 0, basePalette);
    }

    private void renderPatternTable(Pixels pixels, int patternTable, int offsetX, int offsetY, int basePalette) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Address[] low  = patternTables[patternTable].getTileLow(i + j * 16);
                Address[] high = patternTables[patternTable].getTileHigh(i + j * 16);
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        int formattedLow  = Util.getNthBit(low[l].getValue(),  7 - k);
                        int formattedHigh = Util.getNthBit(high[l].getValue(), 7 - k);
                        int palette = formattedLow + formattedHigh * 2 + basePalette * 4;
                        Color color = getColor(palette);
                        if (palette != 4) {

                            int u = 2;
                        }
                        pixels.setPixel(i * 8 + k + offsetX, j * 8 + l + offsetY, color);
                    }
                }
            }
        }
    }

    public void renderNameTables(Pixels pixels, int basePalette) {
        int patternTableSelect = ppuCtrl.getValue() & Integer.parseInt("00001000", 2) >> 3;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 32; k++) {
                    for (int l = 0; l < 30; l++) {
                        int address  = nametable[((2 * i + j) << 10) + (l << 5) + (k << 0)].getValue();
                        Address[] low  = patternTables[patternTableSelect].getTileLow(address);
                        Address[] high = patternTables[patternTableSelect].getTileHigh(address);

                        for (int m = 0; m < 8; m++) {
                            for (int n = 0; n < 8; n++) {
                                int formattedLow  = Util.getNthBit(low[n].getValue(),  7 - m);
                                int formattedHigh = Util.getNthBit(high[n].getValue(), 7 - m);
                                int palette = formattedLow + formattedHigh * 2 + basePalette * 4;
                                Color color = getColor(palette);
                                pixels.setPixel(k * 8 + m + (i * 256), l * 8 + n + (j * 256), color);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean getNmi() {
        return nmi;
    }

    public void setNmi(boolean nmi) {
        this.nmi = nmi;
    }

    public int getCycles() {
        return cycle;
    }

    public void setPixels(Pixels pixels) {
        this.pixels = pixels;
    }
}
