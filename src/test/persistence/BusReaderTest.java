package persistence;

import model.Address;
import model.Bus;
import model.CPU;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BusReaderTest {
    static Bus expectedBus;
    static Bus actualBus;

    static CPU expectedCpu;
    static CPU actualCpu;

    @BeforeAll
    static void runBeforeAll() {
        try {
            actualBus   = new Bus();
            actualBus.loadCartridge(new File("./data/rom/nestest.nes"));
            actualBus.getCpu().addBreakpoint(new Address(Integer.parseInt("ABCD", 16)));
            actualBus.cycleComponents();
            BusWriter.writeToFile(actualBus, "test");

            expectedBus = new Bus();
            expectedBus.reset();
            BusReader.readFromFile(expectedBus, "test");
        } catch (IOException e) {
            fail("IOException thrown! Are you sure the file exists?");
        }

        expectedCpu = expectedBus.getCpu();
        actualCpu   = actualBus.getCpu();
    }


    @AfterAll()
    static void runAfterAll() {
        new File("./data/save/test.sav").delete();
    }

    @Test
    void testCpuRegisters() {
        assertAddressEquality(expectedCpu.getRegisterA(),  actualCpu.getRegisterA());
        assertAddressEquality(expectedCpu.getRegisterX(),  actualCpu.getRegisterX());
        assertAddressEquality(expectedCpu.getRegisterY(),  actualCpu.getRegisterY());
        assertAddressEquality(expectedCpu.getRegisterPC(), actualCpu.getRegisterPC());
        assertAddressEquality(expectedCpu.getRegisterS(),  actualCpu.getRegisterS());
    }

    @Test
    void testCpuFlags() {
        assertEquals(expectedCpu.getStatus(),  actualCpu.getStatus());
    }
    
    @Test
    void testCpuRam() {
        for (int i = 0; i < Integer.parseInt("0800", 16); i++) {
            assertAddressEquality(expectedCpu.readMemory(i), actualCpu.readMemory(i));
        }
    }

    @Test
    void testCpuState() {
        assertEquals(expectedCpu.getCycles(), actualCpu.getCycles());
        for (int i = 0; i < expectedCpu.getBreakpoints().size(); i++) {
            assertAddressEquality(expectedCpu.getBreakpoints().get(i), actualCpu.getBreakpoints().get(i));
        }
    }

    void assertAddressEquality(Address expected, Address actual) {
        assertEquals(expected.getPointer(), actual.getPointer());
        assertEquals(expected.getValue(),   actual.getValue());
    }
}
