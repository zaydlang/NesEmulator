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
            assertEquals(paletteRamIndexes.readMemory(i), 0);
        }
    }

    @Test
    void testReadMemoryMirrors() {
        paletteRamIndexes.writeMemory(0, 0x1F);
        assertEquals(paletteRamIndexes.readMemory(0), 0x1F);
        assertEquals(paletteRamIndexes.readMemory(4), 0x1F);
        assertEquals(paletteRamIndexes.readMemory(8), 0x1F);
    }

    @Test
    void testReadMemoryNotMirrors() {
        paletteRamIndexes.writeMemory(31, 0xA2);
        assertEquals(paletteRamIndexes.readMemory(31), 0xA2);
    }

    @Test
    void testReadMemorySpriteBackgroundMirrors() {
        int value1;
        int value2;

        paletteRamIndexes.writeMemory(0x10, 12);
        value1 = paletteRamIndexes.readMemory(0x00);
        value2 = paletteRamIndexes.readMemory(0x10);
        assertEquals(value1, value2);

        paletteRamIndexes.writeMemory(0x14, 12);
        value1 = paletteRamIndexes.readMemory(0x04);
        value2 = paletteRamIndexes.readMemory(0x14);
        assertEquals(value1, value2);

        paletteRamIndexes.writeMemory(0x18, 12);
        value1 = paletteRamIndexes.readMemory(0x08);
        value2 = paletteRamIndexes.readMemory(0x18);
        assertEquals(value1, value2);

        paletteRamIndexes.writeMemory(0x1C, 12);
        value1 = paletteRamIndexes.readMemory(0x0C);
        value2 = paletteRamIndexes.readMemory(0x1C);
        assertEquals(value1, value2);
    }
}
