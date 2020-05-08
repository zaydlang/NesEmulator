package persistence;

import model.Address;
import model.Bus;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class BusWriterTest {

    @Test
    void testConstructor() {
        try {
            BusWriter busWriter = new BusWriter();
        } catch (Exception e) {
            fail("Dummy constructor failed!");
        }
    }

    @Test
    void testWrite() {
        try {
            Bus bus = Bus.getInstance();
            bus.loadCartridge(new File("./data/rom/nestest.nes"));
            bus.getCpu().addBreakpoint(new Address(0xABCD));

            for (int i = 0; i < 10000; i++) {
                bus.cycleComponents();
            }

            BusWriter.writeToFile(bus, "test");
        } catch (IOException e) {
            fail("IOException thrown! Are you sure the file exists?");
        }
    }

    @Test
    void testWriteFail() {
        try {
            Bus bus = Bus.getInstance();
            bus.loadCartridge(new File("./data/rom/nestest.nes"));
            BusWriter.writeToFile(bus, "this/file/does/not.exist");
        } catch (Exception e) {
            fail("This should be handled by the BusReader!");
        }
    }
}
