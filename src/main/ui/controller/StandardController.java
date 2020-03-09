package ui.controller;

import model.Address;

import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;

public class StandardController extends Controller {
    private static final int[] keyMap = new int[] {
            VK_Z,        // A
            VK_X,        // B
            VK_SPACE,    // Select
            VK_ENTER,    // Start
            VK_UP,       // Up
            VK_DOWN,     // Down
            VK_LEFT,     // Left
            VK_RIGHT     // Right
    };

    private boolean isPolling;
    private int     pollingIndex;

    public StandardController() {
        super(keyMap);

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
