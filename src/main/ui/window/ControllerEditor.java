package ui.window;

import model.Bus;
import ui.Pixels;
import ui.controller.Controller;
import ui.controller.KeyAlreadyMappedException;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class ControllerEditor extends JFrame implements KeyListener {
    private static final int    FPS    = 10;
    private static final int    WIDTH  = 300;
    private static final int    HEIGHT = 200;
    private static final String TITLE  = "Controller Editor";

    private Controller    controller;
    private int           listeningKeyId; // The keyID we're currently listening for

    private Timer         timer;
    private TimerTask     paintTask;

    private ButtonPanel[] buttonPanels;

    public class ControllerEditButton extends JButton {
        public ControllerEditButton(int id) {
            super(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listeningKeyId = id;
                }
            });
        }
    }

    public class ButtonPanel extends JPanel {
        private JLabel               buttonLabel;
        private ControllerEditButton controllerEditButton;
        private int id;

        public ButtonPanel(String label, int id) {
            this.buttonLabel          = new JLabel(label);
            this.controllerEditButton = new ControllerEditButton(id);
            this.id                   = id;

            buttonLabel.setText(label);
            reloadButtonText();

            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            add(buttonLabel);
            add(Box.createHorizontalGlue());
            add(controllerEditButton);

            setPreferredSize(new Dimension(WIDTH, HEIGHT / 8));
        }

        public void reloadButtonText() {
            controllerEditButton.setText(controller.getMapping(id));
        }

        @Override
        public synchronized void addKeyListener(KeyListener l) {
            controllerEditButton.addKeyListener(l);
        }
    }

    public ControllerEditor(Bus bus) {
        this.controller = bus.getController();
        listeningKeyId  = -1; // Not listening for a key

        buttonPanels    = new ButtonPanel[8];
        for (int i = 0; i < 8; i++) {
            buttonPanels[i] = new ButtonPanel(controller.getControllerKey(i), i);
            buttonPanels[i].addKeyListener(this);
            add(buttonPanels[i]);
        }

        // https://stackoverflow.com/questions/17925609/how-to-add-padding-to-a-jpanel-with-a-border/17925693
        rootPane.setBorder(new CompoundBorder(rootPane.getBorder(), new EmptyBorder(0,10,0,0)));
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        setTitle(TITLE);
        addKeyListener(this);
        setupTasks();

        pack();
        setVisible(true);
    }

    private void setupTasks() {
        timer = new Timer();
        paintTask = new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        };
        timer.schedule(paintTask, 0, 1000 / FPS);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (listeningKeyId != -1) { // Not listening for a key
            try {
                controller.changeKey(listeningKeyId, e.getKeyCode());
                buttonPanels[listeningKeyId].reloadButtonText();
            } catch (KeyAlreadyMappedException ex) {
                // Alright, do nothing
            }
        }

        listeningKeyId = -1;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}