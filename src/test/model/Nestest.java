package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.controller.Controller;
import ui.controller.StandardController;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("SimplifiableJUnitAssertion")
public class Nestest {
    Bus bus;
    CPU cpu;

    ArrayList<String> pc;

    @BeforeEach
    void runBefore() {
        try {
            bus = Bus.getInstance();
            cpu = bus.getCpu();
            bus.loadCartridge(new File("data/rom/nestest.nes"));
        } catch (IOException e) {
            fail();
        }

        pc = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File("data/test/nestest_pc.log")))) {
            String line;
            while ((line = br.readLine()) != null) {
                pc.add(line);
            }
        } catch (FileNotFoundException e) {
            fail();
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testNestestRom() {
        cpu.setRegisterPC(0xC000);

        for (int i = 0; i < 5259; i++) {
            assertEquals(cpu.getRegisterPC(), Integer.parseInt(pc.get(i),  16));
            cpu.processInstruction();
        }
    }/*

    @Test
    void testNestestRomSpeed() {
        long totalTime = 0;
        int test = 0;

        for (int i = 0; i < 100; i++) {
            cpu.setRegisterPC(0xC000);

            long startTime = System.nanoTime();
            for (int j = 0; j < 5259; j++) {
                while (cpu.cyclesRemaining > 1)
                    cpu.cycle();
                    test++;
            }

            long endTime = System.nanoTime();

            totalTime += endTime - startTime;
        }

        System.out.println("Expected Average Time: " + (float) 525900 / (float) 893415);
        System.out.println("Actual   Average Time: " + totalTime / 100);
    }*/
}
