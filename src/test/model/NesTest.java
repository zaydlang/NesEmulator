package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class NesTest {
    NES nes;

    @BeforeEach
    void runBefore() {
        try {
            nes = new NES();
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testConstructor() {
        try {
            nes.getLogFile().write(""); // Check that the file is open.
        } catch (Exception e) {
            fail();
        } finally {
            new File(nes.getFilePath()).delete();
        }
    }

    @Test
    void testLoadCartridge() {
        try {
            nes.loadCartridge("test/TestLoadRomTrainerNotPresentSmall.nes");
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testCycle() {
        try {
            nes.loadCartridge("test/TestLoadRomTrainerNotPresentSmall.nes");
            String output = nes.cycle();
            assertTrue(output.equals("NOP :          A: 00 X: 00 Y: 00 PC: C001 S: FD "));
        } catch (IOException e) {
            fail();
        } finally {
            new File(nes.getFilePath()).delete();
        }
    }

    @Test
    void testClose() {
        try {
            nes.loadCartridge("test/TestLoadRomTrainerNotPresentSmall.nes");
            String output = nes.cycle();
            nes.close();
        } catch (IOException e) {
            fail();
        } finally {
            new File(nes.getFilePath()).delete();
        }
    }
}
