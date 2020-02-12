package model;

import org.junit.jupiter.api.Test;
import model.Util;

import static org.junit.jupiter.api.Assertions.*;

public class UtilTest {
    // makes the code coverage autobot not complain about me not instantiating a Util class.
    @Test
    void testConstructor() {
        try {
            Util util = new Util();
        } catch (Exception e) {
            fail();
        }
    }

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

    @Test
    void testGetSignPositive() {
        assertEquals(1, Util.getSign(120));
    }

    @Test
    void testGetSignZero() {
        assertEquals(0, Util.getSign(0));
    }

    @Test
    void testGetSignNegative() {
        assertEquals(Util.getSign(240), -1);
    }
}
