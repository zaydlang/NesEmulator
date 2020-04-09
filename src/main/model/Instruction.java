package model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;

// Class Instruction:
//     Models an Instruction that the CPU can execute. Each instruction contains an Addressing Mode and an Opcode,
//     and they're executed in that order. The number of arguments and number of cycles can be determined from the
//     Addressing Mode and Opcode, but they're specified for simplicity.

public class Instruction {
    private static final String CONFIG_FILE = "./config/instructions.json";
    private static ArrayList<Instruction> instructions;

    private String opcode;
    private String mode;
    private int numArguments;
    private int numCycles;

    // EFFECTS: sets the opcode, mode, numArguments, and numCycles to their specified values.
    private Instruction(String opcode, String mode, int numArguments, int numCycles) {
        this.opcode       = opcode;
        this.mode         = mode;
        this.numArguments = numArguments;
        this.numCycles    = numCycles;
    }

    // EFFECTS: returns the opcode
    public String getOpcode() {
        return opcode;
    }

    // EFFECTS: returns the mode
    public String getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return opcode + " " + mode;
    }

    // EFFECTS: returns the number of bytes
    public int getNumArguments() {
        return numArguments;
    }

    // EFFECTS: returns the number of cycles
    public int getNumCycles() {
        return numCycles;
    }

    // EFFECTS: returns the list of instructions
    public static ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    // https://crunchify.com/how-to-read-json-object-from-file-in-java/
    static {
        instructions = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(CONFIG_FILE));
            JSONArray jsonArray = (JSONArray) obj;
            for (Object nextObject : jsonArray) {
                JSONObject jsonObject = (JSONObject) nextObject;
                instructions.add(new Instruction(
                        (String)                  jsonObject.get("opcode"),
                        (String)                  jsonObject.get("mode"),
                        Integer.parseInt((String) jsonObject.get("numArguments")),
                        Integer.parseInt((String) jsonObject.get("numCycles"))
                ));

                int x = 2;
            }
            int x = 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}