package ui;

import javax.swing.*;
import java.awt.*;
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
    private static final int NUM_ROWS        = 30;
    private static final int NUM_COLS        = 32;
    private static final int PIXELS_PER_ROW  = 8;
    private static final int PIXELS_PER_COL  = 8;
    private static final int PIXEL_WIDTH     = 4;
    private static final int PIXEL_HEIGHT    = 4;

    private static final Color DEFAULT_COLOR = new Color(0,0,0);

    // Calculated Constants
    public static final int DISPLAY_WIDTH    = NUM_COLS * PIXELS_PER_COL * PIXEL_WIDTH;
    public static final int DISPLAY_HEIGHT   = NUM_ROWS * PIXELS_PER_ROW * PIXEL_HEIGHT;

    // Fields
    private Color[][] pixels;
    private ArrayList<Point> updatedPixels;

    public Pixels() {
        setPreferredSize(new Dimension(DISPLAY_WIDTH, DISPLAY_HEIGHT));

        pixels = new Color[NUM_ROWS * PIXELS_PER_ROW][NUM_COLS * PIXELS_PER_COL];
        for (Color[] row : pixels) {
            for (Color color : row) {
                color = new Color(DEFAULT_COLOR.getRGB()); // Creates a copy of DEFAULT_COLOR
            }
        }

        updatedPixels = new ArrayList<Point>();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int col = 0; col < NUM_COLS * PIXELS_PER_COL; col++) {
            for (int row = 0; row < NUM_ROWS * PIXELS_PER_ROW; row++) {
                Color state = pixels[row][col];
                int x = col * PIXEL_HEIGHT;
                int y = row * PIXEL_WIDTH;

                g.setColor(state);
                g.fillRect(x, y, PIXEL_WIDTH, PIXEL_HEIGHT);
            }
        }
        /*
        while (updatedPixels.size() != 0) {
            Point p = updatedPixels.remove(0);
            int row = p.row;
            int col = p.col;

            Color state = pixels[row][col];
            int x = col * PIXEL_HEIGHT;
            int y = row * PIXEL_WIDTH;

            g.setColor(state);
            g.fillRect(x, y, PIXEL_WIDTH, PIXEL_HEIGHT);
        }*/
    }

    public void setPixel(int row, int col, Color color) {
        pixels[row][col] = color;
        //updatedPixels.add(new Point(row, col));
    }
}
