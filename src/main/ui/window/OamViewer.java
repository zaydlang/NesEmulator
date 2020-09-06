package ui.window;

import model.Bus;


public class OamViewer extends PixelWindow {
    private static final int FPS    = 10;
    private static final int scaleX = 3;
    private static final int scaleY = 3;

    public OamViewer(Bus bus) {
        super(bus, 2, 2, 8 * 8 * scaleX, 8 * 8 * scaleY, "OAM Viewer");

        pack();
        setVisible(true);
        postContructor(FPS);
    }

    @Override
    public void repaint() {
        bus.getPpu().renderOAM(getPixels(), scaleX, scaleY);
    }
}