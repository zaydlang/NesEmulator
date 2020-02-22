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
    private Nametable[] nameTables;
    private PaletteRamIndexes paletteRamIndexes;
    private Address[] oam;
    private Mapper mapper;

    // State Machine
    private ArrayList<PpuState> states;
    private Address cycle;
    private Address scanline;

    private Pixels pixels;

    public PPU(Mapper mapper) {
        // TODO: initialize states somehow
        // TODO: just figure out how to initialize everything honestly
        //patternTable0       = new Address[PATTERN_TABLE_SIZE];
        //patternTable1       = new Address[PATTERN_TABLE_SIZE];
        nameTables          = new Nametable[NUM_NAMETABLES];
        patternTables       = new PatternTable[NUM_PATTERNTABLES];
        paletteRamIndexes   = new PaletteRamIndexes();
        oam                 = new Address[OAM_SIZE];

        registerV           = new Address(INITIAL_REGISTER_V, 0, (int) Math.pow(2, REGISTER_V_SIZE));
        registerT           = new Address(INITIAL_REGISTER_T, 0, (int) Math.pow(2, REGISTER_T_SIZE));
        registerX           = new Address(INITIAL_REGISTER_X, 0, (int) Math.pow(2, REGISTER_X_SIZE));
        registerW           = new Address(INITIAL_REGISTER_W, 0, (int) Math.pow(2, REGISTER_W_SIZE));

        shiftRegisterSmall0 = new ShiftRegister(SHIFT_REGISTER_SMALL_SIZE);
        shiftRegisterSmall1 = new ShiftRegister(SHIFT_REGISTER_SMALL_SIZE);
        shiftRegisterLarge0 = new ShiftRegister(SHIFT_REGISTER_LARGE_SIZE);
        shiftRegisterLarge1 = new ShiftRegister(SHIFT_REGISTER_LARGE_SIZE);
        latches             = new Address[NUM_LATCHES];
        nmi                 = true;

        cycle               = new Address(0, 0, NUM_CYCLES);
        scanline            = new Address(0, 0, NUM_SCANLINES);

        setupInternalRegisters();

        this.mapper = mapper;
        applyMapper();

        for (int i = 0; i < nameTables.length; i++) {
            nameTables[i] = new Nametable();
        }

        for (int i = 0; i < latches.length; i++) {
            latches[i] = new Address(0, 0, 65536);
        }

        pixels = new Pixels();

        ppuDataBuffer = new Address(0);
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
        int x = 2;
    }

    public void writeRegister(int pointer, int value) {
        int x = 2;
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
        ppuAddr.setValue(ppuAddr.getValue() + 1);
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
        return ppuAddr;
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

    private Address readMemory(int pointer) {
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
        } else if (pointer <= Integer.parseInt("23FF", 16)) {
            return nameTables[0].readMemory(pointer - Integer.parseInt("2000", 16));
        } else if (pointer <= Integer.parseInt("27FF", 16)) {
            return nameTables[1].readMemory(pointer - Integer.parseInt("2400", 16));
        } else if (pointer <= Integer.parseInt("2BFF", 16)) {
            return nameTables[2].readMemory(pointer - Integer.parseInt("2800", 16));
        } else if (pointer <= Integer.parseInt("2FFF", 16)) {
            return nameTables[3].readMemory(pointer - Integer.parseInt("2C00", 16));
        } else if (pointer <= Integer.parseInt("3EFF", 16)) {
            return readMemory(pointer - Integer.parseInt("2000", 16));
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
        } else if (pointer <= Integer.parseInt("23FF", 16)) {
            nameTables[0].writeMemory(pointer - Integer.parseInt("2000", 16), value);
        } else if (pointer <= Integer.parseInt("27FF", 16)) {
            nameTables[1].writeMemory(pointer - Integer.parseInt("2400", 16), value);
        } else if (pointer <= Integer.parseInt("2BFF", 16)) {
            nameTables[2].writeMemory(pointer - Integer.parseInt("2800", 16), value);
        } else if (pointer <= Integer.parseInt("2FFF", 16)) {
            nameTables[3].writeMemory(pointer - Integer.parseInt("2C00", 16), value);
        } else if (pointer <= Integer.parseInt("3EFF", 16)) {
            return;
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






    // State Machine
    private interface PpuState {
        void run(PPU ppu);
    }

    // EFFECTS: idles the PPU; the ppu does nothing.
    private static PpuState stateIdle = (PPU ppu) -> {
    };

    // EFFECTS: reads a nametable byte
    private static PpuState readNametableByte = (PPU ppu) -> {
        int coarseXScroll        = ppu.registerV.getValue() & Integer.parseInt("000000000011111", 2) >> 0;
        int coarseYScroll        = ppu.registerV.getValue() & Integer.parseInt("000001111100000", 2) >> 5;
        int baseNametableAddress = ppu.registerV.getValue() & Integer.parseInt("000110000000000", 2) >> 7;

        Nametable nametable  = ppu.getNametable(baseNametableAddress);
        ppu.setLatch(0, nametable.readTile(coarseXScroll, coarseYScroll));
        ppu.registerV.setValue(ppu.registerV.getValue() + 1);
    };

    private static PpuState readAttributeTableByte = (PPU ppu) -> {
        int coarseXScroll        = ppu.registerV.getValue() & Integer.parseInt("000000000011111", 2) >> 0;
        int coarseYScroll        = ppu.registerV.getValue() & Integer.parseInt("000001111100000", 2) >> 5;
        int baseNametableAddress = ppu.registerV.getValue() & Integer.parseInt("000110000000000", 2) >> 7;

        Nametable nametable = ppu.getNametable(baseNametableAddress);
        int palette = nametable.getAttributeTable().getPalette(coarseXScroll, coarseYScroll).getValue();
        ppu.setLatch(1, new Address(palette));
    };

    private static PpuState readPatternTableByteLow = (PPU ppu) -> {
        int coarseXScroll         = ppu.registerV.getValue() & Integer.parseInt("000000000011111", 2) >> 0;
        int coarseYScroll         = ppu.registerV.getValue() & Integer.parseInt("000001111100000", 2) >> 5;
        int fineYScroll           = ppu.registerV.getValue() & Integer.parseInt("111000000000000", 2) >> 12;

        PatternTable patternTable = ppu.patternTables[ppu.readRegister(Integer.parseInt("2000", 16)).getValue()];
        Address[] tile = patternTable.getTileLow(coarseXScroll, coarseYScroll);
        Address tileStrip = tile[fineYScroll];
        ppu.setLatch(2, tileStrip);
    };

    private static PpuState readPatternTableByteHigh = (PPU ppu) -> {
        int coarseXScroll         = ppu.registerV.getValue() & Integer.parseInt("000000000011111", 2) >> 0;
        int coarseYScroll         = ppu.registerV.getValue() & Integer.parseInt("000001111100000", 2) >> 5;
        int fineYScroll           = ppu.registerV.getValue() & Integer.parseInt("111000000000000", 2) >> 12;

        PatternTable patternTable = ppu.patternTables[ppu.readRegister(Integer.parseInt("2000", 16)).getValue()];
        Address[] tile = patternTable.getTileHigh(coarseXScroll, coarseYScroll);
        Address tileStrip = tile[fineYScroll];
        ppu.setLatch(3, tileStrip);
    };

    private static PpuState feedShiftRegisters = (PPU ppu) -> {
        ppu.shiftRegisterLarge0.setNthBits(8, 16, ppu.latches[0]);
        ppu.shiftRegisterLarge1.setNthBits(8, 16, ppu.latches[1]);
        ppu.shiftRegisterLarge0.shiftRight(8);
        ppu.shiftRegisterLarge1.shiftRight(8); // TODO: might be problem
        ppu.shiftRegisterSmall0.setBits(ppu.latches[2]);
        ppu.shiftRegisterSmall1.setBits(ppu.latches[3]);
    }

    private static final PpuState[] ppuActions = new PpuState[] {
            stateIdle,                      // 0

            stateIdle,                      // 1
            readNametableByte,              // 2
            stateIdle,                      // 3
            readAttributeTableByte,         // 4     Note: These entries from 9-16 loop all the way
            stateIdle,                      // 5     through entry 256.
            readPatternTableByteLow,        // 6
            stateIdle,                      // 7
            readPatternTableByteHigh,       // 8

            stateIdle,                      // 257
            stateIdle,                      // 258
            stateIdle,                      // 259
            stateIdle,                      // 260   Note: These entries from 265-272 loop all the way
            stateIdle,                      // 261   through entry 320.
            readPatternTableByteLow,        // 262
            stateIdle,                      // 263
            readPatternTableByteHigh,       // 264

            stateIdle,                      // 321
            readNametableByte,              // 322
            stateIdle,                      // 323
            readAttributeTableByte,         // 324   Note: These entries from 9-16 loop all the way
            stateIdle,                      // 325   through entry 336.
            readPatternTableByteLow,        // 326
            stateIdle,                      // 327
            readPatternTableByteHigh,       // 328

            stateIdle,                      // 337
            stateIdle,                      // 338
            stateIdle,                      // 339
            stateIdle                       // 340
    };

    public void cycle() {
        if        (scanline.getValue() <= 239) { // Visible Scanlines
            runVisibleScanline();
        } else if (scanline.getValue() <= 240) { // Post-Render Scanlines
            runPostRenderScanline();
        } else if (scanline.getValue() <= 260) { // Vertical Blanking Scanline
            runVerticalBlankingScanline();
        } else {                                 // Pre-Render Scanlines
            runPreRenderScanline();
        }

        cycle.setValue(cycle.getValue() + 1);

        if (cycle.getValue() >= NUM_CYCLES) {
            cycle.setValue(0);
            scanline.setValue(scanline.getValue() + 1);
        }

        renderShiftRegisters();
    }

    private void runVisibleScanline() {
        if        (1   <= cycle.getValue() && cycle.getValue() <= 256) {
            ppuActions[(cycle.getValue() -   1) % 8 +   1].run(this);
        } else if (257 <= cycle.getValue() && cycle.getValue() <= 320) {
            ppuActions[(cycle.getValue() - 257) % 8 + 257].run(this);
        } else if (321 <= cycle.getValue() && cycle.getValue() <= 336) {
            ppuActions[(cycle.getValue() - 321) % 8 + 321].run(this);
        } else {
            ppuActions[(cycle.getValue())].run(this);
        }
    }

    private void runPostRenderScanline() {
        // Do nothing
    }

    private void runVerticalBlankingScanline() {
        if (cycle.getValue() == 1) {
            ppuStatus.setValue(ppuStatus.getValue() | Integer.parseInt("10000000", 2));
        }
    }

    private void runPreRenderScanline() {
        feedShiftRegisters.run(this);
    }

    private void renderShiftRegisters() {
        int fineXScroll    = registerX.getValue();
        int patternLow     = (shiftRegisterLarge0.getBit(fineXScroll) ? 1 : 0);
        int patternHigh    = (shiftRegisterLarge1.getBit(fineXScroll) ? 1 : 0);
        int fullPattern    = patternLow + patternHigh * 2;
        int nametableValue = shiftRegisterSmall0.getBit(fineXScroll) ? 1 : 0;
        int attributeValue = shiftRegisterSmall1.getBit(fineXScroll) ? 1 : 0;

        shiftRegisterSmall0.shiftRight(1);
        shiftRegisterSmall1.shiftRight(1);
        shiftRegisterLarge0.shiftRight(1);
        shiftRegisterLarge1.shiftRight(1);
    }






    // Getters and Setters
    private Nametable getNametable(int nametableAddress) {
        return nameTables[nametableAddress];
    }

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



   public void renderPatternTables() {
        cycle.setValue(cycle.getValue() + 1);
        if (cycle.getValue() == 340) {
            cycle.setValue(0);
            scanline.setValue(scanline.getValue() + 1);
        }
        if (scanline.getValue() == 260) {
            scanline.setValue(0);
        }

        if (cycle.getValue() == 1) {
            nmi = true;
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Address[] low  = patternTables[0].getTileLow(i, j);
                Address[] high = patternTables[0].getTileHigh(i, j);
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        int formattedLow  = Util.getNthBit(low[k].getValue(),  l);
                        int formattedHigh = Util.getNthBit(high[k].getValue(), l);
                        int palette = formattedLow + formattedHigh * 2;
                        Color color = ColorPalette.getColor(palette);
                        pixels.setPixel(i * 8 + k, j * 8 + l, color);
                    }
                }
            }
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Address[] low  = patternTables[1].getTileLow(i, j);
                Address[] high = patternTables[1].getTileHigh(i, j);
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        int formattedLow  = Util.getNthBit(low[k].getValue(),  l);
                        int formattedHigh = Util.getNthBit(high[k].getValue(), l);
                        int palette = formattedLow + formattedHigh * 2;
                        Color color = ColorPalette.getColor(paletteRamIndexes.readMemory(palette).getValue());
                        pixels.setPixel(i * 8 + k, 128 + j * 8 + l, color);
                    }
                }
            }
        }

        for (int i = 0; i < 32; i++) {
            Color color = ColorPalette.getColor(paletteRamIndexes.readMemory(i).getValue());
            pixels.setPixel(i, 0, color);
        }
    }

    public boolean getNmi() {
        return nmi;
    }

    public void setNmi(boolean nmi) {
        this.nmi = nmi;
    }

    public int getCycles() {
        return cycle.getValue();
    }
}
