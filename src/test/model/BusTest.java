package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BusTest {
    Bus bus;

    @BeforeEach
    void runBefore() {
        bus = new Bus();
    }

    @Test
    void testConstructor() {
        assertEquals(bus.getCartridgeLoaded(),     false);
        assertEquals(bus.getControllerConnected(), false);
        assertEquals(bus.getEnabled(),             true);
    }

    @Test
    void testCycleNotEnabled() {
        bus.cycle();
        assertEquals(0, bus.getCpu().getCycles());

        bus.setEnabled(false);
        bus.cycle();
        assertEquals(0, bus.getCpu().getCycles());
    }

    @Test
    void testCycleEnabled() {
        try {
            bus.setEnabled(true);
            bus.loadCartridge(new File("./data/test/TestLoadRomTrainerNotPresentSmall.nes"));
            bus.cycle();
            assertEquals(8, bus.getCpu().getCycles());
        } catch (IOException e) {
            fail("Bus failed to cycle!");
        }
    }
}
