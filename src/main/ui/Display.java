package ui;

import model.Bus;
import ui.controller.Controller;
import ui.controller.StandardController;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.TimerTask;

public class Display extends Window implements KeyListener {
    // Constants
    private static final String  CARTRIDGE_FOLDER     = "data/rom/";
    private static final String  CARTRIDGE            = "nestest";
    private static final String  CARTRIDGE_EXTENSION  = ".nes";
    public static final int      FPS                  = 60;
    public static final double   CYCLES_PER_FRAME     = (341 * 262 - 0.5) * 4;

    // Fields
    private TimerTask  cycleTask;
    private Controller controller;

    // Menu Items
    JMenuItem fileOpenRom       = new JMenuItem(new AbstractAction("Open") {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                bus.loadCartridge(new JFileChooser().getSelectedFile());
            } catch (IOException ex) {
                // Do nothing
            }
        }
    });

    JMenuItem viewPatternTables = new JMenuItem(new AbstractAction("Pattern Tables") {
        @Override
        public void actionPerformed(ActionEvent e) {
            new PatternTableViewer(bus);
        }
    });

    JMenuItem viewNameTables    = new JMenuItem(new AbstractAction("Nametables") {
        @Override
        public void actionPerformed(ActionEvent e) {
            new NameTableViewer(bus);
        }
    });

    JMenuItem viewCPUViewer     = new JMenuItem(new AbstractAction("CPU Viewer") {
        @Override
        public void actionPerformed(ActionEvent e) {
            //new CpuViewer(bus);
        }
    });

    public Display(Bus bus) throws IOException {
        super(bus, 2, 2, 32 * 8, 30 * 8,  "NES Emulator");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);

        postContructor(FPS);
        setupTasks();
        setupHeader();
        setupBus();

        pack();
        setVisible(true);
    }

    private void setupTasks() {
        cycleTask = new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < CYCLES_PER_FRAME; i++) {
                    try {
                        bus.cycle();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        schedule(cycleTask, FPS);
    }

    private void setupHeader() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.add(fileOpenRom);
        file.add(new JMenuItem("Save State"));
        file.add(new JMenuItem("Reload State"));

        JMenu view = new JMenu("View");
        view.add(viewCPUViewer);
        view.add(viewPatternTables);
        view.add(viewNameTables);

        JMenu sett = new JMenu("Settings");
        sett.add(new JMenuItem("Controller Settings"));
        sett.add(new JMenuItem("Mapper Settings"));

        menuBar.add(file);
        menuBar.add(view);
        menuBar.add(sett);
        getContentPane().add(BorderLayout.NORTH, menuBar);
    }

    private void setupBus() throws IOException {
        controller = new StandardController();

        bus.loadCartridge(new File(CARTRIDGE_FOLDER + CARTRIDGE + CARTRIDGE_EXTENSION));
        bus.setController(controller);
        bus.getPpu().setPixels(getPixels());
    }

    public static void main(String[] args) throws IOException {
        Bus bus = new Bus();
        new Display(bus);
    }

    @Override
    public void repaint() {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        controller.setState(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        controller.setState(e, false);
    }
}
