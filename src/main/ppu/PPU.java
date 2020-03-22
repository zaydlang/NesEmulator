package ppu;

import mapper.Mapper;
import model.Address;
import model.Bus;
import model.Util;
import ui.Pixels;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

// Lots of information about how the PPU works comes from this video:
// https://www.youtube.com/watch?v=-THeUXqR3zY

public class PPU {
    // Constants
    public  static final int PATTERN_TABLE_SIZE = Integer.parseInt("1000", 16);
    public  static final int NAMETABLE_SIZE     = Integer.parseInt("0400", 16);
    public  static final int PALETTE_RAM_SIZE   = Integer.parseInt("0020", 16);
    public  static final int PRIMARY_OAM_SIZE   = Integer.parseInt("0100", 16);
    public  static final int SECONDARY_OAM_SIZE = Integer.parseInt("0020", 16);
    private static final int PPUCTRL_ADDRESS = Integer.parseInt("2000", 16);
    private static final int PPUMASK_ADDRESS = Integer.parseInt("2001", 16);
    private static final int PPUSTATUS_ADDRESS = Integer.parseInt("2002", 16);
    private static final int OAMADDR_ADDRESS = Integer.parseInt("2003", 16);
    private static final int OAMDATA_ADDRESS = Integer.parseInt("2004", 16);
    private static final int PPUSCROLL_ADDRESS = Integer.parseInt("2005", 16);
    private static final int PPUADDR_ADDRESS = Integer.parseInt("2006", 16);
    private static final int PPUDATA_ADDRESS = Integer.parseInt("2007", 16);
    private static final int OAMDMA_ADDRESS = Integer.parseInt("4014", 16);

    private static final int REGISTER_V_SIZE = 15; // bits
    private static final int REGISTER_T_SIZE = 15; // bits
    private static final int REGISTER_X_SIZE = 3;  // bits
    private static final int REGISTER_W_SIZE = 1;  // bits

    private static final int SHIFT_REGISTER_SMALL_SIZE = 8;
    private static final int SHIFT_REGISTER_LARGE_SIZE = 16;

    private static final int INITIAL_REGISTER_V = Integer.parseInt("00000000000000", 2);
    private static final int INITIAL_REGISTER_T = Integer.parseInt("00000000000000", 2);
    private static final int INITIAL_REGISTER_X = Integer.parseInt("000", 2);
    private static final int INITIAL_REGISTER_W = Integer.parseInt("0", 2);

    private static final int NUM_TILES = 64;
    private static final int NUM_NAMETABLES = 4;
    private static final int NUM_PATTERNTABLES = 2;
    private static final int NUM_LATCHES = 4;
    private static final int NUM_CYCLES = 341;
    private static final int NUM_SCANLINES = 261;

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
    public Address ppuStatus;
    private Address oamAddr;
    private Address ppuScroll;
    public Address ppuData;
    private Address ppuDataBuffer;

    // Memory
    private Address[] nametable;
    private Mirroring nametableMirroring;
    protected PaletteRamIndexes paletteRamIndexes;
    private Address[] primaryOam;
    private Address[] secondaryOam;
    private Sprite[] sprites;

    // Cycling
    protected int cycle;
    protected int scanline;
    private int drawX;
    private int drawY;
    private boolean isOddFrame;

    private Pixels pixels;
    private Bus bus;

    public PPU(Bus bus) {
        nametable = new Address[NUM_NAMETABLES * NAMETABLE_SIZE];
        paletteRamIndexes = new PaletteRamIndexes();
        primaryOam = new Address[PRIMARY_OAM_SIZE];
        secondaryOam = new Address[SECONDARY_OAM_SIZE];
        sprites = new Sprite[8];

        shiftRegisterSmall0 = new ShiftRegister(SHIFT_REGISTER_SMALL_SIZE);
        shiftRegisterSmall1 = new ShiftRegister(SHIFT_REGISTER_SMALL_SIZE);
        shiftRegisterLarge0 = new ShiftRegister(SHIFT_REGISTER_LARGE_SIZE);
        shiftRegisterLarge1 = new ShiftRegister(SHIFT_REGISTER_LARGE_SIZE);

        this.bus = bus;

        reset();
    }

    public void reset() {
        registerV = new Address(INITIAL_REGISTER_V, 0, (int) Math.pow(2, REGISTER_V_SIZE) - 1);
        registerT = new Address(INITIAL_REGISTER_T, 0, (int) Math.pow(2, REGISTER_T_SIZE) - 1);
        registerX = new Address(INITIAL_REGISTER_X, 0, (int) Math.pow(2, REGISTER_X_SIZE) - 1);
        registerW = new Address(INITIAL_REGISTER_W, 0, (int) Math.pow(2, REGISTER_W_SIZE) - 1);
        ppuDataBuffer = new Address(0);

        shiftRegisterSmall0.setNthBits(0, 8, 0);
        shiftRegisterSmall1.setNthBits(0, 8, 0);
        shiftRegisterLarge0.setNthBits(0, 8, 0);
        shiftRegisterLarge1.setNthBits(0, 8, 0);

        latchNametable        = new Address(0);
        latchAttributeTable   = new Address(0);
        latchPatternTableLow  = new Address(0);
        latchPatternTableHigh = new Address(0);

        cycle      = 0;
        scanline   = 0;
        isOddFrame = false;

        resetNametables();
        resetPrimaryOam();
        resetSecondaryOam();
        resetSprites();
        setupInternalRegisters();
    }

    private void resetNametables() {
        for (int i = 0; i < nametable.length; i++) {
            nametable[i] = new Address(0, Integer.parseInt("2000", 16) + i);
        }
    }

    private void resetPrimaryOam() {
        for (int i = 0; i < primaryOam.length; i++) {
            primaryOam[i] = new Address(Integer.parseInt("FF", 16), i);
        }
    }

    private void setupInternalRegisters() {
        ppuCtrl = new Address(0, PPUCTRL_ADDRESS);
        ppuMask = new Address(0, PPUMASK_ADDRESS);
        ppuStatus = new Address(0, PPUSTATUS_ADDRESS);
        oamAddr = new Address(0, OAMADDR_ADDRESS);
        ppuScroll = new Address(0, PPUSCROLL_ADDRESS);
        ppuData = new Address(0, PPUDATA_ADDRESS);
    }


    // Cycling
    public void cycle() {
        if (scanline <= -1 && Util.getNthBit(ppuMask.getValue(), 3) == 1) { // Pre-Render Scanlines
            runPreRenderScanline();
        } else if (scanline <= 239 && Util.getNthBit(ppuMask.getValue(), 3) == 1) { // Visible Scanlines
            runVisibleScanline();
        } else if (scanline <= 240 && Util.getNthBit(ppuMask.getValue(), 3) == 1) { // Post-Render Scanlines
            runPostRenderScanline();
        } else if (241 <= scanline && scanline <= 260) { // Vertical Blanking Scanlines
            runVerticalBlankingScanline();
        }

        cycle++;
        if (cycle == 341) {
            cycle = 0;
            scanline++;
        }
        if (scanline == 261) {
            isOddFrame = !isOddFrame;
            scanline = -1;
        }
    }

    private void runVisibleScanline() {
        if (cycle <= 0) {   // Idle Cycle
            drawX = 0;
        } else if (cycle <= 256) { // Memory fetches for current scanline
            runVisibleScanlineSpriteEvaluationCycles();
            runVisibleScanlineRenderingCycles();
            oamAddr.setValue(0);
        } else if (cycle <= 320) { // Mostly Garbage memory fetches
            runHorizontalBlankCycles();
        } else if (cycle <= 336) { // Memory fetches for next scanline
            runVisibleScanlineFutureCycles();
        } else if (cycle <= 340) { // Garbage memory fetches
            // Do nothing
        }
    }

    private void runVisibleScanlineSpriteEvaluationCycles() {
        // https://wiki.nesdev.com/w/index.php/PPU_sprite_evaluation
        if (cycle == 1) {
            resetSecondaryOam();
        } else if (cycle == 65) {
            evaluateSprites();
        }
    }

    private void resetSecondaryOam() {
        for (int i = 0; i < secondaryOam.length; i++) {
            secondaryOam[i] = new Address(Integer.parseInt("FF", 16), i);
        }
    }

    private void resetSprites() {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = new Sprite(0, 0, 255, 256, 255);
        }
    }

    protected void evaluateSprites() {
        // https://wiki.nesdev.com/w/index.php/PPU_sprite_evaluation
        int secondaryOamIndex = 0;
        for (int i = 0; i < 64; i++) { // Loop through all the sprites
            int spriteY = primaryOam[i * 4 + 0].getValue();
            if (drawY - 6 <= spriteY && spriteY <= drawY + 1) { // Are we drawing the sprite on the next scanline?
                for (int j = 0; j < 4; j++) { // TODO: possible bug?
                    if (secondaryOamIndex >= 32) { // Should the sprite overflow flag be set?
                        ppuStatus.setValue(ppuStatus.getValue() | Integer.parseInt("00100000", 2));
                    } else {
                        secondaryOam[secondaryOamIndex++].setValue(primaryOam[i * 4 + j].getValue());
                    }
                }
            }
        }
    }

    private void loadSprites() {
        // https://wiki.nesdev.com/w/index.php/PPU_sprite_evaluation
        resetSprites();
        int patternTableSelect = Util.getNthBit(ppuCtrl.getValue(), 3);
        int offset = patternTableSelect * Integer.parseInt("0100", 16);

        for (int i = 0; i < 8; i++) {
            int spriteY = secondaryOam[i * 4 + 0].getValue();
            if (spriteY >= Integer.parseInt("EF", 16)) { // TODO: >= or >?
                continue;
            }

            int patternTableAddress = secondaryOam[i * 4 + 1].getValue();
            int attribute = (secondaryOam[i * 4 + 2].getValue() & Integer.parseInt("00000011", 2)) + 4;
            int spriteX = secondaryOam[i * 4 + 3].getValue();
            int fineY = drawY - spriteY;
            int priority = Util.getNthBit(secondaryOam[i * 4 + 2].getValue(), 5);

            int patternTableLow = Util.reverse(getTileLow(offset + patternTableAddress)[fineY].getValue(), 8);
            int patternTableHigh = Util.reverse(getTileHigh(offset + patternTableAddress)[fineY].getValue(), 8);
            sprites[i] = new Sprite(patternTableLow, patternTableHigh, attribute, spriteX, priority);
        }
    }

    private void runVisibleScanlineRenderingCycles() {
        if (scanline != 0 && cycle == 256) {
            incrementFineY();
            drawY++;
        }

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

    @SuppressWarnings("PointlessArithmeticExpression")
    private void fetchNametableByte() {
        int address = (registerV.getValue() & Integer.parseInt("000111111111111", 2)) >> 0;
        latchNametable = readNametable(address);
        if (drawY != (registerV.getValue() & Integer.parseInt("111000000000000", 2)) >> 12) {
            int x = 2;
        }
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void fetchAttributeTableByte() {
        int coarseX = (registerV.getValue() & Integer.parseInt("000000000011111", 2)) >> 0;
        int coarseY = (registerV.getValue() & Integer.parseInt("000001111100000", 2)) >> 5;
        int nametableAddress = (registerV.getValue() & Integer.parseInt("000110000000000", 2)) >> 10;

        int offset = nametableAddress * NAMETABLE_SIZE + Integer.parseInt("03C0", 16);
        latchAttributeTable = readNametable(offset + (coarseX >> 2) + 8 * (coarseY >> 2));
    }

    private void fetchPatternTableLowByte() {
        int address = latchNametable.getValue();
        int fineY = (registerV.getValue() & Integer.parseInt("111000000000000", 2)) >> 12;

        int patternTableSelect = Util.getNthBit(ppuCtrl.getValue(), 4);
        int offset = patternTableSelect * Integer.parseInt("0100", 16);
        int patternTableLow = Util.reverse(getTileLow(offset + address)[fineY].getValue(), 8);
        latchPatternTableLow.setValue(patternTableLow);
    }

    private void fetchPatternTableHighByte() {
        int address = latchNametable.getValue();
        int fineY = (registerV.getValue() & Integer.parseInt("111000000000000", 2)) >> 12;

        int patternTableSelect = Util.getNthBit(ppuCtrl.getValue(), 4);
        int offset = patternTableSelect * Integer.parseInt("0100", 16);
        int patternTableHigh = Util.reverse(getTileHigh(offset + address)[fineY].getValue(), 8);

        latchPatternTableHigh.setValue(patternTableHigh);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private void loadShiftRegisters() {
        int coarseX1 = Util.getNthBit(registerV.getValue(), 1);
        int coarseY1 = Util.getNthBit(registerV.getValue(), 6);
        int attributeTableLow = Util.getNthBit(latchAttributeTable.getValue(), ((coarseY1 * 2 + coarseX1) << 1) + 0);
        int attributeTableHigh = Util.getNthBit(latchAttributeTable.getValue(), ((coarseY1 * 2 + coarseX1) << 1) + 1);

        shiftRegisterSmall0.setNthBits(0, 8, attributeTableLow == 1 ? 255 : 0);
        shiftRegisterSmall1.setNthBits(0, 8, attributeTableHigh == 1 ? 255 : 0);
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

    protected void incrementCoarseX() {
        int newCoarseX = ((registerV.getValue() & Integer.parseInt("000000000011111", 2)) >> 0) + 1;
        if (newCoarseX >= Math.pow(2, 5)) {
            newCoarseX = 0;
        }

        registerV.setValue(Util.maskNthBits(newCoarseX, registerV.getValue(), 0, 0, 5));
    }

    protected void incrementCoarseY() {
        int newCoarseY = ((registerV.getValue() & Integer.parseInt("000001111100000", 2)) >> 5) + 1;
        if (newCoarseY >= Math.pow(2, 5)) {
            newCoarseY = 0;
        }

        registerV.setValue(Util.maskNthBits(newCoarseY, registerV.getValue(), 0, 5, 5));
    }

    private void renderShiftRegisters() {
        int fineX = registerX.getValue();
        int bitOne = Util.getNthBit(shiftRegisterSmall0.getValue(), fineX);
        int bitTwo = Util.getNthBit(shiftRegisterSmall1.getValue(), fineX);
        int bitThree = Util.getNthBit(shiftRegisterLarge0.getValue(), fineX);
        int bitFour = Util.getNthBit(shiftRegisterLarge1.getValue(), fineX);
        int fullByte = bitOne * 4 + bitTwo * 8 + bitThree * 1 + bitFour * 2;

        Color color = getColor(getColorAddressUsingPriority(fullByte));
        pixels.setPixel(drawX, drawY, color);

        drawX++;
        shiftRegisterSmall0.shiftLeft(1);
        shiftRegisterSmall1.shiftLeft(1);
        shiftRegisterLarge0.shiftLeft(1);
        shiftRegisterLarge1.shiftLeft(1);
    }

    private int getColorAddressUsingPriority(int backgroundFullByte) {
        int returnByte = backgroundFullByte;
        for (int i = 0; i < 8; i++) {
            if (sprites[i].isActive()) {
                int spriteFullByte = sprites[i].getNextColorAddressAsInt();
                int priority = sprites[i].getPriority();
                int bgPixelLow = backgroundFullByte & Integer.parseInt("0011", 2);
                int spritePixelLow = spriteFullByte & Integer.parseInt("0011", 2);

                if (priority == 0 || bgPixelLow == 0) {
                    returnByte = spriteFullByte;
                } else {
                    returnByte = backgroundFullByte;
                }
            }
            sprites[i].decrementCounter();
        }

        return returnByte;
    }

    private Color getColor(int address) {
        return ColorPalette.getColor(paletteRamIndexes.readMemory(address).getValue());
    }

    private void runHorizontalBlankCycles() {
        if (cycle == 257) {                 // Restore the CoarseX
            int newCoarseX = (registerT.getValue() & Integer.parseInt("000000000011111", 2)) >> 0;
            registerV.setValue(Util.maskNthBits(newCoarseX, registerV.getValue(), 0, 0, 5));

            loadSprites();
        }
    }

    private void runVisibleScanlineFutureCycles() {
        switch ((cycle - 321) % 8) {
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
                shiftRegisterSmall0.shiftLeft(8);
                shiftRegisterSmall1.shiftLeft(8);
                shiftRegisterLarge0.shiftLeft(8);
                shiftRegisterLarge1.shiftLeft(8);
                loadShiftRegisters();
                incrementCoarseX();
                break;
        }
    }

    private void runPostRenderScanline() {

    }

    private void runVerticalBlankingScanline() {
        if (scanline == 241 && cycle == 1) {
            ppuStatus.setValue(ppuStatus.getValue() | Integer.parseInt("10000000", 2));
            if (Util.getNthBit(ppuCtrl.getValue(), 7) == 1) {
                bus.setNmi(true);
            }
        }
    }


    private void runPreRenderScanline() {
        if (cycle == 0 && isOddFrame) {
            cycle++;
        }

        drawY = 0;

        if (cycle == 1) {
            ppuStatus.setValue(ppuStatus.getValue() & Integer.parseInt("01111111", 2));
        }

        if (258 <= cycle && cycle <= 320) {
            if (280 <= cycle && cycle <= 304) { // Restore the CoarseY and FineY
                int newFineY = (registerT.getValue() & Integer.parseInt("111000000000000", 2)) >> 12;
                int newCoarseY = (registerT.getValue() & Integer.parseInt("000001111100000", 2)) >> 5;
                registerV.setValue(Util.maskNthBits(newFineY, registerV.getValue(), 0, 12, 3));
                registerV.setValue(Util.maskNthBits(newCoarseY, registerV.getValue(), 0, 5, 5));
            }
            oamAddr.setValue(0);
        } else if (321 <= cycle && cycle <= 336) { // Fetches for next scanline
            runVisibleScanlineFutureCycles();
        }
    }


    public void writeRegister(int pointer, int value) {
        if (pointer == PPUCTRL_ADDRESS) {
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
        // Cannot be done
    }

    private void setOamAddr(int value) {
        oamAddr.setValue(value);
    }

    private void setOamData(int value) {
        primaryOam[oamAddr.getValue()].setValue(value);
        oamAddr.setValue(oamAddr.getValue() + 1);
    }

    private void setPpuScroll(int value) {
        if (registerW.getValue() == 0) { // First Write
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 3, 0, 5));
            registerX.setValue(value & Integer.parseInt("00000111", 2));
        } else {                         // Second Write
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 0, 12, 3));
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 3, 5, 5));
            // TODO:
        }

        registerW.setValue(registerW.getValue() ^ 1);
    }

    private void setPpuAddr(int value) {
        if (registerW.getValue() == 0) { // First Write
            // ppuAddr.setValue(Util.maskNthBits(value, ppuAddr.getValue(), 0, 8, 6));
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 0, 8, 6));
            registerT.setValue(Util.maskNthBits(0, registerT.getValue(), 0, 14, 1));
        } else {                         // Second Write
            // ppuAddr.setValue(Util.maskNthBits(value, ppuAddr.getValue(), 0, 0, 8));
            registerT.setValue(Util.maskNthBits(value, registerT.getValue(), 0, 0, 8));
            registerV.setValue(registerT.getValue());
        }

        registerW.setValue(registerW.getValue() ^ 1);
    }

    private void setPpuData(int value) {
        writeMemory(registerV.getValue(), value);

        if (Util.getNthBit(ppuCtrl.getValue(), 2) == 1) {
            registerV.setValue(registerV.getValue() + 32);
        } else {
            registerV.setValue(registerV.getValue() + 1);
        }
    }

    public Address readRegister(int pointer) {
        if (pointer == PPUCTRL_ADDRESS) {
            return getPpuCtrl();
        } else if (pointer == PPUMASK_ADDRESS) {
            return getPpuMask();
        } else if (pointer == PPUSTATUS_ADDRESS) {
            return getPpuStatus();
        } else if (pointer == OAMADDR_ADDRESS) {
            return new Address(0); // Not allowed!
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

    public Address getPpuCtrl() {
        return ppuCtrl;
    }

    public Address getPpuMask() {
        return ppuMask;
    }

    public Address getPpuStatus() {
        registerW.setValue(0);

        int value1 = ppuStatus.getValue() & Integer.parseInt("11100000", 2);
        int value2 = ppuDataBuffer.getValue() & Integer.parseInt("00011111", 2);

        ppuStatus.setValue(ppuStatus.getValue() & Integer.parseInt("01111111"));
        return new Address(value1 | value2);
    }

    public Address getOamData() {
        return primaryOam[oamAddr.getValue()]; // TODO: something about only incrementing oamAddr during vblank?
    }

    public Address getPpuScroll() {
        return ppuScroll;
    }

    public Address getPpuAddr() {
        return new Address(0); // Cannot be read from!
    }

    public Address getPpuData() {
        ppuData.setValue(ppuDataBuffer.getValue());
        ppuDataBuffer.setValue(readMemory(registerV.getValue()).getValue());
        if (registerV.getValue() >= Integer.parseInt("3F00", 16)) {
            ppuData.setValue(readMemory(registerV.getValue()).getValue());
        }

        if (Util.getNthBit(ppuCtrl.getValue(), 2) == 1) {
            registerV.setValue(registerV.getValue() + 32);
        } else {
            registerV.setValue(registerV.getValue() + 1);
        }

        return ppuData;
    }

    public Address peekPpuCtrl() {
        return ppuCtrl;
    }

    public Address peekPpuMask() {
        return ppuMask;
    }

    public Address peekPpuStatus() {
        return ppuStatus;
    }

    public Address peekOamAddr() {
        return oamAddr;
    }

    public Address peekPpuScroll() {
        return ppuScroll;
    }

    public Address peekPpuData() {
        return ppuData;
    }

    public Address peekPpuDataBuffer() {
        return ppuDataBuffer;
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

        if (pointer <= Integer.parseInt("1FFF", 16)) {
            return bus.mapperReadPpu(pointer);
        } else if (pointer <= Integer.parseInt("2FFF", 16)) {
            return readNametable(pointer - Integer.parseInt("2000", 16));
        } else if (pointer <= Integer.parseInt("3EFF", 16)) {
            return readNametable(pointer - Integer.parseInt("3000", 16));
        } else {
            return paletteRamIndexes.readMemory((pointer - Integer.parseInt("3F00", 16)) % PALETTE_RAM_SIZE);
        }
    }

    protected void writeMemory(int pointer, int value) {
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

        if (pointer <= Integer.parseInt("0FFF", 16)) {
            // patternTables[0].writeMemory(pointer, value);
        } else if (pointer <= Integer.parseInt("1FFF", 16)) {
            // patternTables[1].writeMemory(pointer - Integer.parseInt("1000", 16), value);
        } else if (pointer <= Integer.parseInt("2FFF", 16)) {
            writeNametable(pointer - Integer.parseInt("2000", 16), value);
        } else if (pointer <= Integer.parseInt("3EFF", 16)) {
            writeNametable(pointer - Integer.parseInt("3000", 16), value);
        } else {
            int mirroredAddress = (pointer - Integer.parseInt("3F00", 16)) % PALETTE_RAM_SIZE;
            System.out.print(Integer.toHexString(pointer) + " -> ");
            System.out.print(Integer.toHexString(Integer.parseInt("3F00", 16) + mirroredAddress) + " : ");
            System.out.println(value);
            paletteRamIndexes.writeMemory(mirroredAddress, value);
        }
    }


    // Getters and Setters
    public void setNametableMirroring(Mirroring nametableMirroring) {
        this.nametableMirroring = nametableMirroring;
    }

    protected Address readNametable(int pointer) {
        int rawPointer = pointer;
        switch (nametableMirroring) {
            case HORIZONTAL:
                pointer = pointer % Integer.parseInt("0400", 16);
                pointer += (rawPointer > Integer.parseInt("8000", 16)) ? Integer.parseInt("0800", 16) : 0;
                break;
            case VERTICAL:
                pointer = pointer % Integer.parseInt("0800", 16);
                break;
        }

        return nametable[pointer];
    }

    protected void writeNametable(int pointer, int value) {
        int rawPointer = pointer;
        switch (nametableMirroring) {
            case HORIZONTAL:
                pointer = pointer % Integer.parseInt("0400", 16);
                pointer += (rawPointer > Integer.parseInt("8000", 16)) ? Integer.parseInt("0800", 16) : 0;
                break;
            case VERTICAL:
                pointer = pointer % Integer.parseInt("0800", 16);
                break;
        }

        nametable[pointer].setValue(value);
    }


    public void renderPatternTables(Pixels pixels, int basePalette) {
        renderPatternTable(pixels, 0, 0, 0, basePalette);
        renderPatternTable(pixels, 1, 128, 0, basePalette);
    }

    private void renderPatternTable(Pixels pixels, int patternTable, int offsetX, int offsetY, int basePalette) {
        int offset = patternTable * Integer.parseInt("0100", 16);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Address[] low = getTileLow(offset + i + j * 16);
                Address[] high = getTileHigh(offset + i + j * 16);
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        int formattedLow = Util.getNthBit(low[l].getValue(), 7 - k);
                        int formattedHigh = Util.getNthBit(high[l].getValue(), 7 - k);
                        int palette = formattedLow + formattedHigh * 2 + basePalette * 4;
                        Color color = getColor(palette);
                        pixels.setPixel(i * 8 + k + offsetX, j * 8 + l + offsetY, color);
                    }
                }
            }
        }
    }

    public void renderNameTables(Pixels pixels, int basePalette) {
        int patternTableSelect = Util.getNthBit(ppuCtrl.getValue(), 4);
        int offset = patternTableSelect * Integer.parseInt("0100", 16);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 32; k++) {
                    for (int l = 0; l < 30; l++) {
                        int address = readNametable(((2 * i + j) << 10) + (l << 5) + (k << 0)).getValue();
                        Address[] low = getTileLow(offset + address);
                        Address[] high = getTileHigh(offset + address);

                        for (int m = 0; m < 8; m++) {
                            for (int n = 0; n < 8; n++) {
                                int formattedLow = Util.getNthBit(low[n].getValue(), 7 - m);
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

    public void renderOAM(Pixels pixels, int scaleX, int scaleY) {
        int patternTableSelect = Util.getNthBit(ppuCtrl.getValue(), 3);
        int offset = patternTableSelect * Integer.parseInt("0100", 16);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int attribute = (primaryOam[i * 4 + 2].getValue() & Integer.parseInt("00000011", 2)) + 4;
                int address = primaryOam[i * 4 * 8 + j * 4 + 1].getValue();
                Address[] low = getTileLow(offset + address);
                Address[] high = getTileHigh(offset + address);
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        int formattedLow = Util.getNthBit(low[l].getValue(), 7 - k);
                        int formattedHigh = Util.getNthBit(high[l].getValue(), 7 - k);
                        int fullByte = (attribute << 2) + (formattedHigh << 1) + formattedLow;
                        Color color = getColor(fullByte);
                        for (int m = 0; m < scaleX; m++) {
                            for (int n = 0; n < scaleY; n++) {
                                pixels.setPixel((i * 8 + k) * scaleX + m, (j * 8 + l) * scaleY + n, color);
                            }
                        }
                    }
                }
            }
        }
    }

    private Address[] getTileLow(int pointer) {
        int offset = pointer << 4;

        Address[] tileLow = new Address[8];
        for (int i = 0; i < 8; i++) {
            tileLow[i - 0] = readMemory(offset + i);
        }
        return tileLow;
    }

    private Address[] getTileHigh(int pointer) {
        int tile = pointer;
        int offset = pointer << 4;

        Address[] tileHigh = new Address[8];
        for (int i = 8; i < 16; i++) {
            tileHigh[i - 8] = readMemory(offset + i);
        }
        return tileHigh;
    }

    public void setPixels(Pixels pixels) {
        this.pixels = pixels;
    }

    public void writeOam(int value) {
        primaryOam[oamAddr.getValue()].setValue(value);
        oamAddr.setValue(oamAddr.getValue() + 1);
    }

    protected void setRegisterT(int value) {
        registerT.setValue(value);
    }

    protected void setRegisterV(int value) {
        registerV.setValue(value);
    }

    protected void setRegisterX(int value) {
        registerX.setValue(value);
    }

    protected void setRegisterW(int value) {
        registerW.setValue(value);
    }

    public Address getRegisterT() {
        return registerT;
    }

    public Address getRegisterV() {
        return registerV;
    }

    public Address getRegisterX() {
        return registerX;
    }

    public Address getRegisterW() {
        return registerW;
    }

    public Address getLatchNametable() {
        return latchNametable;
    }

    public Address getLatchAttributeTable() {
        return latchAttributeTable;
    }

    public Address getLatchPatternTableLow() {
        return latchPatternTableLow;
    }

    public Address getLatchPatternTableHigh() {
        return latchPatternTableHigh;
    }

    public ShiftRegister getShiftRegisterSmall0() {
        return shiftRegisterSmall0;
    }

    public ShiftRegister getShiftRegisterSmall1() {
        return shiftRegisterSmall1;
    }

    public ShiftRegister getShiftRegisterLarge0() {
        return shiftRegisterLarge0;
    }

    public ShiftRegister getShiftRegisterLarge1() {
        return shiftRegisterLarge1;
    }

    public Address getOamAddr() {
        return oamAddr;
    }

    public Address getPpuDataBuffer() {
        return ppuDataBuffer;
    }

    public Address[] getNametable() {
        return nametable;
    }

    public Mirroring getNametableMirroring() {
        return nametableMirroring;
    }

    public PaletteRamIndexes getPaletteRamIndexes() {
        return paletteRamIndexes;
    }

    public Address[] getPrimaryOam() {
        return primaryOam;
    }

    public Address[] getSecondaryOam() {
        return secondaryOam;
    }

    public Sprite[] getSprites() {
        return sprites;
    }

    public int getCycle() {
        return cycle;
    }

    public int getScanline() {
        return scanline;
    }

    public int getDrawX() {
        return drawX;
    }

    public int getDrawY() {
        return drawY;
    }

    public boolean isOddFrame() {
        return isOddFrame;
    }

    public boolean getIsOddFrame() {
        return isOddFrame;
    }

    public void setLatchNametable(Address latchNametable) {
        this.latchNametable = latchNametable;
    }

    public void setLatchAttributeTable(Address latchAttributeTable) {
        this.latchAttributeTable = latchAttributeTable;
    }

    public void setLatchPatternTableLow(Address latchPatternTableLow) {
        this.latchPatternTableLow = latchPatternTableLow;
    }

    public void setLatchPatternTableHigh(Address latchPatternTableHigh) {
        this.latchPatternTableHigh = latchPatternTableHigh;
    }

    public void setRegisterV(Address registerV) {
        this.registerV = registerV;
    }

    public void setRegisterT(Address registerT) {
        this.registerT = registerT;
    }

    public void setRegisterX(Address registerX) {
        this.registerX = registerX;
    }

    public void setRegisterW(Address registerW) {
        this.registerW = registerW;
    }

    public void setShiftRegisterSmall0(ShiftRegister shiftRegisterSmall0) {
        this.shiftRegisterSmall0 = shiftRegisterSmall0;
    }

    public void setShiftRegisterSmall1(ShiftRegister shiftRegisterSmall1) {
        this.shiftRegisterSmall1 = shiftRegisterSmall1;
    }

    public void setShiftRegisterLarge0(ShiftRegister shiftRegisterLarge0) {
        this.shiftRegisterLarge0 = shiftRegisterLarge0;
    }

    public void setShiftRegisterLarge1(ShiftRegister shiftRegisterLarge1) {
        this.shiftRegisterLarge1 = shiftRegisterLarge1;
    }

    public void setPpuCtrl(Address ppuCtrl) {
        this.ppuCtrl = ppuCtrl;
    }

    public void setPpuMask(Address ppuMask) {
        this.ppuMask = ppuMask;
    }

    public void setPpuStatus(Address ppuStatus) {
        this.ppuStatus = ppuStatus;
    }

    public void setOamAddr(Address oamAddr) {
        this.oamAddr = oamAddr;
    }

    public void setPpuScroll(Address ppuScroll) {
        this.ppuScroll = ppuScroll;
    }

    public void setPpuData(Address ppuData) {
        this.ppuData = ppuData;
    }

    public void setPpuDataBuffer(Address ppuDataBuffer) {
        this.ppuDataBuffer = ppuDataBuffer;
    }

    public void setNametable(Address[] nametable) {
        this.nametable = nametable;
    }

    public void setPaletteRamIndexes(PaletteRamIndexes paletteRamIndexes) {
        this.paletteRamIndexes = paletteRamIndexes;
    }

    public void setPrimaryOam(Address[] primaryOam) {
        this.primaryOam = primaryOam;
    }

    public void setSecondaryOam(Address[] secondaryOam) {
        this.secondaryOam = secondaryOam;
    }

    public void setSprites(Sprite[] sprites) {
        this.sprites = sprites;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public void setScanline(int scanline) {
        this.scanline = scanline;
    }

    public void setDrawX(int drawX) {
        this.drawX = drawX;
    }

    public void setDrawY(int drawY) {
        this.drawY = drawY;
    }

    public void setOddFrame(boolean oddFrame) {
        isOddFrame = oddFrame;
    }
}