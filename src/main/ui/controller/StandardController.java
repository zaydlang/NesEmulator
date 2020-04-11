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

    static {
        initialKeyMap = new Key[8]; // 8 bits in a byte
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(new FileReader(Controller.CONFIG_FILE));
            JSONObject keyboardKeyEvents = (JSONObject) json.get("keys");
            JSONObject controllerKeyMaps = (JSONObject) json.get("controllers");
            JSONArray  standardKeyMap    = (JSONArray) controllerKeyMaps.get("standard");

            for (int i = 0; i < 8; i++) {
                JSONArray key = (JSONArray) standardKeyMap.get(i);
                int keyboardKey = Math.toIntExact((long) keyboardKeyEvents.get(key.get(0)));
                String controllerKey = (String) key.get(1);
                initialKeyMap[i] = new Key(keyboardKey, controllerKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
