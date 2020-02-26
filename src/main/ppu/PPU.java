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

    private Address latchNametable;
    private Address latchAttributeTable;
    private Address latchPatternTableLow;
    private Address latchPatternTableHigh;

    private Address ppuCtrl;
    private Address ppuMask;
    private Address ppuStatus;
    private Address oamAddr;
    private Address oamData;
    private Address ppuScroll;
    private Address ppuAddr;
    private Address ppuData;
    private Address oamDma;

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

    private Pixels pixels;

    public PPU(Mapper mapper) {
        // TODO: initialize states somehow
        // TODO: just figure out how to initialize everything honestly
        //patternTable0       = new Address[PATTERN_TABLE_SIZE];
        //patternTable1       = new Address[PATTERN_TABLE_SIZE];
        nametable           = new Address[NUM_NAMETABLES * NAMETABLE_SIZE];
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
        nmi                 = true;

        cycle               = 0;
        scanline            = 0;

        setupInternalRegisters();

        this.mapper = mapper;
        applyMapper();

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






    // Cycling
    public void cycle() {
        if        (scanline <= 239) { // Visible Scanlines
            runVisibleScanline();
        } else if (scanline <= 240) { // Post-Render Scanlines
            runPostRenderScanline();
        } else if (scanline <= 260) { // Vertical Blanking Scanline
            runVerticalBlankingScanline();
        } else {                      // Pre-Render Scanlines
            runPreRenderScanline();
        }

        cycle++;
        if (cycle == NUM_CYCLES) {
            cycle = 0;
            scanline++;
        }
        if (scanline == NUM_SCANLINES) {
            scanline = 0;
        }
    }

    private void runVisibleScanline() {
        if        (cycle <= 0) {   // Idle cycle
            return;
        } else if (cycle <= 256) { // Memory fetches for current scanline
            runVisibleScanlineRenderingCycles();
        } else if (cycle <= 320) { // Garbage memory fetches
            return;
        } else if (cycle <= 336) { // Memory fetches for next scanline
            runVisibleScanlineFutureCycles();
        } else if (cycle <= 340) { // Garbage memory fetches
            return;
        }
    }

    private void runVisibleScanlineRenderingCycles() {
        switch ((cycle - 1) % 8) {
            case 0:
                renderShiftRegisters();
            case 1:
                fetchNametableByte();
                break;
            case 3:
                fetchAttributeTableByte();
            case 5:
                fetchPatternTableLowByte();
            case 7:
                fetchPatternTableHighByte();
        }
    }

    private void fetchNametableByte() {
        int address = registerV.getValue() & Integer.parseInt("011111111111111", 2);
        latchNametable = nametable[address];
    }

    private void fetchAttributeTableByte() {
        int address = registerV.getValue()
    }

    private void renderShiftRegisters() {
    }










    // Memory Reading / Writing
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
            return nametable[pointer - Integer.parseInt("2000", 16)];
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
            nametable[pointer - Integer.parseInt("2000", 16)].setValue(value);
        } else if (pointer <= Integer.parseInt("3EFF", 16)) {
            return;
        } else {
            int mirroredAddress = (pointer - Integer.parseInt("3F00", 16)) % PALETTE_RAM_SIZE;
            System.out.print(Integer.toHexString(pointer) + " -> ");
            System.out.print(Integer.toHexString(Integer.parseInt("3F00", 16) + mirroredAddress) + " : ");
            System.out.println(value);
            paletteRamIndexes.writeMemory(mirroredAddress, value);
        }
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
    }











    // Getters and Setters
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
        cycle++;
        if (cycle == NUM_CYCLES) {
            cycle = 0;
            scanline++;
        }
        if (scanline == NUM_SCANLINES) {
            scanline = 0;
        }

        if (cycle == 1) {
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
                        Color color = ColorPalette.getColor(paletteRamIndexes.readMemory(palette).getValue());
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
        return cycle;
    }
}
