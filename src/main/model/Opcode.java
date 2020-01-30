package model;

import java.util.HashMap;

public class Opcode extends HashMap<String, Opcode.OpcodeAction> {
    public interface OpcodeAction {
        void run(int argument, CPU cpu);
    }

    public static void runOpcode(String opcode, int argument, CPU cpu) {

    }
}
