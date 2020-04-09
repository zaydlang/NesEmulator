package ppu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class PaletteRamIndexesTest {
    PaletteRamIndexes paletteRamIndexes;
    @BeforeEach
    void runBefore() {
        paletteRamIndexes = new PaletteRamIndexes();
    }

    @Test
    void testConstructor() {
        for (int i = 0; i < PaletteRamIndexes.PALETTE_RAM_INDEXES_SIZE; i++) {
            assertEquals(paletteRamIndexes.readMemory(i).getValue(), 0);
        }
    }

    @Test
    void testReadMemoryMirrors() {
        paletteRamIndexes.writeMemory(0, Integer.parseInt("1F", 16));
        assertEquals(paletteRamIndexes.readMemory(0).getValue(), Integer.parseInt("1F", 16));
        assertEquals(paletteRamIndexes.readMemory(4).getValue(), Integer.parseInt("1F", 16));
        assertEquals(paletteRamIndexes.readMemory(8).getValue(), Integer.parseInt("1F", 16));
    }

    @Test
    void testReadMemoryNotMirrors() {
        paletteRamIndexes.writeMemory(31, Integer.parseInt("A2", 16));
        assertEquals(paletteRamIndexes.readMemory(31).getValue(), Integer.parseInt("A2", 16));
    }

    @Test
    void testReadMemorySpriteBackgroundMirrors() {
        int value1;
        int value2;

        paletteRamIndexes.writeMemory(Integer.parseInt("10", 16), 12);
        value1 = paletteRamIndexes.readMemory(Integer.parseInt("00", 16)).getValue();
        value2 = paletteRamIndexes.readMemory(Integer.parseInt("10", 16)).getValue();
        assertEquals(value1, value2);

        paletteRamIndexes.writeMemory(Integer.parseInt("14", 16), 12);
        value1 = paletteRamIndexes.readMemory(Integer.parseInt("04", 16)).getValue();
        value2 = paletteRamIndexes.readMemory(Integer.parseInt("14", 16)).getValue();
        assertEquals(value1, value2);

        paletteRamIndexes.writeMemory(Integer.parseInt("18", 16), 12);
        value1 = paletteRamIndexes.readMemory(Integer.parseInt("08", 16)).getValue();
        value2 = paletteRamIndexes.readMemory(Integer.parseInt("18", 16)).getValue();
        assertEquals(value1, value2);

        paletteRamIndexes.writeMemory(Integer.parseInt("1C", 16), 12);
        value1 = paletteRamIndexes.readMemory(Integer.parseInt("0C", 16)).getValue();
        value2 = paletteRamIndexes.readMemory(Integer.parseInt("1C", 16)).getValue();
        assertEquals(value1, value2);
    }
}
