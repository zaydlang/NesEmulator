package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilTest {
    @SuppressWarnings("SimplifiableJUnitAssertion")
    @Test
    void getNthBit() {
        int value = Integer.parseInt("10011011", 2);
        assertTrue(Util.getNthBit(value, 0) == 1);
        assertTrue(Util.getNthBit(value, 1) == 1);
        assertTrue(Util.getNthBit(value, 2) == 0);
        assertTrue(Util.getNthBit(value, 3) == 1);
        assertTrue(Util.getNthBit(value, 4) == 1);
        assertTrue(Util.getNthBit(value, 5) == 0);
        assertTrue(Util.getNthBit(value, 6) == 0);
        assertTrue(Util.getNthBit(value, 7) == 1);
    }
}
