package ui.window;

import model.Bus;
import ui.MaxedQueue;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Queue;
import java.io.File;
import java.io.IOException;

public class CpuViewer extends PixelWindow implements CpuOutput {
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

        }
    });

    private JTextArea    textArea;
    private JTextArea    breakpoints;
    private JScrollPane  scrollPane;

    private MaxedQueue<String> log;

    public CpuViewer(Bus bus) {
        super(bus, 1, 1, 400 + 50, 400 + 100, "CPU Viewer");

        textArea = new JTextArea(MAX_LOG_SIZE, 80);
        textArea.setFont(font);
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        add(scrollPane,  BorderLayout.CENTER);

        cycleButton.setBorder(BorderFactory.createEmptyBorder());
        cycleButton.setPreferredSize(new Dimension(0, 60));
        add(cycleButton, BorderLayout.SOUTH);

        bus.getCpu().setLoggingOutput(this);
        log = new MaxedQueue<>(MAX_LOG_SIZE);

        pack();
        setVisible(true);
        postContructor(FPS);
    }

    @Override
    public void repaint() {
        StringBuilder text = new StringBuilder();
        Queue<String> queue = log.getQueue();

        for (String cpuLog : queue) {
            text.append(cpuLog);
        }

        textArea.setText(text.toString());
        textArea.setSelectionEnd(0);
        super.repaint();
    }

    @Override
    public void log(String cpuLog) {
        log.add(cpuLog + "\n");
        if (log.size() > MAX_LOG_SIZE) {
            log.remove();
        }
    }
}
