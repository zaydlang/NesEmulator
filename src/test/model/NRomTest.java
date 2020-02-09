package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("SimplifiableJUnitAssertion")
public class NRomTest {
    NRom nRom;

    @BeforeEach
    void runBefore() {
        nRom = new NRom();
    }

    @Test
    void testConstructor() {
        assertTrue(nRom.header.length  == NRom.HEADER_SIZE);
        assertTrue(nRom.trainer.length == NRom.TRAINER_SIZE);
        assertTrue(nRom.prgRam.length  == NRom.PRG_RAM_SIZE);
        assertTrue(nRom.prgRom.length  == NRom.PRG_ROM_128_SIZE + NRom.PRG_ROM_256_SIZE);
        assertTrue(nRom.chrRom.length  == NRom.CHR_ROM_SIZE);
    }

    // TODO: split loadCartridge into separate tests; remove hashcodes

    @Test
    void testLoadCartridgeTrainerPresent() {
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
    }

    @Test
    void testLoadCartridgeTrainerNotPresent() {
        nRom.loadCartridge("test/TestLoadRomTrainerNotPresent.nes");
        assertTrue(nRom.header.length  == 16);
        assertTrue(nRom.trainer.length == 512);
        assertTrue(nRom.prgRom.length  == 32768);
        assertTrue(nRom.chrRom.length  == 24576);
    }

    @Test
    void testReadFileNoOffset() {
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        assertTrue(nRom.header.length  == 16);
        assertTrue(nRom.trainer.length == 512);
        assertTrue(nRom.prgRom.length  == 32768);
        assertTrue(nRom.chrRom.length  == 24576);
    }

    @Test
    void testReadFileOffset() throws IOException {
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
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
        nRom.setPrgRam(Integer.parseInt("17", 16), 125);
        assertTrue(nRom.readMemory(Integer.parseInt("6017", 16)).getValue() == 125);
    }

    @Test
    void testReadMemoryPrgRom() {
        nRom.setPrgRom(Integer.parseInt("17", 16), 125);
        assertTrue(nRom.readMemory(Integer.parseInt("8017", 16)).getValue() == 125);
    }




    // ######### TESTS FOR WRITING MEMORY: PRG RAM ##########

    @Test
    void testWriteMemoryPrgRam() {
        int address = Integer.parseInt("6017", 16);
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        nRom.writeMemory(address, 125);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRamOverflow() {
        int address = Integer.parseInt("6017", 16);
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        nRom.writeMemory(address, 125 + 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRamUnderflow() {
        int address = Integer.parseInt("6017", 16);
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRamLowerBound() {
        int address = Integer.parseInt("6000", 16);
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRamUpperBound() {
        int address = Integer.parseInt("7FFF", 16);
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)).getValue() == 125);
    }




    // ######### TESTS FOR WRITING MEMORY: PRG ROM ##########

    @Test
    void testWriteMemoryPrgRom() {
        int address = Integer.parseInt("8017", 16);
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        nRom.writeMemory(address, 125);
        assertTrue(nRom.getPrgRom(address - Integer.parseInt("8000", 16)).getValue()== 125);
    }

    @Test
    void testWriteMemoryPrgRomUnderflow() {
        int address = Integer.parseInt("8017", 16);
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRom(address - Integer.parseInt("8000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRomOverflow() {
        int address = Integer.parseInt("8017", 16);
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        nRom.writeMemory(address, 125 + 256);
        assertTrue(nRom.getPrgRom(address - Integer.parseInt("8000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRomLowerBound() {
        int address = Integer.parseInt("8000", 16);
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        nRom.writeMemory(address, 125);
        assertTrue(nRom.getPrgRom(address - Integer.parseInt("8000", 16)).getValue() == 125);
    }

    @Test
    void testWriteMemoryPrgRomUpperBound() {
        int address = Integer.parseInt("FFFF", 16);
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        nRom.writeMemory(address, 125);
        assertTrue(nRom.getPrgRom(address - Integer.parseInt("8000", 16)).getValue() == 125);
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
}
