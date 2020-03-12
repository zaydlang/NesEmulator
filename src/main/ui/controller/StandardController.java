package ui.controller;

import model.Address;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import static java.awt.event.KeyEvent.*;

public class StandardController extends Controller {
    private static Key[] initialKeyMap = new Key[] {
            new Key(VK_Z,     "A"),
            new Key(VK_X,     "B"),
            new Key(VK_SPACE, "Start"),
            new Key(VK_ENTER, "Select"),
            new Key(VK_UP,    "Up"),
            new Key(VK_DOWN,  "Down"),
            new Key(VK_LEFT,  "Left"),
            new Key(VK_RIGHT, "Right"),
    };

    private boolean isPolling;
    private int     pollingIndex;

    public StandardController() {
        super(initialKeyMap);

        isPolling    = false;
        pollingIndex = 0;
    }

    @Override
    public Address poll() {
        if (pollingIndex >= keyState.length) {
            return new Address(1);
        }

        Address result = new Address(keyState[pollingIndex] ? Integer.parseInt("0", 16) : Integer.parseInt("1", 16));
        pollingIndex++;
        return result;
    }

    @Override
    public void setPolling(boolean isPolling) {
        this.isPolling = isPolling;

        if (!isPolling) {
            pollingIndex = 0;
        }
    }
}
