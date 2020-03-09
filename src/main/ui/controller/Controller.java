package ui.controller;

import model.Address;

import java.awt.event.KeyEvent;

public abstract class Controller {
    private   int[]     keyMap; // 1 if key is held, 0 if not.
    protected boolean[] keyState;

    public Controller(int[] keyMap) {
        this.keyMap  = keyMap;
        keyState     = new boolean[8];

        clearState();
    }

    public void setState(KeyEvent keyEvent, boolean value) {
        try {
            int index = findKeyIndex(keyEvent);
            keyState[index] = value;
        } catch (KeyNotFoundException e) {
            // Do nothing
        }
    }

    public void clearState() {
        for (int i = 0; i < 8; i++) {
            keyState[i] = false;
        }
    }

    private int findKeyIndex(KeyEvent keyEvent) throws KeyNotFoundException {
        for (int i = 0; i < 8; i++) {
            if (keyMap[i] == keyEvent.getKeyCode()) {
                return i;
            }
        }

        throw new KeyNotFoundException();
    }

    public abstract Address poll();

    public abstract void setPolling(boolean isPolling);
}
