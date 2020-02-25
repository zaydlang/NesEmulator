package ui;

import model.Address;
import model.Mode;
import model.NES;

import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings({"CodeBlock2Expr", "Convert2MethodRef"})
public class Actions extends HashMap<String, Actions.Command> {
    private static Actions actions;

    protected interface Command {
        void run(NES nes, String argument) throws IOException;
    }

    public static void runCommand(NES nes, String commandName, String argument) throws IOException {
        try {
            actions.get(commandName).run(nes, argument);
        } catch (NullPointerException e) {
            // Do nothing
        }
    }

    private static Command addBreakpoint = (NES nes, String argument) -> {
        nes.addBreakpoint(new Address(Integer.parseInt(argument, 16), 0, 65536));
    };

    private static Command save = (NES nes, String argument) -> {
        nes.save(argument);
    };

    private static Command load = (NES nes, String argument) -> {
        nes.load(argument);
    };

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
