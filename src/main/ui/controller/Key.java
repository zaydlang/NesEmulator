package ui.controller;

public class Key {
    private int keyboardKey;
    private String controllerKey;

    public Key(int keyboardKey, String controllerKey) {
        this.keyboardKey   = keyboardKey;
        this.controllerKey = controllerKey;
    }

    public void setKeyboardKey(int keyboardKey) {
        this.keyboardKey = keyboardKey;
    }

    public int getKeyboardKey() {
        return keyboardKey;
    }

    public String getControllerKey() {
        return controllerKey;
    }
}
