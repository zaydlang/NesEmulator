package ui;

import model.CPU;
import model.NES;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static final String CARTRIDGE_NAME = "test/nestest.nes";

    public static void main(String[] args) throws IOException {
        NES nes = new NES();
        nes.loadCartridge(CARTRIDGE_NAME);

        Scanner scanner = new Scanner(System.in);
        String userInput = "";

        while (!userInput.equals("quit")) {
            System.out.println(nes.cycle());
            //userInput = scanner.nextLine().toLowerCase();
        }

        nes.close();
    }
}
