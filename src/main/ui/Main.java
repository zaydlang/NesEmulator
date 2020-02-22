package ui;

import model.Address;
import model.NES;

import java.io.File;
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
        nes.addBreakpoint(new Address(Integer.parseInt("C000", 16), 0, 65536));

        while (!userInput.equals("quit")) {
            nes.cycle();
            try {
                Address breakpoint = new Address(Integer.parseInt(userInput, 16), 0, 65536);
                nes.addBreakpoint(breakpoint);
            } catch (NumberFormatException e) {
                continue;
            }
        }
    }
}
