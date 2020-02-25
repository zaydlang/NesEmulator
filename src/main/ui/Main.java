package ui;

import model.Address;
import model.NES;

import java.io.IOException;
import java.util.Scanner;

// Class Main:
//     Tentative class: Cycles the CPU and handles User Input.
public class Main {
    public static final String CARTRIDGE_NAME = "test/nestest.nes";

    public static void main(String[] args) throws IOException {
        NES nes = new NES();
        nes.loadCartridge(CARTRIDGE_NAME);

        Scanner scanner = new Scanner(System.in);
        String userInput = "";

        while (!userInput.equals("quit")) {
            System.out.print(nes.cycle() + " > ");

            userInput = scanner.nextLine().toLowerCase();

            try {
                String command = userInput.split(" ", 2)[0];
                String argument = userInput.split(" ", 2)[1];
                Actions.runCommand(nes, command, argument);
            } catch (ArrayIndexOutOfBoundsException e) {
                // Do nothing
            }
        }
    }
}
