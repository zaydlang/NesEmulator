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
        Bus bus = Bus.getInstance();
        try {
            bus.loadCartridge(new File("./data/test/TestLoadRomTrainerNotPresentSmall.nes"));
            nrom = (NRom) bus.getMapper();
        } catch (IOException e) {
            fail("Bus failed to load cartridge!");
        }
    }

    @Test
    void testReadMemoryCpuOutOfBounds() {
        assertEquals(0, nrom.readMemoryCpu(0x5FFF));
    }

    @Test
    void testWriteMemoryCpuOutOfBounds() {
        try {
            nrom.writeMemory(0x5FFF, 0);
            fail("ArrayIndexOutOfBoundsException not thrown!");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Good!
        }

        try {
            nrom.writeMemory(0xFFFF, 0);
            fail("ArrayIndexOutOfBoundsException not thrown!");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Good!
        }
    }
}