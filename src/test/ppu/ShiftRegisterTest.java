package ppu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShiftRegisterTest {
    ShiftRegister shiftRegister;

    @BeforeEach
    void runBefore() {
        shiftRegister = new ShiftRegister(3);
    }

    @Test
    void testConstructor() {
        assertTrue(shiftRegister.getValue() == 0);
    }

    @Test
    void testSetNthBits() {
        shiftRegister.setNthBits(0, 3,   Integer.parseInt("110", 2));
        assertEquals(shiftRegister.getValue(), Integer.parseInt("110", 2));
    }

    @Test
    void testShiftLeftOnce() {
        shiftRegister.setNthBits(0, 3,   Integer.parseInt("110", 2));
        shiftRegister.shiftLeft(1);
        assertEquals(shiftRegister.getValue(), Integer.parseInt("011", 2));
    }

    @Test
    void testShiftLeftMultiple() {
        shiftRegister.setNthBits(0, 3,   Integer.parseInt("110", 2));
        shiftRegister.shiftLeft(2);
        assertEquals(shiftRegister.getValue(), Integer.parseInt("001", 2));
    }

    @Test
    void testShiftRightOnce() {
        shiftRegister.setNthBits(0, 3,   Integer.parseInt("110", 2));
        shiftRegister.shiftRight(1);
        assertEquals(shiftRegister.getValue(), Integer.parseInt("100", 2));
    }

    @Test
    void testShiftRightMultiple() {
        shiftRegister.setNthBits(0, 3,   Integer.parseInt("110", 2));
        shiftRegister.shiftRight(2);
        assertEquals(shiftRegister.getValue(), Integer.parseInt("000", 2));
    }
}
