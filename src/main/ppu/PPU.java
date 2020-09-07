package ppu;

import mapper.Mapper;
import model.Bus;
import model.Util;
import ui.Pixels;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

// Lots of information about how the PPU works comes from this video:
// https://www.youtube.com/watch?v=-THeUXqR3zY

// Class PPU:
//     Models the 2C02 PPU in the NES. Renders 8x8 sprites onto the screen and renders the background on the screen.
//     Currently does not support color masking in ppuMask.

public class PPU {
    // Constants
    public  static final int PATTERN_TABLE_SIZE = 0x1000;
    public  static final int NAMETABLE_SIZE     = 0x0400;
    public  static final int PALETTE_RAM_SIZE   = 0x0020;
    public  static final int PRIMARY_OAM_SIZE   = 0x0100;
    public  static final int SECONDARY_OAM_SIZE = 0x0020;
    private static final int PPUCTRL_ADDRESS    = 0x2000;
    private static final int PPUMASK_ADDRESS    = 0x2001;
    private static final int PPUSTATUS_ADDRESS  = 0x2002;
    private static final int OAMADDR_ADDRESS    = 0x2003;
    public  static final int OAMDATA_ADDRESS    = 0x2004;
    private static final int PPUSCROLL_ADDRESS  = 0x2005;
    private static final int PPUADDR_ADDRESS    = 0x2006;
    private static final int PPUDATA_ADDRESS    = 0x2007;
    public  static final int OAMDMA_ADDRESS     = 0x4014;

    private static final int REGISTER_V_SIZE = 15; // bits
    private static final int REGISTER_T_SIZE = 15; // bits
    private static final int REGISTER_X_SIZE = 3;  // bits
    private static final int REGISTER_W_SIZE = 1;  // bits

    private static final int SHIFT_REGISTER_SMALL_SIZE = 8;
    private static final int SHIFT_REGISTER_LARGE_SIZE = 16;

    private static final int INITIAL_REGISTER_V = 0b00000000000000;
    private static final int INITIAL_REGISTER_T = 0b00000000000000;
    private static final int INITIAL_REGISTER_X = 0b000;
    private static final int INITIAL_REGISTER_W = 0b0;

    private static final int NUM_TILES = 64;
    private static final int NUM_NAMETABLES = 4;
    private static final int NUM_PATTERNTABLES = 2;
    private static final int NUM_LATCHES = 4;
    private static final int NUM_CYCLES = 341;
    private static final int NUM_SCANLINES = 261;

    private int latchNametable;
    private int latchAttributeTable;
    private int latchPatternTableLow;
    private int latchPatternTableHigh;

    // Registers and Latches
    public int registerV;
    public int registerT;
    public int registerX;
    public int registerW;

    private ShiftRegister shiftRegisterSmall0;
    private ShiftRegister shiftRegisterSmall1;
    private ShiftRegister shiftRegisterLarge0;
    private ShiftRegister shiftRegisterLarge1;

    public int ppuCtrl;
    public int ppuMask;
    public int ppuStatus;
    public int oamAddr;
    public int ppuScroll;
    public int ppuData;
    public int ppuDataBuffer;

    // Memory
    private int[] nametable;
    private Mirroring nametableMirroring;
    protected PaletteRamIndexes paletteRamIndexes;
    private int[] primaryOam;
    private int[] secondaryOam;
    private Sprite[] sprites;

    // Cycling
    protected int cycle;
    protected int scanline;
    private int drawX;
    private int drawY;
    private boolean isOddFrame;

    private Pixels pixels;

    // true if we can optimize by rendering the full screen at once,
    // false if not. this can be used to make some MAJOR optimizations if it's false.
    // essentially, we can skip rendering pixel by pixel and just put the nametable on
    // the screen directly.
    private boolean canRenderFullScreen;
    private int[] backgroundCache; // used to draw the sprites after the background is rendered. contains background data.

    // MODIFIES: this
    // EFFECTS: initializes the ppu, connects it to the bus, and resets it.
    public PPU() {
        nametable = new int[NUM_NAMETABLES * NAMETABLE_SIZE];
        paletteRamIndexes = new PaletteRamIndexes();
        primaryOam = new int[PRIMARY_OAM_SIZE];
        secondaryOam = new int[SECONDARY_OAM_SIZE];
        sprites = new Sprite[8];

        shiftRegisterSmall0 = new ShiftRegister(SHIFT_REGISTER_SMALL_SIZE);
        shiftRegisterSmall1 = new ShiftRegister(SHIFT_REGISTER_SMALL_SIZE);
        shiftRegisterLarge0 = new ShiftRegister(SHIFT_REGISTER_LARGE_SIZE);
        shiftRegisterLarge1 = new ShiftRegister(SHIFT_REGISTER_LARGE_SIZE);

        reset();
    }

    // MODIFIES: this
    // EFFECTS: resets all registers, latches, and cycling data of the PPU to their default values
    public void reset() {
        registerV = INITIAL_REGISTER_V;
        registerT = INITIAL_REGISTER_T;
        registerX = INITIAL_REGISTER_X;
        registerW = INITIAL_REGISTER_W;
        ppuDataBuffer = 0;

        shiftRegisterSmall0.setNthBits(0, 8, 0);
        shiftRegisterSmall1.setNthBits(0, 8, 0);
        shiftRegisterLarge0.setNthBits(0, 8, 0);
        shiftRegisterLarge1.setNthBits(0, 8, 0);

        latchNametable        = 0;
        latchAttributeTable   = 0;
        latchPatternTableLow  = 0;
        latchPatternTableHigh = 0;

        cycle      = 0;
        scanline   = 0;
        isOddFrame = false;

        // used for optimization
        canRenderFullScreen = true;
        backgroundCache = new int[256 * 240];

        resetNametables();
        resetPrimaryOam();
        resetSecondaryOam();
        resetSprites();
        setupInternalRegisters();
    }

    // MODIFIES: nametable
    // EFFECTS: resets the nametables to their default values
    private void resetNametables() {
        for (int i = 0; i < nametable.length; i++) {
            nametable[i] = 0;
        }
    }

    // MODIFIES: primaryOam
    // EFFECTS: resets the primary OAM to its default value
    private void resetPrimaryOam() {
        for (int i = 0; i < primaryOam.length; i++) {
            primaryOam[i] = 0xFF;
        }
    }

    // MODIFIES: secondaryOam
    // EFFECTS:  resets the secondary OAM to its default value
    private void resetSecondaryOam() {
        for (int i = 0; i < secondaryOam.length; i++) {
            secondaryOam[i] = 0xFF;
        }
    }

    // MODIFIES: sprites
    // EFFECTS: resets the sprites to their default value
    private void resetSprites() {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = new Sprite(0, 0, 255, 256, 255, false, false);
        }
    }

    // MODIFIES: ppuCtrl, ppuMask, ppuStatus, oamAddr, ppuScroll, ppuData
    // EFFECTS: resets the internal registers to their default values
    private void setupInternalRegisters() {
        ppuCtrl = 0;
        ppuMask = 0;
        ppuStatus = 0;
        oamAddr = 0;
        ppuScroll = 0;
        ppuData = 0;
    }

    // REQUIRES: 0 <= scanline <= 260
    // MODIFIES: this
    // EFFECTS: runs the appropriate scanline and increments the cycle. Increments scanline if cycle overflows, and
    //          toggles isOddFrame when scanline overflows.
    public void cycle() {
        if (canRenderFullScreen) {
            // if we can optimize by rendering the full screen, we can just go straight to rendering.
            if (scanline == 0) {
                if (cycle == 0) {
                    // get sprite zero's y position
                    int sprite0Y = primaryOam[0];

                    // render the whole screen till there.
                    renderScreenNametables(0, sprite0Y >> 3, pixels);
                    // as well as the sprites, those most likely wont be affected by sprite0 hits.
                    renderSprites(pixels);
                }
            } else if (scanline == 240) {
                if (cycle == 0) {
                    // get sprite zero's location
                    int sprite0Y = primaryOam[0];

                    pixels.storeBuffer();
                    renderScreenNametables(sprite0Y >> 3, 32, pixels);
                }
            } else if (241 <= scanline && scanline <= 260) { // Vertical Blanking Scanlines
                runVerticalBlankingScanline();
            }


        } else {
            if (scanline <= -1 && Util.getNthBit(ppuMask, 3) == 1) { // Pre-Render Scanlines
                runPreRenderScanline();
            } else if (scanline <= 239 && Util.getNthBit(ppuMask, 3) == 1) { // Visible Scanlines
                runVisibleScanline();
            } else if (scanline <= 240 && Util.getNthBit(ppuMask, 3) == 1) { // Post-Render Scanlines
                runPostRenderScanline();
            } else if (241 <= scanline && scanline <= 260) { // Vertical Blanking Scanlines
                runVerticalBlankingScanline();
            }
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

    // REQUIRES: 0 <= cycle <= 340
    // MODIFIES: this
    // EFFECTS: runs the appropriate visible scanline cycle based on the value of cycle
    //          resets drawX if cycle <= 0
    private void runVisibleScanline() {
        if (cycle <= 0) {   // Idle Cycle
            drawX = 0;
        } else if (cycle <= 256) { // Memory fetches for current scanline
            runVisibleScanlineSpriteEvaluationCycles();
            runVisibleScanlineRenderingCycles();
            oamAddr = 0;
        } else if (cycle <= 320) { // Mostly Garbage memory fetches
            runHorizontalBlankCycles();
        } else if (cycle <= 336) { // Memory fetches for next scanline
            runVisibleScanlineFutureCycles();
        } else if (cycle <= 340) { // Garbage memory fetches
            // Do nothing
        }
    }

    // REQUIRES: 1 <= cycle <= 256
    // MODIFIES: secondaryOam, sprites
    // EFFECTS: if cycle == 1,  resets the secondaryOam
    //          if cycle == 65, calculates which sprites should appear on the next scanline
    private void runVisibleScanlineSpriteEvaluationCycles() {
        // https://wiki.nesdev.com/w/index.php/PPU_sprite_evaluation
        if (cycle == 1) {
            resetSecondaryOam();
        } else if (cycle == 65) {
            evaluateSprites();
        }
    }

    // MODIFIES: secondaryOam, ppuStatus
    // EFFECTS: reads through the primaryOam, and if the sprite will appear on the next scanline, copies that
    //          sprites' data to secondaryOam. If more than 8 sprites can render on the next scanline, only the first
    //          8 will be displayed and the sprite overflow flag in ppuStatus will be set.
    protected void evaluateSprites() {
        // https://wiki.nesdev.com/w/index.php/PPU_sprite_evaluation
        int secondaryOamIndex = 0;
        for (int i = 0; i < 64; i++) { // Loop through all the sprites
            int spriteY = primaryOam[i * 4 + 0];
            if (drawY - 6 <= spriteY && spriteY <= drawY + 1) { // Are we drawing the sprite on the next scanline?
                for (int j = 0; j < 4; j++) {
                    if (secondaryOamIndex >= 32) { // Should the sprite overflow flag be set?
                        ppuStatus |= 0b00100000;
                    } else {
                        secondaryOam[secondaryOamIndex++] = primaryOam[i * 4 + j];
                    }
                }
            }
        }
    }

    // MODIFIES: sprites
    // EFFECTS: converts the data in secondaryOam to sprite objects that can be rendered by the PPU
    private void loadSprites() {
        // https://wiki.nesdev.com/w/index.php/PPU_sprite_evaluation
        resetSprites();
        int patternTableSelect = Util.getNthBit(ppuCtrl, 3);
        int offset = patternTableSelect * 0x0100;

        for (int i = 0; i < 8; i++) {
            int spriteY = secondaryOam[i * 4 + 0];
            if (spriteY >= 0xEF) { // TODO: >= or >?
                continue;
            }

            int patternTableAddress = secondaryOam[i * 4 + 1];
            int attribute = Util.getNthBits(secondaryOam[i * 4 + 2], 0, 2) + 4;
            int spriteX = secondaryOam[i * 4 + 3];

            int fineY = drawY - spriteY;
            boolean isMirroredVertically = Util.getNthBit(secondaryOam[i * 4 + 2], 7) == 1;
            if (isMirroredVertically) {
                fineY = 7 - fineY;
            }
            int priority = Util.getNthBit(secondaryOam[i * 4 + 2], 5);

            int patternTableLow = Util.reverse(getTileLow(offset + patternTableAddress)[fineY], 8);
            int patternTableHigh = Util.reverse(getTileHigh(offset + patternTableAddress)[fineY], 8);
            boolean isMirroredHorizontally = Util.getNthBit(secondaryOam[i * 4 + 2], 6) == 1;
            sprites[i] = new Sprite(patternTableLow, patternTableHigh, attribute, spriteX, priority,
                    isMirroredHorizontally, isMirroredVertically);
        }
    }

    // REQUIRES: 1 <= cycle <= 256
    // MODIFIES: this
    // EFFECTS: increments the fineY if this is the 256th cycle and scanline != 0. Processes the data in the nametables,
    //          attribute tables, and pattern tables to render one pixel per 8 cycles.
    private void runVisibleScanlineRenderingCycles() {
        if (scanline != 0 && cycle == 256) {
            incrementFineY();
            drawY++;
        }

        renderShiftRegisters();
        switch ((cycle - 1) & 0x7) {
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

        incrementFineX();
    }

    // MODIFIES: latchNametable
    // EFFECTS:  reads the nametable at the address stored in registerV and stores the result in latchNametable
    @SuppressWarnings("PointlessArithmeticExpression")
    private void fetchNametableByte() {
        int address = Util.getNthBits(registerV, 0, 12);
        latchNametable = readNametable(address);
    }

    // MODIFIES: latchAttributeTable
    // EFFECTS:  reads the attributeTable at the address stored in registerV and stores the result
    //           in latchAttributeTable
    @SuppressWarnings("PointlessArithmeticExpression")
    private void fetchAttributeTableByte() {
        int coarseX = Util.getNthBits(registerV, 0, 5);
        int coarseY = Util.getNthBits(registerV, 5, 5);
        int nametableAddress = Util.getNthBits(registerV, 10, 2);

        int offset = nametableAddress * NAMETABLE_SIZE + 0x03C0;
        latchAttributeTable = readNametable(offset + (coarseX >> 2) + 8 * (coarseY >> 2));
    }

    // MODIFIES: latchPatternTableLow
    // EFFECTS:  reads the low byte of the patternTable at the address stored in registerV and stores the result
    //           in latchPatternTableLow
    private void fetchPatternTableLowByte() {
        int address = latchNametable;
        int fineY = getFineY();

        int patternTableSelect = Util.getNthBit(ppuCtrl, 4);
        int offset = patternTableSelect * 0x0100;
        int patternTableLow = Util.reverse(getTileLow(offset + address)[fineY], 8);
        latchPatternTableLow = patternTableLow;
    }

    private int getFineY() {
        return Util.getNthBits(registerV, 12, 3);
    }

    // MODIFIES: latchPatternTableHigh
    // EFFECTS:  reads the high byte of the patternTable at the address stored in registerV and stores the result
    //           in latchPatternTableHigh
    private void fetchPatternTableHighByte() {
        int address = latchNametable;
        int fineY = getFineY();

        int patternTableSelect = Util.getNthBit(ppuCtrl, 4);
        int offset = patternTableSelect * 0x0100;
        int patternTableHigh = Util.reverse(getTileHigh(offset + address)[fineY], 8);

        latchPatternTableHigh = patternTableHigh;
    }

    // MODIFIES: shiftRegisterSmall0, shiftRegisterSmall1, shiftRegisterLarge0, shiftRegisterLarge1
    // EFFECTS:  loads the values of the attribute table and the pattern table into the shift registers.
    //           NOTE: since latchAttributeTable is 2 bits and its corresponding shift registers are both 8 bits,
    //           the shiftRegisters are just inflated with the same value (in other words, 1 becomes 11111111)
    @SuppressWarnings("PointlessArithmeticExpression")
    private void loadShiftRegisters() {
        int coarseX1 = Util.getNthBit(registerV, 1);
        int coarseY1 = Util.getNthBit(registerV, 6);
        int attributeTableLow = Util.getNthBit(latchAttributeTable, ((coarseY1 * 2 + coarseX1) << 1) + 0);
        int attributeTableHigh = Util.getNthBit(latchAttributeTable, ((coarseY1 * 2 + coarseX1) << 1) + 1);

        shiftRegisterSmall0.setNthBits(0, 8, attributeTableLow == 1 ? 255 : 0);
        shiftRegisterSmall1.setNthBits(0, 8, attributeTableHigh == 1 ? 255 : 0);
        shiftRegisterLarge0.setNthBits(8, 16, latchPatternTableLow);
        shiftRegisterLarge1.setNthBits(8, 16, latchPatternTableHigh);
    }

    // MODIFIES: registerV
    // EFFECTS: increments coarseX if drawX % 8 == 0
    private void incrementFineX() {
        if ((drawX & 0x7) == 0) {
            incrementCoarseX();
        }
    }

    // MODIFIES: registerV
    // EFFECTS: increments fineY, and increments coarseY if fineY overflows
    private void incrementFineY() {
        setRegisterV(registerV + 0b001000000000000);  // Increment fineY
        int fineY = getFineY();

        if (fineY == 0) {
            incrementCoarseY();
        }
    }

    // MODIFIES: registerV
    // EFFECTS: increments coarseX.
    protected void incrementCoarseX() {
        int newCoarseX = Util.getNthBits(registerV, 0, 5) + 1;
        if (newCoarseX >= Math.pow(2, 5)) {
            newCoarseX = 0;
        }

        setRegisterV(Util.maskNthBits(newCoarseX, registerV, 0, 0, 5));
    }

    // MODIFIES: registerV
    // EFFECTS: increments coarseY.
    protected void incrementCoarseY() {
        int newCoarseY = Util.getNthBits(registerV, 5, 5) + 1;
        if (newCoarseY >= Math.pow(2, 5)) {
            newCoarseY = 0;
        }

        setRegisterV(Util.maskNthBits(newCoarseY, registerV, 0, 5, 5));
    }

    // MODIFIES: shiftRegisterSmall0, shiftRegisterSmall1, shiftRegisterLarge0, shiftRegisterLarge1, pixels, drawX
    // EFFECTS: renders one pixel on the screen based on the values in the shift registers.
    //          increments drawX and shifts the shift registers.
    private void renderShiftRegisters() {
        int fineX = registerX;
        int bitOne = Util.getNthBit(shiftRegisterSmall0.getValue(), fineX);
        int bitTwo = Util.getNthBit(shiftRegisterSmall1.getValue(), fineX);
        int bitThree = Util.getNthBit(shiftRegisterLarge0.getValue(), fineX);
        int bitFour = Util.getNthBit(shiftRegisterLarge1.getValue(), fineX);
        int fullByte = bitOne * 4 + bitTwo * 8 + bitThree * 1 + bitFour * 2;

        Color color = getColor(getColorAddressUsingPriority(fullByte));
        pixels.setPixel(drawX & 0xFF, drawY % 240, color);

        drawX++;
        shiftRegisterSmall0.shiftLeft(1);
        shiftRegisterSmall1.shiftLeft(1);
        shiftRegisterLarge0.shiftLeft(1);
        shiftRegisterLarge1.shiftLeft(1);
    }

    // MODIFIES: sprites
    // EFFECTS: uses the values stored in the sprite to decide whether or not to display the background or the sprite.
    //          if the sprite's priority is 0 (foreground) or the background pixel is empty (0), returns the background
    //          pixel. else, returns the sprite pixel
    private int getColorAddressUsingPriority(int backgroundFullByte) {
        int returnByte = backgroundFullByte;
        boolean isReturningSprite = false;

        for (int i = 0; i < 8; i++) {
            if (sprites[i].isActive()) {
                int spriteFullByte = sprites[i].getNextColorAddressAsInt();
                int priority = sprites[i].getPriority();
                int bgPixelLow = Util.getNthBits(backgroundFullByte, 0, 2);
                int spritePixelLow = Util.getNthBits(spriteFullByte, 0, 2);

                if (spritePixelLow != 0 && (bgPixelLow == 0 || priority == 0)) {
                    returnByte = spriteFullByte;
                    isReturningSprite = true;
                } else {
                    if (!isReturningSprite) {
                        returnByte = backgroundFullByte;
                    }
                }
            }
            sprites[i].decrementCounter();
        }

        return returnByte;
    }

    // EFFECTS: returns the color associated with the given paletteRamIndexes address
    private Color getColor(int address) {
        return ColorPalette.getColor(paletteRamIndexes.readMemory(address));
    }

    // REQUIRES: 257 <= cycle <= 320
    // MODIFIES: this
    // EFFECTS:  runs the horizontal blank cycle. When cycle == 256, coarseX is restored to the value in registerT
    //           and the sprites are reloaded.
    private void runHorizontalBlankCycles() {
        if (cycle == 257) {                 // Restore the CoarseX
            int newCoarseX = Util.getNthBits(registerT, 0, 5);
            setRegisterV(Util.maskNthBits(newCoarseX, registerV, 0, 0, 5));

            loadSprites();
        }
    }

    // REQUIRES: 321 <= cycle <= 336
    // MODIFIES: this
    // EFFECTS:  prepares the latches for rendering on the next scanline
    private void runVisibleScanlineFutureCycles() {
        switch ((cycle - 321) & 0x7) {
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

    // EFFECTS: nothing happens
    private void runPostRenderScanline() {

    }

    // MODIFIES: this, bus
    // EFFECTS: sets bus NMI if the 7th bit of PPUStatus is set, scanline == 241, and cycle == 1
    private void runVerticalBlankingScanline() {
        if (scanline == 241 && cycle == 1) {
            ppuStatus = (ppuStatus | 0b10000000);
            if (Util.getNthBit(ppuCtrl, 7) == 1) {
                Bus.getInstance().setNmi(true);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: runs dummy reads for the preRenderCycle. Sets up the ppu for the upcoming scanline.
    private void runPreRenderScanline() {
        if (cycle == 0 && isOddFrame) {
            cycle++;
        }

        drawY = 0;

        if (cycle == 1) {
            ppuStatus = (Util.maskNthBits(0, ppuStatus, 0, 7, 1));
        }

        if (258 <= cycle && cycle <= 320) {
            if (280 <= cycle && cycle <= 304) { // Restore the CoarseY and FineY
                int newFineY = Util.getNthBits(registerT, 12, 3);
                int newCoarseY = Util.getNthBits(registerT, 5, 5);
                setRegisterV(Util.maskNthBits(newFineY, registerV, 0, 12, 3));
                setRegisterV(Util.maskNthBits(newCoarseY, registerV, 0, 5, 5));
            }
            oamAddr = 0;
        } else if (321 <= cycle && cycle <= 336) { // Fetches for next scanline
            runVisibleScanlineFutureCycles();
        }
    }

    // MODIFIES: this:
    // EFFECTS: writes the value to the appropriate PPU register.
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

    // MODIFIES: ppuCtrl
    // EFFECTS:  setsPpuCtrl to the value and updates registerT accordingly
    private void setPpuCtrl(int value) {
        ppuCtrl = value;
        setRegisterT(Util.maskNthBits(value, registerT, 0, 10, 2));
    }

    private void setPpuMask(int value) {
        ppuMask = value;
    }

    private void setPpuStatus(int value) {
        // Cannot be done
    }

    private void setOamAddr(int value) {
        oamAddr = value;
    }

    // MODIFIES: primaryOam, oamAddr
    // EFFECTS: sets primaryOam at index oamAddr to the value and increments oamAddr
    private void setOamData(int value) {
        primaryOam[oamAddr] = value;
        oamAddr = (oamAddr + 1);
    }

    // MODIFIES: registerT, registerX, registerW
    // EFFECTS: ppuScroll is a write x2 register. Sets registerT according to the value in registerW and the value
    //          passed into the function.
    private void setPpuScroll(int value) {
        if (registerW == 0) { // First Write
            setRegisterT(Util.maskNthBits(value, registerT, 3, 0, 5));
            setRegisterX(Util.getNthBits(value, 0, 3));
        } else {                         // Second Write
            setRegisterT(Util.maskNthBits(value, registerT, 0, 12, 3));
            setRegisterT(Util.maskNthBits(value, registerT, 3, 5, 5));
            // TODO:
        }

        setRegisterW(registerW ^ 1);
    }

    // MODIFIES: registerW, registerT, registerV
    // EFFECTS: ppuAddr is a write x2 register. Sets registerT and registerV according to the value in registerW and
    //          the value passed into the function.
    private void setPpuAddr(int value) {
        if (registerW == 0) { // First Write
            // ppuAddr.setValue(Util.maskNthBits(value, ppuAddr.getValue(), 0, 8, 6));
            setRegisterT(Util.maskNthBits(value, registerT, 0, 8, 6));
            setRegisterT(Util.maskNthBits(0, registerT, 0, 14, 1));
        } else {                         // Second Write
            // ppuAddr.setValue(Util.maskNthBits(value, ppuAddr.getValue(), 0, 0, 8));
            setRegisterT(Util.maskNthBits(value, registerT, 0, 0, 8));
            setRegisterV(registerT);
        }

        setRegisterW(registerW ^ 1);
    }

    // MODIFIES: this
    // EFFECTS: sets the value in memory at address registerV to value, and increments registerV according to ppuCtrl.
    private void setPpuData(int value) {
        writeMemory(registerV, value);

        if (Util.getNthBit(ppuCtrl, 2) == 1) {
            registerV = registerV + 32;
        } else {
            registerV = registerV + 1;
        }
    }

    // MODIFIES: this
    // EFFECTS: returns the value accessed from the specific register. Some accesses modify the PPU's state
    public int readRegister(int pointer) {
        if (pointer == PPUCTRL_ADDRESS) {
            return getPpuCtrl();
        } else if (pointer == PPUMASK_ADDRESS) {
            return getPpuMask();
        } else if (pointer == PPUSTATUS_ADDRESS) {
            return getPpuStatus();
        } else if (pointer == OAMADDR_ADDRESS) {
            return 0; // Not allowed!
        } else if (pointer == OAMDATA_ADDRESS) {
            return getOamData();
        } else if (pointer == PPUSCROLL_ADDRESS) {
            return getPpuScroll();
        } else if (pointer == PPUADDR_ADDRESS) {
            return getPpuAddr();
        } else if (pointer == PPUDATA_ADDRESS) {
            return getPpuData();
        }

        return 0;
    }

    public int getPpuCtrl() {
        return ppuCtrl;
    }

    public int getPpuMask() {
        return ppuMask;
    }

    // MODIFIES: registerW, ppuStatus
    // EFFECTS:  resets registerW and sets the 7th bit of ppuStatus to false. Returns ppuStatus
    public int getPpuStatus() {
        registerW = 0;

        int value1 = ppuStatus & 0b11100000;
        int value2 = ppuDataBuffer & 0b00011111;

        ppuStatus = (Util.getNthBits(ppuStatus, 0, 7));
        return (value1 | value2);
    }

    public int getOamData() {
        return primaryOam[oamAddr]; // TODO: something about only incrementing oamAddr during vblank?
    }

    public int getPpuScroll() {
        return ppuScroll;
    }

    public int getPpuAddr() {
        return 0; // Cannot be read from!
    }

    // MODIFIES: ppuData, ppuDataBuffer, registerV
    // EFFECTS:  returns the value in ppuData after the second access. returns the value immediately if the pointer is
    //           in the paletteRamIndexes (>= 0x3F00).
    public int getPpuData() {
        ppuData = ppuDataBuffer;
        ppuDataBuffer = (readMemory(registerV));
        if (registerV >= 0x3F00) {
            ppuData = (readMemory(registerV));
        }

        if (Util.getNthBit(ppuCtrl, 2) == 1) {
            setRegisterV(registerV + 32);
        } else {
            setRegisterV(registerV + 1);
        }

        return ppuData;
    }

    public int peekPpuCtrl() {
        return ppuCtrl;
    }

    public int peekPpuMask() {
        return ppuMask;
    }

    public int peekPpuStatus() {
        return ppuStatus;
    }

    public int peekOamAddr() {
        return oamAddr;
    }

    public int peekPpuScroll() {
        return ppuScroll;
    }

    public int peekPpuData() {
        return ppuData;
    }

    public int peekPpuDataBuffer() {
        return ppuDataBuffer;
    }

    // EFFECTS: reads the value in memory at the given pointer and returns it. See table below for more info.
    public int readMemory(int pointer) {
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

        if (pointer <= 0x1FFF) {
            return Bus.getInstance().mapperReadPpu(pointer);
        } else if (pointer <= 0x2FFF) {
            return readNametable(pointer - 0x2000);
        } else if (pointer <= 0x3EFF) {
            return readNametable(pointer - 0x3000);
        } else {
            return paletteRamIndexes.readMemory((pointer - 0x3F00) & 0x001F);
        }
    }

    // MODIFIES: this
    // EFFECTS: writes the value in memory at the given pointer and returns it. See table below for more info.
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

        if (pointer <= 0x0FFF) {
            // patternTables[0].writeMemory(pointer, value);
        } else if (pointer <= 0x1FFF) {
            // patternTables[1].writeMemory(pointer - 0x1000, value);
        } else if (pointer <= 0x2FFF) {
            writeNametable(pointer - 0x2000, value);
        } else if (pointer <= 0x3EFF) {
            writeNametable(pointer - 0x3000, value);
        } else {
            int mirroredAddress = (pointer - 0x3F00) & 0x001F;
            // System.out.print(Integer.toHexString(pointer) + " -> ");
            // System.out.print(Integer.toHexString(0x3F00 + mirroredAddress) + " : ");
            // System.out.println(value);
            paletteRamIndexes.writeMemory(mirroredAddress, value);
        }
    }


    public void setNametableMirroring(Mirroring nametableMirroring) {
        this.nametableMirroring = nametableMirroring;
    }

    // EFFECTS: mirrors the pointer according to the nametableMirroring and returns the value at that pointer in the
    //          nametable
    protected int readNametable(int pointer) {
        int rawPointer = pointer;
        switch (nametableMirroring) {
            case HORIZONTAL:
                pointer = pointer & 0x03FF;
                pointer += (rawPointer > 0x8000) ? 0x0800 : 0;
                break;
            case VERTICAL:
                pointer = pointer & 0x07FF;
                break;
        }

        return nametable[pointer];
    }

    // MODIFIES: nametable
    // EFFECTS:  mirrors the pointer according to the nametableMirroring and writes the value at that pointer in the
    //           nametable
    protected void writeNametable(int pointer, int value) {
        int rawPointer = pointer;
        switch (nametableMirroring) {
            case HORIZONTAL:
                pointer = pointer & 0x03FF;
                pointer += (rawPointer > 0x8000) ? 0x0800 : 0;
                break;
            case VERTICAL:
                pointer = pointer & 0x07FF;
                break;
        }

        nametable[pointer] = value;
    }

    // MODIFIES: pixels
    // EFFECTS:  renders the patternTables to the pixels using the given basePalette. This is because a given tile can
    //           use multiple palettes, so a basePalette must be specified.
    public void renderPatternTables(Pixels pixels, int basePalette) {
        renderPatternTable(pixels, 0, 0,   0, basePalette);
        renderPatternTable(pixels, 1, 128, 0, basePalette);
    }

    // MODIFIES: pixels
    // EFFECTS:  renders one half of the pattern table to the pixels using the given offsets and basePalette.
    private void renderPatternTable(Pixels pixels, int patternTable, int offsetX, int offsetY, int basePalette) {
        int offset = patternTable * 0x0100;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int[] low = getTileLow(offset + i + j * 16);
                int[] high = getTileHigh(offset + i + j * 16);
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        int formattedLow = Util.getNthBit(low[l], 7 - k);
                        int formattedHigh = Util.getNthBit(high[l], 7 - k);
                        int palette = formattedLow + formattedHigh * 2 + basePalette * 4;
                        Color color = getColor(palette);
                        pixels.setPixel(i * 8 + k + offsetX, j * 8 + l + offsetY, color);
                    }
                }
            }
        }

        pixels.storeBuffer();
    }

    // MODIFIES: pixels
    // EFFECTS:  renders the nametable to the pixels using the given basePalette. This is because a given tile can
    //           use multiple palettes, so a basePalette must be specified.
    public void renderNameTables(Pixels pixels, int basePalette) {
        int patternTableSelect = Util.getNthBit(ppuCtrl, 4);
        int offset = patternTableSelect * 0x0100;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 32; k++) {
                    for (int l = 0; l < 30; l++) {
                        int address = readNametable(((2 * i + j) << 10) + (l << 5) + (k << 0));
                        int[] low = getTileLow(offset + address);
                        int[] high = getTileHigh(offset + address);

                        for (int m = 0; m < 8; m++) {
                            for (int n = 0; n < 8; n++) {
                                int formattedLow = Util.getNthBit(low[n], 7 - m);
                                int formattedHigh = Util.getNthBit(high[n], 7 - m);
                                int palette = formattedLow + formattedHigh * 2 + basePalette * 4;
                                Color color = getColor(palette);
                                pixels.setPixel(k * 8 + m + (i * 256), l * 8 + n + (j * 256), color);
                            }
                        }
                    }
                }
            }
        }

        pixels.storeBuffer();
    }

    private void renderScreenNametables(int y1, int y2, Pixels pixels) {
        // this chunk of code renders the nametables
        // its the same as the renderNametables function, just without the outer
        // four loops and with a few lines of code that determines the base palette for each tile.
        int patternTableSelect = Util.getNthBit(ppuCtrl, 4);
        int offset = patternTableSelect * 0x0100;

        // y2 might be at max 32, because of how sprite data is stored in the NES PPU.
        // so this corrects it if that is the case.
        if (y2 > 30)
            y2 = 30;

        for (int k = 0; k < 32; k++) {
            for (int l = y1; l < y2; l++) {
                int address = readNametable((2 << 10) + (l << 5) + (k << 0));
                int[] low = getTileLow(offset + address);
                int[] high = getTileHigh(offset + address);

                int attributeTableOffset = Util.getNthBits(registerV, 10, 2) * NAMETABLE_SIZE + 0x03C0;
                int attributeTableData = readNametable(attributeTableOffset + (k >> 2) + 8 * (l >> 2));
                int attributeTableLow  = Util.getNthBit(attributeTableData, ((((k & 2) >> 1) + ((l & 2))) << 1) + 0);
                int attributeTableHigh = Util.getNthBit(attributeTableData, ((((k & 2) >> 1) + ((l & 2))) << 1) + 1);

                for (int m = 0; m < 8; m++) {
                    for (int n = 0; n < 8; n++) {
                        int formattedLow = Util.getNthBit(low[n], 7 - m);
                        int formattedHigh = Util.getNthBit(high[n], 7 - m);

                        int basePalette = attributeTableLow + (attributeTableHigh << 1);
                        int palette = formattedLow + formattedHigh * 2 + basePalette * 4;

                        backgroundCache[(k * 8 + m) + (l * 8 + n) * 256] = palette;
                        Color color = getColor(palette);
                        pixels.setPixel(k * 8 + m, l * 8 + n, color);
                    }
                }
            }
        }
    }

    private void renderSprites(Pixels pixels) {
        int patternTableSelectSprites = Util.getNthBit(ppuCtrl, 3);
        int offsetSprites = patternTableSelectSprites * 0x0100;

        for (int i = 0; i < 64; i++) {
            // get the relevant data from primaryOam
            int spriteX                    =  primaryOam[i * 4 + 3];
            int spriteY                    =  primaryOam[i * 4 + 0];
            int tileNumber                 =  primaryOam[i * 4 + 1];
            int palette                    = (primaryOam[i * 4 + 2] & 0x3) + 4;
            boolean isMirroredVertically   = (primaryOam[i * 4 + 2] & 0x80) != 0;
            boolean isMirroredHorizontally = (primaryOam[i * 4 + 2] & 0x40) != 0;
            int priority                   = (primaryOam[i * 4 + 2] & 0x20);

            // get the pattern table data
            int[] low  = getTileLow (offsetSprites + tileNumber);
            int[] high = getTileHigh(offsetSprites + tileNumber);

            // loop through the bounds of the current sprite and begin rendering
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    int formattedLow =  Util.getNthBit( low[isMirroredVertically ? 7 - y : y],
                            isMirroredHorizontally ? x : 7 - x);
                    int formattedHigh = Util.getNthBit(high[isMirroredVertically ? 7 - y : y],
                            isMirroredHorizontally ? x : 7 - x);

                    // use priority to determine if we should just skip this sprite or not (i.e., render the background
                    // instead of the sprite)
                    int spriteFullByte = (palette << 2) + (formattedHigh << 1) + formattedLow;

                    int index = (spriteX + x) + (spriteY + y) * 256;
                    if (index > 256 * 240) continue;

                    int bgPixelLow = Util.getNthBits(backgroundCache[index], 0, 2);
                    int spritePixelLow = Util.getNthBits(spriteFullByte, 0, 2);
                    if (spritePixelLow != 0 && (bgPixelLow == 0 || priority == 0)) {
                        Color color = getColor(spriteFullByte);
                        pixels.setPixel(spriteX + x, spriteY + y, color);
                    }
                }
            }
        }
    }

    // MODIFIES: pixels
    // EFFECTS:  renders the OAM to the pixels using the given scaling.
    public void renderOAM(Pixels pixels, int scaleX, int scaleY) {
        int patternTableSelect = Util.getNthBit(ppuCtrl, 3);
        int offset = patternTableSelect * 0x0100;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int attribute = Util.getNthBits(primaryOam[i * 4 + 2], 0, 2) + 4;
                int address = primaryOam[i * 4 * 8 + j * 4 + 1];
                int[] low = getTileLow(offset + address);
                int[] high = getTileHigh(offset + address);
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        int formattedLow = Util.getNthBit(low[l], 7 - k);
                        int formattedHigh = Util.getNthBit(high[l], 7 - k);
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

        pixels.storeBuffer();
    }

    // REQUIRES: 0x0000 <= pointer <= 0x1FFF
    // EFFECTS:  returns the 8 low bits of the given tile at the pointer in memory.
    private int[] getTileLow(int pointer) {
        int offset = pointer << 4;

        int[] tileLow = new int[8];
        for (int i = 0; i < 8; i++) {
            tileLow[i - 0] = readMemory(offset + i);
        }
        return tileLow;
    }

    // REQUIRES: 0x0000 <= pointer <= 0x1FFF
    // EFFECTS:  returns the 8 high bits of the given tile at the pointer in memory.
    private int[] getTileHigh(int pointer) {
        int tile = pointer;
        int offset = pointer << 4;

        int[] tileHigh = new int[8];
        for (int i = 8; i < 16; i++) {
            tileHigh[i - 8] = readMemory(offset + i);
        }
        return tileHigh;
    }

    public void setPixels(Pixels pixels) {
        this.pixels = pixels;
    }

    // MODIFIES: primaryOam
    // EFFECTS: writes the value to the primaryOam at the address specified in oamAddr, and increments oamAddr
    public void writeOam(int value) {
        primaryOam[oamAddr] = value;
        oamAddr = (oamAddr + 1);
    }

    public void setRegisterT(int value) {
        registerT = value & 0x7FFF;
    }

    public void setRegisterV(int value) {
        registerV = value & 0x7FFF;
    }

    public void setRegisterX(int value) {
        registerX = value & 0x7;
    }

    public void setRegisterW(int value) {
        registerW = value & 0x1;
    }

    public int getRegisterT() {
        return registerT;
    }

    public int getRegisterV() {
        return registerV;
    }

    public int getRegisterX() {
        return registerX;
    }

    public int getRegisterW() {
        return registerW;
    }

    public int getLatchNametable() {
        return latchNametable;
    }

    public int getLatchAttributeTable() {
        return latchAttributeTable;
    }

    public int getLatchPatternTableLow() {
        return latchPatternTableLow;
    }

    public int getLatchPatternTableHigh() {
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

    public int getOamAddr() {
        return oamAddr;
    }

    public int getPpuDataBuffer() {
        return ppuDataBuffer;
    }

    public int[] getNametable() {
        return nametable;
    }

    public Mirroring getNametableMirroring() {
        return nametableMirroring;
    }

    public PaletteRamIndexes getPaletteRamIndexes() {
        return paletteRamIndexes;
    }

    public int[] getPrimaryOam() {
        return primaryOam;
    }

    public int[] getSecondaryOam() {
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

    public boolean getIsOddFrame() {
        return isOddFrame;
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

    public void setLatchNametable(int latchNametable) {
        this.latchNametable = latchNametable;
    }

    public void setLatchAttributeTable(int latchAttributeTable) {
        this.latchAttributeTable = latchAttributeTable;
    }

    public void setLatchPatternTableLow(int latchPatternTableLow) {
        this.latchPatternTableLow = latchPatternTableLow;
    }

    public void setLatchPatternTableHigh(int latchPatternTableHigh) {
        this.latchPatternTableHigh = latchPatternTableHigh;
    }

    public void setNametable(int i, int value) {
        nametable[i] = value;
    }

    public void setPrimaryOam(int i, int value) {
        primaryOam[i] = value;
    }

    public void setSecondaryOam(int i, int value) {
        secondaryOam[i] = value;
    }
}