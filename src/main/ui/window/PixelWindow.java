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

    // when this is on, the pixels class will auto-repaint itself.
    // if not, it must be repainted manually.
    private final boolean autoRefresh;

    public PixelWindow(Bus bus, int pixelWidth, int pixelHeight, int pixelsPerRow, int pixelsPerCol, String name, final boolean autoRefresh) {
        this.bus = bus;
        this.autoRefresh = autoRefresh;
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
                if (autoRefresh) // autorefresh is final, so this should be optimized by the compiler
                    getPixels().repaint();
            }
        };

        schedule(paintTask, fps);
    }

    protected void schedule(TimerTask task, int frequency) {
        timer.schedule(task, 0, 1000 / frequency);
    }

    public Pixels getPixels() {
        return pixels;
    }
}
