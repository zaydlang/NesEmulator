package ui;

import model.NES;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Window extends JFrame {
    protected NES nes;
    private Pixels pixels;

    protected TimerTask paintTask;
    private Timer timer;

    public Window(NES nes, int pixelWidth, int pixelHeight, int pixelsPerRow, int pixelsPerCol, int fps, String name) {
        this.nes = nes;
        pixels = new Pixels(pixelWidth, pixelHeight, pixelsPerRow, pixelsPerCol);
        add(pixels);

        setSize(new Dimension(pixels.getDisplayWidth(), pixels.getDisplayHeight()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(name);

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
