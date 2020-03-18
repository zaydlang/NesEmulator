package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PendingAddressTest {
    @Test
    void testConstructor() {
        PendingAddress pendingAddress = new PendingAddress(0, () -> {
           fail();
           return new Address(0);
        });
    }

    @Test
    void testGetValue() {
        PendingAddress pendingAddress = new PendingAddress(0, () -> new Address(50));
        assertEquals(pendingAddress.getValue(), 50);
    }
}
