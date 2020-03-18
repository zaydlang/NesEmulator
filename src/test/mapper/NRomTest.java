package mapper;

import model.Address;
import model.Bus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class NRomTest {
    NRom nrom;

    @BeforeEach
    void runBefore() {
        Bus bus = new Bus();
        try {
            bus.loadCartridge(new File("./data/test/TestLoadRomTrainerNotPresentSmall.nes"));
            nrom = (NRom) bus.getMapper();
        } catch (IOException e) {
            fail("Bus failed to load cartridge!");
        }
    }

    @Test
    void testReadMemoryCpuOutOfBounds() {
        assertEquals(0, nrom.readMemoryCpu(Integer.parseInt("5FFF", 16)).getValue());
    }

    @Test
    void testWriteMemoryCpuOutOfBounds() {
        try {
            nrom.writeMemory(Integer.parseInt("5FFF", 16), 0);
            fail("ArrayIndexOutOfBoundsException not thrown!");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Good!
        }

        try {
            nrom.writeMemory(Integer.parseInt("FFFF", 16), 0);
            fail("ArrayIndexOutOfBoundsException not thrown!");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Good!
        }
    }
}