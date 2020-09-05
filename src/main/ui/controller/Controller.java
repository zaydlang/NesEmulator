package ui.controller;

import java.awt.event.KeyEvent;

public abstract class Controller {
    protected static String CONFIG_FILE = "./config/controllers.json";

    protected Key[]     keyMap;
    protected boolean[] keyState;

    public Controller(Key[] keyMap) {
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
            if (keyMap[i].getKeyboardKey() == keyEvent.getKeyCode()) {
                return i;
            }
        }

        throw new KeyNotFoundException();
    }

    public void changeKey(int id, int newKey) throws KeyAlreadyMappedException {
        for (Key key : keyMap) {
            if (key.getKeyboardKey() == newKey) {
                throw new KeyAlreadyMappedException();
            }
        }

        keyMap[id].setKeyboardKey(newKey);
    }

    public String getMapping(int keyId) {
        return KeyEvent.getKeyText(keyMap[keyId].getKeyboardKey());
    }

    public String getControllerKey(int keyId) {
        return keyMap[keyId].getControllerKey();
    }

    public abstract int poll();

    public abstract void setPolling(boolean isPolling);

    public abstract boolean getPolling();
}
