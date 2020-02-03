package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SimplifiableJUnitAssertion")
public class NRomTest {
    NRom nRom;

    @BeforeEach
    void runBefore() {
        nRom = new NRom();
    }

    @Test
    void testConstructor() {
        for (int i = Integer.parseInt("6000", 16); i < Integer.parseInt("FFFF", 16); i++) {
            assertTrue(nRom.readMemory(i) == 0);
        }
    }

    @Test
    void testLoadCartridgeTrainerPresent() {
        nRom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        assertTrue(Arrays.hashCode(nRom.header)  == 1454197404);
        assertTrue(Arrays.hashCode(nRom.trainer) == -791873883);
        assertTrue(Arrays.hashCode(nRom.prgRom)  == 1512190940);
        assertTrue(Arrays.hashCode(nRom.chrRom)  == -245463348);
    }

    @Test
    void testLoadCartridgeTrainerNotPresent() {
        nRom.loadCartridge("test/TestLoadRomTrainerNotPresent.nes");
        assertTrue(Arrays.hashCode(nRom.header)  == -2054715872);
        assertTrue(Arrays.hashCode(nRom.trainer) == 1425784833);
        assertTrue(Arrays.hashCode(nRom.prgRom)  == 1512190940);
        assertTrue(Arrays.hashCode(nRom.chrRom)  == -245463348);
    }

    @Test
    void testReadFileNoOffset() throws IOException {
        FileInputStream file = new FileInputStream("./data/test/TestReadFile.nes");
        int[] result = nRom.readFile(file, 0, 4);
        assertTrue(Arrays.hashCode(result) == 1724931);
    }

    @Test
    void testReadFileOffset() throws IOException {
        FileInputStream file = new FileInputStream("./data/test/TestReadFile.nes");
        int[] result = nRom.readFile(file, 1, 3);
        assertTrue(Arrays.hashCode(result) == 55642);
    }




    // ######### TESTS FOR READING MEMORY ##########

    @Test
    void testReadMemoryPrgRam() {
        nRom.setPrgRam(Integer.parseInt("17", 16), 125);
        assertTrue(nRom.readMemory(Integer.parseInt("6017", 16)) == 125);
    }

    @Test
    void testReadMemoryPrgRom() {
        nRom.setPrgRom(Integer.parseInt("17", 16), 125);
        assertTrue(nRom.readMemory(Integer.parseInt("8017", 16)) == 125);
    }




    // ######### TESTS FOR WRITING MEMORY: PRG RAM ##########

    @Test
    void testWriteMemoryPrgRam() {
        int address = Integer.parseInt("6017", 16);
        nRom.writeMemory(address, 125);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)) == 125);
    }

    @Test
    void testWriteMemoryPrgRamOverflow() {
        int address = Integer.parseInt("6017", 16);
        nRom.writeMemory(address, 125 + 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)) == 125);
    }

    @Test
    void testWriteMemoryPrgRamUnderflow() {
        int address = Integer.parseInt("6017", 16);
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)) == 125);
    }

    @Test
    void testWriteMemoryPrgRamLowerBound() {
        int address = Integer.parseInt("6000", 16);
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)) == 125);
    }

    @Test
    void testWriteMemoryPrgRamUpperBound() {
        int address = Integer.parseInt("7FFF", 16);
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRam(address - Integer.parseInt("6000", 16)) == 125);

    }




    // ######### TESTS FOR WRITING MEMORY: PRG ROM ##########

    @Test
    void testWriteMemoryPrgRom() {
        int address = Integer.parseInt("8017", 16);
        nRom.writeMemory(address, 125);
        assertTrue(nRom.getPrgRom(address - Integer.parseInt("8000", 16)) == 125);
    }

    @Test
    void testWriteMemoryPrgRomUnderflow() {
        int address = Integer.parseInt("8017", 16);
        nRom.writeMemory(address, 125 - 256);
        assertTrue(nRom.getPrgRom(address - Integer.parseInt("8000", 16)) == 125);
    }

    @Test
    void testWriteMemoryPrgRomOverflow() {
        int address = Integer.parseInt("8017", 16);
        nRom.writeMemory(address, 125 + 256);
        assertTrue(nRom.getPrgRom(address - Integer.parseInt("8000", 16)) == 125);
    }

    @Test
    void testWriteMemoryPrgRomLowerBound() {
        int address = Integer.parseInt("8000", 16);
        nRom.writeMemory(address, 125);
        assertTrue(nRom.getPrgRom(address - Integer.parseInt("8000", 16)) == 125);
    }

    @Test
    void testWriteMemoryPrgRomUpperBound() {
        int address = Integer.parseInt("F7FF", 16);
        nRom.writeMemory(address, 125);
        assertTrue(nRom.getPrgRom(address - Integer.parseInt("8000", 16)) == 125);
    }

    @Test
    void testWriteMemoryFailure() {
        boolean isSuccessful = nRom.writeMemory(Integer.parseInt("5000", 16), 47);
        assertTrue(!isSuccessful);
    }
}
