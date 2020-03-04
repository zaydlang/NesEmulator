package ui;

import model.NES;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.TimerTask;

public class Display extends Window implements KeyListener {
    // Constants
    private static final String  CARTRIDGE_FOLDER     = "rom/";
    private static final String  CARTRIDGE            = "donkeykong";
    private static final String  CARTRIDGE_EXTENSION  = ".nes";
    public static final int      FPS                  = 60;
    public static final double   CYCLES_PER_FRAME     = (341 * 262 - 0.5) * 4;

    // Fields
    private TimerTask cycleTask;
    private TimerTask paintTask;
    private Pixels pixels;

    // Menu Items
    JMenuItem viewPatternTables = new JMenuItem(new AbstractAction("Pattern Tables") {
        public void actionPerformed(ActionEvent e) {
            new PatternTableViewer(nes);
        }
    });

    JMenuItem viewNameTables    = new JMenuItem(new AbstractAction("Nametables") {
        public void actionPerformed(ActionEvent e) {
            new NameTableViewer(nes);
        }
    });

    public Display(NES nes) throws IOException {
        super(nes, 2, 2, 32 * 8, 30 * 8, 60, "NES Emulator");
        nes.loadCartridge(CARTRIDGE_FOLDER + CARTRIDGE + CARTRIDGE_EXTENSION);
        nes.setPixels(getPixels());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setupTasks();
        setupHeader();

        pack();
        setVisible(true);
    }

    private void setupTasks() {
        cycleTask = new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < CYCLES_PER_FRAME; i++) {
                    try {
                        nes.cycle();
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
        file.add(new JMenuItem("Open"));
        file.add(new JMenuItem("Save State"));
        file.add(new JMenuItem("Reload State"));

        JMenu view = new JMenu("View");
        view.add(new JMenuItem("CPU Instruction Viewer"));
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

    public static void main(String[] args) throws IOException {
        NES nes = new NES();
        new Display(nes);
    }

    @Override
    public void repaint() {

    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }
}
