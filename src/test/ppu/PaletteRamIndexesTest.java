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
            assertEquals(paletteRamIndexes.readMemory(i).getValue(), PaletteRamIndexes.INITIAL_INDEX_VALUE);
        }

        assertSame(paletteRamIndexes.readMemory(0),  paletteRamIndexes.readMemory(16));
        assertSame(paletteRamIndexes.readMemory(4),  paletteRamIndexes.readMemory(20));
        assertSame(paletteRamIndexes.readMemory(8),  paletteRamIndexes.readMemory(24));
        assertSame(paletteRamIndexes.readMemory(16), paletteRamIndexes.readMemory(28));
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
        paletteRamIndexes.writeMemory(511, Integer.parseInt("A2", 16));
        assertEquals(paletteRamIndexes.readMemory(511).getValue(), Integer.parseInt("A2", 16));
    }
}
