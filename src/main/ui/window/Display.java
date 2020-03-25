package ui.window;

import model.Bus;
import model.Opcode;
import persistence.BusReader;
import persistence.BusWriter;
import ui.controller.Controller;
import ui.controller.StandardController;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TimerTask;

public class Display extends PixelWindow implements KeyListener {
    // Constants
    private static final String    CARTRIDGE_FOLDER     = "data/rom/";
    private static final String    CARTRIDGE            = "nestest";
    private static final String    CARTRIDGE_EXTENSION  = ".nes";
    private static final int       FPS                  = 60;
    private static final double    CYCLES_PER_FRAME     = (341 * 262 - 0.5) * 4 / 12;

    private static final int       ICON_SIZE            = 20;
    private static final ImageIcon ICON_PAUSE;
    private static final ImageIcon ICON_PLAY;

    static {
        ICON_PAUSE = createIcon("./data/resource/icon/pause.png", ICON_SIZE, ICON_SIZE);
        ICON_PLAY  = createIcon("./data/resource/icon/play.png",  ICON_SIZE, ICON_SIZE);
    }

    // Fields
    private TimerTask  cycleTask;
    private Controller controller;

    private PatternTableViewer patternTableViewer;
    private NameTableViewer    nameTableViewer;
    private OamViewer          oamViewer;
    private CpuViewer          cpuViewer;
    private ControllerEditor   controllerEditor;

    // Menu Items
    JMenuItem fileOpenRom        = new JMenuItem(new AbstractAction("Open") {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/" + CARTRIDGE_FOLDER));
                fileChooser.showOpenDialog(null);
                bus.loadCartridge(fileChooser.getSelectedFile());
            } catch (IOException ex) {
                // Do nothing
            }
        }
    });

    JMenuItem fileSaveSate       = new JMenuItem(new AbstractAction("Save State") {
        @Override
        public void actionPerformed(ActionEvent e) {
            BusWriter.writeToFile(bus, "savestate");
        }
    });

    JMenuItem fileLoadState      = new JMenuItem(new AbstractAction("Load State") {
        @Override
        public void actionPerformed(ActionEvent e) {
            bus = BusReader.readFromFile("savestate");
            try {
                setupBus();
            } catch (IOException ex) {
                // Failed to load savestate!
            }
            bus.setEnabled(true);
        }
    });

    JMenuItem viewPatternTables  = new JMenuItem(new AbstractAction("Pattern Tables") {
        @Override
        public void actionPerformed(ActionEvent e) {
            patternTableViewer = new PatternTableViewer(bus);
        }
    });

    JMenuItem viewNameTables     = new JMenuItem(new AbstractAction("Nametables") {
        @Override
        public void actionPerformed(ActionEvent e) {
            nameTableViewer = new NameTableViewer(bus);
        }
    });

    JMenuItem viewOAM            = new JMenuItem(new AbstractAction("OAM") {
        @Override
        public void actionPerformed(ActionEvent e) {
            oamViewer = new OamViewer(bus);
        }
    });

    JMenuItem viewCpuViewer      = new JMenuItem(new AbstractAction("CPU Viewer") {
        @Override
        public void actionPerformed(ActionEvent e) {
            cpuViewer = new CpuViewer(bus);
        }
    });

    JMenuItem viewBreakpointViewer = new JMenuItem(new AbstractAction("Breakpoint Viewer") {
        @Override
        public void actionPerformed(ActionEvent e) {
            new BreakpointViewer(bus);
        }
    });

    JMenuItem settingsController = new JMenuItem(new AbstractAction("Controller Settings") {
        @Override
        public void actionPerformed(ActionEvent e) {
            controllerEditor = new ControllerEditor(bus);
        }
    });

    JButton   pauseButton        = new JButton(new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            bus.setEnabled(!bus.getEnabled());
            viewCpuViewer.setEnabled(!bus.getEnabled());
            viewBreakpointViewer.setEnabled(!bus.getEnabled());

            if (bus.getEnabled()) {
                pauseButton.setIcon(ICON_PAUSE);
            } else {
                pauseButton.setIcon(ICON_PLAY);
            }
        }
    });

    public Display(Bus bus) throws IOException {
        super(bus, 2, 2, 32 * 8, 30 * 8,  "NES Emulator");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        postContructor(FPS);
        setupTasks();
        setupHeader();
        setupBus();
        setFocusable(true);
        addKeyListener(this);

        pack();
        setVisible(true);
    }


    Instant previousSecond = Instant.now();
    int frames = 0;

    private void setupTasks() {
        cycleTask = new TimerTask() {
            @Override
            public void run() {
                frames++;
                if (ChronoUnit.MILLIS.between(previousSecond, Instant.now()) >= 1 * 1000) {
                    previousSecond = Instant.now();
                    //Opcode.print(frames / 1);
                    Opcode.advance();
                    System.out.println(frames);
                    frames = 0;
                }

                for (int i = 0; i < CYCLES_PER_FRAME; i++) {
                    bus.cycle();
                }
            }
        };

        schedule(cycleTask, FPS);
    }

    private void setupHeader() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.add(fileOpenRom);
        file.add(fileSaveSate);
        file.add(fileLoadState);

        JMenu view = new JMenu("View");
        viewCpuViewer.setEnabled(false);
        viewBreakpointViewer.setEnabled(false);
        view.add(viewCpuViewer);
        view.add(viewBreakpointViewer);
        view.add(viewPatternTables);
        view.add(viewNameTables);
        view.add(viewOAM);

        pauseButton.setIcon(ICON_PAUSE);
        pauseButton.setBorder(BorderFactory.createEmptyBorder());

        menuBar.add(file);
        menuBar.add(view);
        menuBar.add(pauseButton);
        getContentPane().add(BorderLayout.NORTH, menuBar);
    }

    private void setupBus() throws IOException {
        controller = new StandardController();

        //bus.loadCartridge(new File(CARTRIDGE_FOLDER + CARTRIDGE + CARTRIDGE_EXTENSION));
        bus.setController(controller);
        bus.getPpu().setPixels(getPixels());
    }

    // http://www.nullpointer.at/2011/08/21/java-code-snippets-howto-resize-an-imageicon/#comment-11870
    private static ImageIcon createIcon(String url, int width, int height) {
        Image image = new ImageIcon(url).getImage(); // transform it
        Image resized = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        return new ImageIcon(resized);
    }

    public static void main(String[] args) throws IOException {
        Bus bus = new Bus();
        //bus.loadCartridge(new File("./data/rom/donkeykong.nes"));
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
