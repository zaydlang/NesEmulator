package ui.controller;

import model.Address;
import model.Instruction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import static java.awt.event.KeyEvent.*;

public class StandardController extends Controller {
    private static Key[] initialKeyMap;

    private boolean isPolling;
    private int     pollingIndex;

    public StandardController() {
        super(ControllerConfig.getKeyMap("standard"));

        isPolling    = false;
        pollingIndex = 0;
    }

    @Override
    public Address poll() {
        if (pollingIndex >= keyState.length) {
            return new Address(1);
        }

        Address result = new Address(keyState[pollingIndex] ? 1 : 0);
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

    @Override
    public boolean getPolling() {
        return isPolling;
    }
}
