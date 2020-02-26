package ui;

import model.Address;
import model.Mode;
import model.NES;

import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings({"CodeBlock2Expr", "Convert2MethodRef"})
// Class Actions:
//     No, this is not a Class-Action lawsuit. Forgive my pun.
//     This class contains a series of commands that the user can execute on the NES. Allows for easy creation of
//     commands by the programmer. It maps a String-version of the command to a lambda that modifies the NES.

public class Actions extends HashMap<String, Actions.Command> {
    private static Actions actions;

    protected interface Command {
        void run(NES nes, String argument) throws IOException;
    }

    // MODIFIES: nes
    // EFFECTS: runs the specified command with the given argument on the NES.
    public static void runCommand(NES nes, String commandName, String argument) throws IOException {
        try {
            actions.get(commandName).run(nes, argument);
        } catch (NullPointerException e) {
            // Do nothing
        }
    }

    // MODIFIES: nes
    // REQUIRES: 0x0000 <= argument <= 0xFFFF
    // EFFECTS: adds the given breakpoint to the nes.
    private static Command addBreakpoint = (NES nes, String argument) -> {
        nes.addBreakpoint(new Address(Integer.parseInt(argument, 16), 0, 65536));
    };

    // REQUIRES: if argument contains a file path, then the path is valid. See NesWriter.java
    // EFFECTS: saves the NES' state to the file path specified by the argument.
    private static Command save = (NES nes, String argument) -> {
        nes.save(argument);
    };

    // REQUIRES: argument specifies a file path that exists on the NES.
    // MODIFIES: nes
    // EFFECTS: loads the NES' state from the given file.
    private static Command load = (NES nes, String argument) -> {
        nes.load(argument);
    };

    // EFFECTS: quits the program
    private static Command quit = (NES nes, String argument) -> {
        System.exit(1);
    };

    static {
        actions = new Actions();
        actions.put("break", addBreakpoint);
        actions.put("save",  save);
        actions.put("load",  load);
        actions.put("quit",  quit);
    }
}
