package ui.window;

import model.Bus;
import ui.Pixels;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public abstract class PixelWindow extends JFrame {
    protected Bus bus;
    private Pixels pixels;

    protected TimerTask paintTask;
    private Timer timer;

    public PixelWindow(Bus bus, int pixelWidth, int pixelHeight, int pixelsPerRow, int pixelsPerCol, String name) {
        this.bus = bus;
        pixels = new Pixels(pixelWidth, pixelHeight, pixelsPerRow, pixelsPerCol);
        add(pixels);

        setSize(new Dimension(pixels.getDisplayWidth(), pixels.getDisplayHeight()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(name);
    }

    protected void postContructor(int fps) {
        timer = new Timer();
        paintTask = new TimerTask() {
            @Override
            public void run() {
                repaint();
                getPixels().repaint();
            }
        };
        schedule(paintTask, fps);
    }

    public void repaint() {
    }

    protected void schedule(TimerTask task, int frequency) {
        timer.schedule(task, 0, 1000 / frequency);
    }

    public Pixels getPixels() {
        return pixels;
    }
}
