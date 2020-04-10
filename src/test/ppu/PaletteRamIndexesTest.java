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
        paletteRamIndexes.writeMemory(0, 0x1F);
        assertEquals(paletteRamIndexes.readMemory(0).getValue(), 0x1F);
        assertEquals(paletteRamIndexes.readMemory(4).getValue(), 0x1F);
        assertEquals(paletteRamIndexes.readMemory(8).getValue(), 0x1F);
    }

    @Test
    void testReadMemoryNotMirrors() {
        paletteRamIndexes.writeMemory(31, 0xA2);
        assertEquals(paletteRamIndexes.readMemory(31).getValue(), 0xA2);
    }

    @Test
    void testReadMemorySpriteBackgroundMirrors() {
        int value1;
        int value2;

        paletteRamIndexes.writeMemory(0x10, 12);
        value1 = paletteRamIndexes.readMemory(0x00).getValue();
        value2 = paletteRamIndexes.readMemory(0x10).getValue();
        assertEquals(value1, value2);

        paletteRamIndexes.writeMemory(0x14, 12);
        value1 = paletteRamIndexes.readMemory(0x04).getValue();
        value2 = paletteRamIndexes.readMemory(0x14).getValue();
        assertEquals(value1, value2);

        paletteRamIndexes.writeMemory(0x18, 12);
        value1 = paletteRamIndexes.readMemory(0x08).getValue();
        value2 = paletteRamIndexes.readMemory(0x18).getValue();
        assertEquals(value1, value2);

        paletteRamIndexes.writeMemory(0x1C, 12);
        value1 = paletteRamIndexes.readMemory(0x0C).getValue();
        value2 = paletteRamIndexes.readMemory(0x1C).getValue();
        assertEquals(value1, value2);
    }
}
