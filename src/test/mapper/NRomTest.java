package mapper;

import mapper.NRom;
import model.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ppu.Mirroring;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/*
@SuppressWarnings("SimplifiableJUnitAssertion")
public class NRomTest {
    NRom nRom;

    @BeforeEach
    void runBefore() {
        nRom = new NRom(Mirroring.VERTICAL);
    }

    @Test
    void testConstructor() {
        assertTrue(nRom.header.length  == 0);
        assertTrue(nRom.trainer.length == 0);
        assertTrue(nRom.prgRom.length  == 0);
        assertTrue(nRom.chrRom.length  == 0);

        assertTrue(nRom.prgRam.length  == NRom.PRG_RAM_SIZE);
    }

    @Test
    void testLoadCartridgeTrainerPresent() {
        loadCartridge("test/TestLoadRomTrainerPresent.nes");
        assertTrue(nRom.header.length  == 16);
        assertTrue(nRom.trainer.length == 512);
        assertTrue(nRom.prgRom.length  == 32768);
        assertTrue(nRom.chrRom.length  == 24576);
    }

    @Test
    void testLoadCartridgeTrainerNotPresent() {
        loadCartridge("test/TestLoadRomTrainerNotPresent.nes");

        assertTrue(nRom.header.length  == 16);
        assertTrue(nRom.trainer.length == 0);
        assertTrue(nRom.prgRom.length  == 32768);
        assertTrue(nRom.chrRom.length  == 24576);
    }

    @Test
    void testLoadCartridgeTrainerPresentSmall() {
        loadCartridge("test/TestLoadRomTrainerPresentSmall.nes");
        assertTrue(nRom.header.length  == 16);
        assertTrue(nRom.trainer.length == 512);
        assertTrue(nRom.prgRom.length  == 32768);
        assertTrue(nRom.chrRom.length  == 8192);
    }

    @Test
    void testLoadCartridgeTrainerNotPresentSmall() {
        loadCartridge("test/TestLoadRomTrainerNotPresentSmall.nes");
        assertTrue(nRom.header.length  == 16);
        assertTrue(nRom.trainer.length == 0);
        assertTrue(nRom.prgRom.length  == 32768);
        assertTrue(nRom.chrRom.length  == 8192);
    }

    @Test
    void testReadNonexistentFile() {
        try {
            nRom.loadCartridge("test/ThisFileDoesNotExist.nes");
        } catch (IOException e) {
            return;
        }

        fail();
    }

    @Test
    void testReadFileNoOffset() {
        loadCartridge("test/TestLoadRomTrainerPresent.nes");
        assertTrue(nRom.header.length  == 16);
        assertTrue(nRom.trainer.length == 512);
        assertTrue(nRom.prgRom.length  == 32768);
        assertTrue(nRom.chrRom.length  == 24576);
    }

    @Test
    void testReadFileOffset() throws IOException {
        loadCartridge("test/TestLoadRomTrainerPresent.nes");
        FileInputStream file = new FileInputStream("./data/test/TestReadFile.nes");
        Address[] result = nRom.readFile(file, 1,0, 3);
        assertTrue(nRom.header.length  == 16);
        assertTrue(nRom.trainer.length == 512);
        assertTrue(nRom.prgRom.length  == 32768);
        assertTrue(nRom.chrRom.length  == 24576);
    }




    // ######### TESTS FOR READING MEMORY ##########

    @Test
    void testReadMemoryPrgRam() {
        loadCartridge("test/TestLoadRomTrainerNotPresentSmall.nes");
        nRom.setPrgRam(Integer.parseInt("17", 16), 125);
        assertTrue(nRom.readMemory(Integer.parseInt("6017", 16)).getValue() == 125);
    }

    @Test
    void testReadMemoryPrgRom() {
        loadCartridge("test/TestLoadRomTrainerNotPresentSmall.nes");
        nRom.setPrgRom(Integer.parseInt("17", 16), 125);
        assertTrue(nRom.readMemory(Integer.parseInt("8017", 16)).getValue() == 125);
    }

    @Test
    void testReadMemoryBelowBounds() {
        loadCartridge("test/TestLoadRomTrainerNotPresentSmall.nes");
        try {
            nRom.readMemory(Integer.parseInt("5000", 16));
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }

        fail();
    }




    // ######### TESTS FOR WRITING MEMORY: PRG RAM ##########

    @Test
    void testWriteMemoryPrgRam() {
        loadCartridge("test/TestLoadRomTrainerPresent.nes");

        int address = Integer.parseInt("6017", 16);
        nRom.writeMemory(address, 125);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRamOverflow() {
        loadCartridge("test/TestLoadRomTrainerPresent.nes");

        int address = Integer.parseInt("6017", 16);
        nRom.writeMemory(address, 125 + 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRamUnderflow() {
        loadCartridge("test/TestLoadRomTrainerPresent.nes");

        int address = Integer.parseInt("6017", 16);
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRamLowerBound() {
        loadCartridge("test/TestLoadRomTrainerPresent.nes");

        int address = Integer.parseInt("6000", 16);
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRamUpperBound() {
        loadCartridge("test/TestLoadRomTrainerPresent.nes");

        int address = Integer.parseInt("7FFF", 16);
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)).getValue() == 125);
    }




    // ######### TESTS FOR WRITING MEMORY: ILLEGAL ACTIONS ##########

    @Test
    void testWriteMemoryPrgRom() {
        loadCartridge("test/TestLoadRomTrainerPresent.nes");

        int address = Integer.parseInt("8017", 16);
        try {
            nRom.writeMemory(address, 125);
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }

        fail();
    }

    @Test
    void testWriteMemoryFailure() {
        try {
            nRom.writeMemory(Integer.parseInt("5000", 16), 47);
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }

        fail();
    }

    void loadCartridge(String cartridgeName) {
        try {
            nRom.loadCartridge(cartridgeName);
        } catch (IOException e) {
            fail();
        }
    }
}
*/