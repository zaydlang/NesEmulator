package persistence;

import model.Address;
import model.CPU;
import model.NES;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class NesWriterTest {
    @Test
    void testConstructor() {
        try {
            NesWriter nesWriter = new NesWriter();
        } catch (Exception e) {
            fail("This is a dummy constructor; if this failed, you've got bigger problems.");
        }
    }

    @Test
    void testIOException() {
        try {
            NES nes = new NES();
            nes.save("this/file/does/not.exist");
            fail();
        } catch (IOException e) {
            // Expected
        }
    }

    @Test
    void testWriteToFile() {
        try {
            NES nes1 = new NES();
            nes1.loadCartridge("test/nestest.nes");
            nes1.addBreakpoint(new Address(Integer.parseInt("C5F5", 16), 0, 65536));
            nes1.cycle();
            nes1.save("writeTest");

            NES nes2 = new NES();
            nes2.load("writeTest");
            checkForEqualityCPURegisters(nes1.getCPU(), nes2.getCPU());
        } catch (IOException e) {
            fail("IOException thrown!");
        } finally {
            //noinspection ResultOfMethodCallIgnored
            new File("./data/save/writeTest.sav").delete();
        }
    }

    void checkForEqualityCPURegisters(CPU cpu1, CPU cpu2) {
        assertEquals(cpu1.getRegisterA().getValue(),    cpu2.getRegisterA().getValue());
        assertEquals(cpu1.getRegisterX().getValue(),    cpu2.getRegisterX().getValue());
        assertEquals(cpu1.getRegisterY().getValue(),    cpu2.getRegisterY().getValue());
        assertEquals(cpu1.getRegisterPC().getValue(),   cpu2.getRegisterPC().getValue());
        assertEquals(cpu1.getRegisterS().getValue(),    cpu2.getRegisterS().getValue());
        assertEquals(cpu1.getStatus(),                  cpu2.getStatus());

        for (int i = 0; i < Integer.parseInt("0800", 16); i++) {
            assertEquals(cpu1.readMemory(i).getValue(), cpu2.readMemory(i).getValue());
        }
    }
}
