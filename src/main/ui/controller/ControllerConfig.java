package ui.controller;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class ControllerConfig {
    private static final String CONFIG_FILE = "./config/controllers.json";

    private static ControllerConfig controllerConfig; // Singleton

    // The key in the map controllers is the type of controller (i.e. "standard")
    // This leads to a list of List<String> with size 8, and each List<String> has size 2.
    // The List<String>'s first element is a keyboard key, and the second element is its name
    // The List<List<String>> orders these keys in the order they're supposed to be sent to the CPU.
    private Map<String, List<List<String>>> controllers;

    // Keys maps String (which is an human-readable KeyEvent) to its KeyEvent integer value
    private Map<String, Integer> keys;

    private ControllerConfig() {
        // Constructor shouldn't be able to be called; this class should be created using GSON
    }

    static {
        try {
            controllerConfig = new Gson().fromJson(new FileReader(CONFIG_FILE), ControllerConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Returns the key map of the given controller
    public static Key[] getKeyMap(String controller) {
        Key[] keyMap = new Key[8];

        List<List<String>> rawKeyMap = controllerConfig.controllers.get(controller);
        for (int i = 0; i < keyMap.length; i++) {
            List<String> key = rawKeyMap.get(i);
            String keyboardKey   = key.get(0);
            String controllerKey = key.get(1);

            int vkKey = controllerConfig.keys.get(keyboardKey);
            keyMap[i] = new Key(vkKey, controllerKey);
        }

        return keyMap;
    }
}
