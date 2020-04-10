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
        shiftRegister.setNthBits(0, 3,   0b110);
        assertEquals(shiftRegister.getValue(), 0b110);
    }

    @Test
    void testShiftLeftOnce() {
        shiftRegister.setNthBits(0, 3,   0b110);
        shiftRegister.shiftLeft(1);
        assertEquals(shiftRegister.getValue(), 0b011);
    }

    @Test
    void testShiftLeftMultiple() {
        shiftRegister.setNthBits(0, 3,   0b110);
        shiftRegister.shiftLeft(2);
        assertEquals(shiftRegister.getValue(), 0b001);
    }

    @Test
    void testShiftRightOnce() {
        shiftRegister.setNthBits(0, 3,   0b110);
        shiftRegister.shiftRight(1);
        assertEquals(shiftRegister.getValue(), 0b100);
    }

    @Test
    void testShiftRightMultiple() {
        shiftRegister.setNthBits(0, 3,   0b110);
        shiftRegister.shiftRight(2);
        assertEquals(shiftRegister.getValue(), 0b000);
    }
}
