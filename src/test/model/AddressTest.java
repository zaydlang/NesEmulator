package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddressTest {
    Address address;

    @Test
    void testConstructorAllArguments() {
        address = new Address(10, 20, 0, 24);

        assertEquals(10, (int) address.getValue());
        assertEquals(20, address.getPointer());
    }

    @Test
    void testConstructorDefaultPointer() {
        address = new Address(10, 0, 24);

        assertEquals(10, (int) address.getValue());
        assertEquals(10, address.getPointer());
    }

    @Test
    void testConstructorDefaultLowestAndHighestValue() {
        address = new Address(0, 20);

        assertEquals(0, (int) address.getValue());
        assertEquals(20, address.getPointer());
    }

    @Test
    void testConstructorDefaultPointerAndLowestAndHighestValue() {
        address = new Address(10);

        assertEquals(10, (int) address.getValue());
        assertEquals(10, address.getPointer());
    }

    @Test
    void testSetValue() {
        address = new Address(10, 20, 1, 24);
        address.setValue(11);

        assertEquals(11, (int) address.getValue());
    }

    @Test
    void testSetValueUpperBound() {
        address = new Address(10, 20, 1, 24);
        address.setValue(24);

        assertEquals(24, (int) address.getValue());
    }

    @Test
    void testSetValueLowerBound() {
        address = new Address(10, 20, 1, 24);
        address.setValue(1);

        assertEquals(1, (int) address.getValue());
    }

    @Test
    void testSetValueOverflow() {
        address = new Address(10, 20, 1, 24);
        address.setValue(30);

        assertEquals(6, (int) address.getValue());
    }

    @Test
    void testSetValueUnderflow() {
        address = new Address(10, 20, 1, 24);
        address.setValue(0);

        assertEquals(24, (int) address.getValue());
    }
}
