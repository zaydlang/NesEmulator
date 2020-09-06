package ui;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Pixels extends JPanel {
    // Classes
    private class Point {
        protected int row;
        protected int col;

        public Point(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    // Constants
    private static final Color DEFAULT_COLOR = new Color(0,0,0);

    // Fields
    private int pixelWidth;
    private int pixelHeight;
    private int pixelsPerRow;
    private int pixelsPerCol;

    private Color[][] pixels;       // this array is the one that is actually repainted.
    private Color[][] pixelsBuffer; // can be freely edited without fear of repainting mid-frame. can be copied over
                                    // to pixels[][] using storeBuffer()

    public Pixels(int pixelWidth, int pixelHeight, int pixelsPerRow, int pixelsPerCol) {
        this.pixelWidth   = pixelWidth;
        this.pixelHeight  = pixelHeight;
        this.pixelsPerRow = pixelsPerRow;
        this.pixelsPerCol = pixelsPerCol;

        setPreferredSize(new Dimension(getDisplayWidth(), getDisplayHeight()));

        pixels       = new Color[pixelsPerRow][pixelsPerCol];
        pixelsBuffer = new Color[pixelsPerRow][pixelsPerCol];
        for (int i = 0; i < pixelsPerRow; i++) {
            for (int j = 0; j < pixelsPerCol; j++) {
                pixels      [i][j] = new Color(DEFAULT_COLOR.getRGB()); // Creates a copy of DEFAULT_COLOR
                pixelsBuffer[i][j] = new Color(DEFAULT_COLOR.getRGB());
            }
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int col = 0; col < pixelsPerRow; col++) {
            for (int row = 0; row < pixelsPerCol; row++) {
                Color state = pixels[col][row];
                int x = col * pixelHeight;
                int y = row * pixelWidth;

                g.setColor(state);
                g.fillRect(x, y, pixelWidth, pixelHeight);
            }
        }

        g.fillRect(0, 0, pixelWidth, pixelHeight);
    }

    public void repaint() {
        int x = 2;
    }

    public void setPixel(int x, int y, Color color) {
        if (x >= 240 || y >= 256) return;
        pixelsBuffer[x][y] = color;
    }

    public void storeBuffer() {
        for (int col = 0; col < pixelsPerRow; col++) {
            for (int row = 0; row < pixelsPerCol; row++) {
                pixels[col][row] = pixelsBuffer[col][row];
            }
        }
    }

    public Color getPixel(int i, int j) {
        return pixels[i][j];
    }

    public int getDisplayWidth() {
        return pixelWidth  * pixelsPerRow;
    }

    public int getDisplayHeight() {
        return pixelHeight * pixelsPerCol;
    }
}
