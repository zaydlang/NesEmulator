package ui.window;

import model.Bus;
import ppu.ColorPalette;
import ppu.PPU;
import ui.Pixels;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class PatternTableViewer extends PixelWindow implements KeyListener {
    private static final int   FPS                   = 10;
    private static final int   NUMBER_OF_PALETTES    = 8;
    private static final int   CHOSEN_PALETTE_HEIGHT = 2;
    private static final Color CHOSEN_PALETTE_COLOR  = Color.GREEN;
    private static final Color BACKGROUND_COLOR      = Color.GRAY;

    private static final int   KEY_CHANGE_PALETTE    = KeyEvent.VK_SPACE;

    private int currentPalette;

    public PatternTableViewer(Bus bus) {
        super(bus, 2, 2, 16 * 8 * 2, 16 * 8 + 8 + CHOSEN_PALETTE_HEIGHT, "Pattern Table Viewer");

        currentPalette = 0;
        addKeyListener(this);

        pack();
        setVisible(true);
        postContructor(FPS);
    }

    @Override
    public void repaint() {
        bus.getPpu().renderPatternTables(getPixels(), currentPalette);
        renderPalette();
    }

    private void renderPalette() {
        Pixels pixels = getPixels();
        PPU ppu = bus.getPpu();

        // Draw Palette
        for (int i = 0; i < 4 * 8; i++) {
            Color color = ColorPalette.getColor(ppu.readMemory(0x3F00 + i));
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    pixels.setPixel(i * 8 + x, 128 + y, color);
                }
            }
        }

        // Indicate which one is chosen
        for (int x = 0; x < 32 * 8; x++) {
            for (int y = 0; y < CHOSEN_PALETTE_HEIGHT; y++) {
                boolean isCurrentPalette = (0 <= x - currentPalette * 32 && x - currentPalette * 32 <= 31);
                Color color = isCurrentPalette ? CHOSEN_PALETTE_COLOR : BACKGROUND_COLOR;
                pixels.setPixel(x, 128 + 8 + y, color);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KEY_CHANGE_PALETTE) {
            incrementPalette();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void incrementPalette() {
        currentPalette = (currentPalette + 1) % NUMBER_OF_PALETTES;
    }
}