package ui;

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

        while (!userInput.equals("quit") && nes.isEnabled()) {
            System.out.print(nes.cycle());
            userInput = scanner.nextLine().toLowerCase();
        }

        nes.close();
    }
}
