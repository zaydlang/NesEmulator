package ui;

import model.NES;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class Display extends JFrame implements KeyListener {
    // Constants
    private static final String  CARTRIDGE_FOLDER     = "rom/";
    private static final String  CARTRIDGE            = "donkeykong";
    private static final String  CARTRIDGE_EXTENSION  = ".nes";
    public static final int      FPS                  = (int) ((341 * 262 - 0.5) * 4 * 60);

    // Fields
    private NES nes;
    private Timer refreshTimer;
    private Timer paintTimer;

    public Display(NES nes) throws IOException {
        this.nes = nes;
        nes.loadCartridge(CARTRIDGE_FOLDER + CARTRIDGE + CARTRIDGE_EXTENSION);

        init();
    }

    private void init() {
        setTitle(CARTRIDGE);
        setSize(Pixels.DISPLAY_WIDTH, Pixels.DISPLAY_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);

        add(nes.getPixels());
        pack();

        refreshTimer = new Timer(1000 / FPS, e -> {
            try {
                nes.cycle();
                //repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        refreshTimer.start();

        paintTimer = new Timer(1000, e -> {
            try {
                repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        paintTimer.start();

        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        NES nes = new NES();
        new Display(nes);
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }
}
