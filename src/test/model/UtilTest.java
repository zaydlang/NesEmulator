package model;

import org.junit.jupiter.api.Test;
import model.Util;
import org.junit.jupiter.api.TestTemplate;

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
    void getNthBits() {
        int value = Integer.parseInt("10011011", 2);
        assertTrue(Util.getNthBits(value, 0, 3) == Integer.parseInt("011", 2));
        assertTrue(Util.getNthBits(value, 1, 5) == Integer.parseInt("01101", 2));
        assertTrue(Util.getNthBits(value, 2, 1) == Integer.parseInt("0", 2));
    }

    @Test
    void testMaskNthBits() {
        int mask  = Integer.parseInt("10010101", 2);
        int value = Integer.parseInt("10111",    2);
        int newValue = Util.maskNthBits(mask, value, 1, 2, 3);
        assertTrue(newValue == Integer.parseInt("01011", 2));
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

    @Test
    void testReverse() {
        int original = Integer.parseInt("1010111001001", 2);
        int expected = Integer.parseInt("1001001110101", 2);
        assertEquals(Util.reverse(original, 13), expected);
    }
}
