package ui.window;

import model.Bus;
import ui.MaxedQueue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Queue;

public class BreakpointViewer extends PixelWindow {
    private static final String FONT_FILE    = "./data/resource/font/CONSOLA.ttf";
    private static final float  FONT_SIZE    = 12.0f;
    private static final int    MAX_LOG_SIZE = 30;
    private static final int    FPS          = 60;

    private static Font font;

    static {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_FILE));
            font = font.deriveFont(FONT_SIZE);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    JButton cycleButton = new JButton(new AbstractAction("Cycle") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!bus.getEnabled() && bus.getCartridgeLoaded()) {
                bus.cycleComponents();
            }
        }
    });

    JButton breakpointButton = new JButton(new AbstractAction("Add Breakpoint") {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                bus.getCpu().addBreakpoint(Integer.parseInt(breakpoint.getText(), 16));
                breakpoints.append("0x" + breakpoint.getText());
            } catch (Exception ex) {
                // Do nothing
                int x = 2;
            }
            breakpoint.setText("");
        }
    });

    private JTextArea    breakpoints;
    private JTextArea    breakpoint;
    private JScrollPane  scrollPane;

    private MaxedQueue<String> log;

    public BreakpointViewer(Bus bus) {
        super(bus, 1, 1, 400 + 50, 400 + 100, "CPU Viewer", true);

        breakpoints = new JTextArea(MAX_LOG_SIZE, 80);
        breakpoints.setFont(font);
        breakpoints.setEditable(false);
        scrollPane = new JScrollPane(breakpoints);
        scrollPane.setBounds(0, 0, 400, 400);
        add(scrollPane,  BorderLayout.NORTH);

        breakpoint = new JTextArea(MAX_LOG_SIZE, 80);
        breakpoint.setFont(font);
        add(breakpoint, BorderLayout.CENTER);

        add(breakpointButton, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(800, 600));
        log = new MaxedQueue<>(MAX_LOG_SIZE);

        pack();
        setVisible(true);
        postContructor(FPS);
    }

    @Override
    public void repaint() {
        super.repaint();
    }
}
