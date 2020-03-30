package ppu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpriteTest {
    Sprite sprite;

    @BeforeEach
    void runBefore() {
        int patternTableData0 = Integer.parseInt("10010011", 2);
        int patternTableData1 = Integer.parseInt("01011011", 2);
        sprite = new Sprite(patternTableData0, patternTableData1, 3, 5, 0, false, false);
    }

    @Test
    void testConstructor() {
        assertEquals(0,     sprite.getPriority());
        assertEquals(false, sprite.isActive());
    }

    @Test
    void testIsActive() {
        for (int expectedCounter = 5; expectedCounter >= -7; expectedCounter--) {
            assertEquals(-7 <= expectedCounter && expectedCounter <= 0, sprite.isActive());
            sprite.decrementCounter();
        }
    }

    @Test
    void testGetNextColorAddressAsInt() {
        int[] expectedValues = new int[]{
                Integer.parseInt("11", 2) + (3 << 2),
                Integer.parseInt("11", 2) + (3 << 2),
                Integer.parseInt("00", 2) + (3 << 2),
                Integer.parseInt("10", 2) + (3 << 2),
                Integer.parseInt("11", 2) + (3 << 2),
                Integer.parseInt("00", 2) + (3 << 2),
                Integer.parseInt("10", 2) + (3 << 2),
                Integer.parseInt("01", 2) + (3 << 2)
        };

        for (int i = 0; i < 8; i++) {
            assertEquals(expectedValues[i], sprite.getNextColorAddressAsInt());
        }
    }
}
