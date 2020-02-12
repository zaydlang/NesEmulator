package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
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
            assertTrue(nes.isEnabled());
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
            assertEquals("NOP :          A: 00 X: 00 Y: 00 PC: C000 S: FD Cycle: 0 ", output);
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

    @Test
    void testEnableAll() {
        try {
            nes.addBreakpoint(new Address(Integer.parseInt("C000", 16), 0, 65536));
            nes.loadCartridge("test/TestLoadRomTrainerNotPresentSmall.nes");
            nes.cycle();
            nes.enable();
            assertTrue(nes.isEnabled());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testIsEnabledCPUDisabled() {
        try {
            nes.addBreakpoint(new Address(Integer.parseInt("C000", 16), 0, 65536));
            nes.loadCartridge("test/TestLoadRomTrainerNotPresentSmall.nes");
            nes.cycle();
            assertFalse(nes.isEnabled());
        } catch (IOException e) {
            fail();
        }
    }
}
