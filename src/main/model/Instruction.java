package model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.lang.reflect.Field;

// Class Instruction:
//     Models an Instruction that the CPU can execute. Each instruction contains an Addressing Mode and an Opcode,
//     and they're executed in that order. The number of arguments and number of cycles can be determined from the
//     Addressing Mode and Opcode, but they're specified for simplicity.

public class Instruction {
    private static final String CONFIG_FILE = "./config/instructions.json";
    private static ArrayList<Instruction> instructions;

    private int opcode;
    private int mode;
    private int numArguments;
    private int numCycles;

    // https://www.rgagnon.com/javadetails/java-0038.html
    @SuppressWarnings("rawtypes")
    public static Object getValueOf(String className, Object clazz, String lookingForValue)
            throws Exception {
        Field field = Class.forName(className).getField(lookingForValue);
        Class clazzType = field.getType();
        if (clazzType.toString().equals("double"))
            return field.getDouble(clazz);
        else if (clazzType.toString().equals("int"))
            return field.getInt(clazz);
        // else other type ...
        // and finally
        return field.get(clazz);
    }

    // EFFECTS: sets the opcode, mode, numArguments, and numCycles to their specified values.
    private Instruction(int opcode, int mode, int numArguments, int numCycles) {
        this.opcode       = opcode;
        this.mode         = mode;
        this.numArguments = numArguments;
        this.numCycles    = numCycles;
    }

    // EFFECTS: returns the opcode
    public int getOpcode() {
        return opcode;
    }

    // EFFECTS: returns the mode
    public int getMode() {
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
                        (Integer)                 getValueOf("model.Opcode", Opcode.class, (String) jsonObject.get("opcode")),
                        (Integer)                 getValueOf("model.Mode", Mode.class,   (String) jsonObject.get("mode")),
                        Integer.parseInt((String) jsonObject.get("numArguments")),
                        Integer.parseInt((String) jsonObject.get("numCycles"))
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}