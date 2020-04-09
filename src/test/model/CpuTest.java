package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.controller.Controller;
import ui.controller.StandardController;

import java.io.IOException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("SimplifiableJUnitAssertion")
public class CpuTest {
    Bus bus;
    CPU cpu;

    @BeforeEach
    void runBefore() {
        try {
            bus = new Bus();
            cpu = bus.getCpu();
            bus.loadCartridge(new File("data/test/TestLoadRomTrainerPresent.nes"));
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testConstructor() {
        assertTrue(cpu.getRegisterA().getValue()  == CPU.INITIAL_REGISTER_A);
        assertTrue(cpu.getRegisterX().getValue()  == CPU.INITIAL_REGISTER_X);
        assertTrue(cpu.getRegisterY().getValue()  == CPU.INITIAL_REGISTER_Y);
        assertTrue(cpu.getRegisterS().getValue()  == CPU.INITIAL_REGISTER_S);
        assertTrue(cpu.getCycles()                == CPU.INITIAL_CYCLES);
    }

    // ########## TESTS FOR READING MEMORY: INTERNAL RAM ##########

    @Test
    void testReadMemoryInternalRam() {
        int address = Integer.parseInt("0547", 16);
        cpu.ram[address] = new Address(157);
        assertTrue(cpu.readMemory(address).getValue() == 157);
    }

    @Test
    void testReadMemoryInternalRamLowerBound() {
        int address = Integer.parseInt("0000", 16);
        cpu.ram[address] = new Address(157);
        assertTrue(cpu.readMemory(address).getValue() == 157);
    }

    @Test
    void testReadMemoryInternalRamUpperBound() {
        int address = Integer.parseInt("07FF", 16);
        cpu.ram[address] = new Address(157);
        assertTrue(cpu.readMemory(address).getValue() == 157);
    }




    // ########## TESTS FOR READING MEMORY: INTERNAL RAM MIRRORS ##########

    @Test
    void testReadMemoryInternalRamMirrors() {
        int baseAddress = Integer.parseInt("0547", 16);
        cpu.ram[baseAddress] = new Address(157);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16);
            assertTrue(cpu.readMemory(address).getValue() == 157);
        }
    }

    @Test
    void testReadMemoryInternalRamMirrorsLowerBound() {
        int baseAddress = Integer.parseInt("0000", 16);
        cpu.ram[baseAddress] = new Address(157);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16);
            assertTrue(cpu.readMemory(address).getValue() == 157);
        }
    }

    @Test
    void testReadMemoryInternalRamMirrorsUpperBound() {
        int baseAddress = Integer.parseInt("07FF", 16);
        cpu.ram[baseAddress] = new Address(157);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16);
            assertTrue(cpu.readMemory(address).getValue() == 157);
        }
    }

    @Test
    void testReadMemoryPPURegisters() {
        // Since PPU Reads will mainly be tested in PpuTest, I will only simply check that no exceptions are thrown.
        for (int i = 0; i < 8; i++) {
            int address = Integer.parseInt("2000", 16) + i;
            try {
                cpu.readMemory(address);
            } catch (Exception e) {
                fail("Read to address 0x" + address + " failed!");
            }
        }
    }

    @Test
    void testReadMemoryPpuDma() {
        try {
            cpu.readMemory(Integer.parseInt("4014", 16));
        } catch (Exception e) {
            fail("Read to address 0x4014 failed!");
        }
    }

    @Test
    void testApuRead() {
        // APU has not been implemented, so the CPU should return an empty address.
        assertEquals(0, cpu.readMemory(Integer.parseInt("4000", 16)).getValue());
        assertEquals(0, cpu.readMemory(Integer.parseInt("4013", 16)).getValue());
        assertEquals(0, cpu.readMemory(Integer.parseInt("4015", 16)).getValue());
        assertEquals(0, cpu.readMemory(Integer.parseInt("4018", 16)).getValue());
        assertEquals(0, cpu.readMemory(Integer.parseInt("401F", 16)).getValue());
    }

    @Test
    void testControllerRead() {
        bus.setController(new StandardController());
        try {
            cpu.readMemory(Integer.parseInt("4016", 16));
            cpu.readMemory(Integer.parseInt("4017", 16));
        } catch (Exception e) {
            fail("Read from Controller failed!");
        }
    }





    // ########## TESTS FOR WRITING MEMORY: INTERNAL RAM ##########

    @Test
    void testWriteMemoryInternalRam() {
        int address = Integer.parseInt("0547", 16);
        cpu.writeMemory(address, 157);
        assertTrue(cpu.ram[address].getValue() == 157);
    }

    @Test
    void testWriteMemoryInternalRamUnderflow() {
        int address = Integer.parseInt("0547", 16);
        cpu.writeMemory(address, 157 - 256);
        assertTrue(cpu.ram[address].getValue() == 157);
    }

    @Test
    void testWriteMemoryInternalRamOverflow() {
        int address = Integer.parseInt("0547", 16);
        cpu.writeMemory(address, 157 + 256);
        assertTrue(cpu.ram[address].getValue() == 157);
    }

    @Test
    void testWriteMemoryInternalRamLowerBound() {
        int address = Integer.parseInt("0000", 16);
        cpu.writeMemory(address, 157 - 256);
        assertTrue(cpu.ram[address].getValue() == 157);
    }

    @Test
    void testWriteMemoryInternalRamUpperBound() {
        int address = Integer.parseInt("07FF", 16);
        cpu.writeMemory(address, 157 - 256);
        assertTrue(cpu.ram[address].getValue() == 157);
    }




    // ########## TESTS FOR WRITING MEMORY: INTERNAL RAM MIRRORS ##########

    @Test
    void testWriteMemoryInternalRamMirrors() {
        int baseAddress = Integer.parseInt("0547", 16);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16) - i;
            cpu.writeMemory(address, 157 + i);
            assertTrue(cpu.ram[baseAddress - i].getValue() == 157 + i);
        }
    }

    @Test
    void testWriteMemoryInternalRamMirrorsUnderflow() {
        int baseAddress = Integer.parseInt("0547", 16);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16) - i;
            cpu.writeMemory(address, 157 + i - Integer.parseInt("0800",16));
            assertTrue(cpu.ram[baseAddress - i].getValue() == 157 + i);
        }
    }

    @Test
    void testWriteMemoryInternalRamMirrorsOverflow() {
        int baseAddress = Integer.parseInt("0547", 16);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16) - i;
            cpu.writeMemory(address, 157 + i + Integer.parseInt("0800",16));
            assertTrue(cpu.ram[baseAddress - i].getValue() == 157 + i);
        }
    }

    @Test
    void testWriteMemoryInternalRamMirrorsLowerBound() {
        int baseAddress = Integer.parseInt("0000", 16);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16) + i;
            cpu.writeMemory(address, 157 + i + Integer.parseInt("0800",16));
            assertTrue(cpu.ram[baseAddress + i].getValue() == 157 + i);
        }
    }

    @Test
    void testWriteMemoryInternalRamMirrorsUpperBound() {
        int baseAddress = Integer.parseInt("07FF", 16);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16) - i;
            cpu.writeMemory(address, 157 + i + Integer.parseInt("0800",16));
            assertTrue(cpu.ram[baseAddress - i].getValue() == 157 + i);
        }
    }

    @Test
    void testWriteMemoryPPU() {
        // Since PPU Writes will mainly be tested in PpuTest, I will only simply check that no exceptions are thrown.
        for (int i = 0; i < 8; i++) {
            int address = Integer.parseInt("2000", 16) + i;
            try {
                cpu.writeMemory(address, 0);
            } catch (Exception e) {
                fail("Write to address 0x" + address + " failed!");
            }
        }
    }

    @Test
    void testWriteMemoryController() {
        bus.setController(new StandardController());
        Controller controller = bus.getController();
        cpu.writeMemory(Integer.parseInt("4016", 16), 1);
        assertEquals(true,  controller.getPolling());
        cpu.writeMemory(Integer.parseInt("4016", 16), 0);
        assertEquals(false, controller.getPolling());

        // Controller 2 should not be connected yet, so writing to 0x4017 is useless.
        // More importantly, it should not affect Controller 1's polling state.
        cpu.writeMemory(Integer.parseInt("4017", 16), 1);
        assertEquals(false, controller.getPolling());
        cpu.writeMemory(Integer.parseInt("4017", 16), 0);
        assertEquals(false, controller.getPolling());
    }




    // ########## TESTS FOR MISCELLANEOUS CPU METHODS ##########

    @Test
    void testCycle() {
        for (int i = 0; i < Instruction.getInstructions().size(); i++) {
            Instruction instruction = Instruction.getInstructions().get(i);
            cpu.setRegisterPC(Integer.parseInt("6000", 16));

            // Simulate an instruction at the Program Counter
            cpu.writeMemory(cpu.getRegisterPC().getValue(), i);
            int numArguments = instruction.getNumArguments();
            for (int j = 0; j < numArguments; j++) {
                cpu.writeMemory(cpu.getRegisterPC().getValue() + j + 1, Integer.parseInt("0000", 16));
            }

            // Cycle the cpu, and make sure no exceptions are thrown
            // Note: Opcode / Mode testing are done separately, so this may be a bit redundant, which is why I'm only
            // checking for exceptions being thrown.
            try {
                cpu.cycle();
            } catch (Exception e) {
                fail();
            }
        }
    }

    @Test
    void testSetStatus() {
        int testCpuStatus = Integer.parseInt("11010001", 2);
        cpu.setStatus(testCpuStatus);
        assertTrue(cpu.getFlagC() == Util.getNthBit(testCpuStatus, 0));
        assertTrue(cpu.getFlagZ() == Util.getNthBit(testCpuStatus, 1));
        assertTrue(cpu.getFlagI() == Util.getNthBit(testCpuStatus, 2));
        assertTrue(cpu.getFlagD() == Util.getNthBit(testCpuStatus, 3));
        // bit 4 in the flags byte is empty
        // bit 5 in the flags byte is empty
        assertTrue(cpu.getFlagV() == Util.getNthBit(testCpuStatus, 6));
        assertTrue(cpu.getFlagN() == Util.getNthBit(testCpuStatus, 7));
    }

    @Test
    void testPushStack() {
        cpu.pushStack(100);
        assertTrue(cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 1);
        assertTrue(cpu.peekStack().getValue()    == 100);

        cpu.pushStack(255);
        assertTrue(cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 2);
        assertTrue(cpu.peekStack().getValue()    == 255);

        cpu.pushStack(0);
        assertTrue(cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 3);
        assertTrue(cpu.peekStack().getValue()    == 0);
    }

    @Test
    void testPullStack() {
        cpu.pushStack(0);
        cpu.pushStack(255);
        cpu.pushStack(123);

        assertTrue(cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 3);
        assertTrue(cpu.pullStack().getValue()    == 123);

        assertTrue(cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 2);
        assertTrue(cpu.pullStack().getValue()    == 255);

        assertTrue(cpu.getRegisterS().getValue() == CPU.INITIAL_REGISTER_S - 1);
        assertTrue(cpu.pullStack().getValue()    == 0);
    }

    @Test
    void testGetStatus() {
        int testCpuStatus = Integer.parseInt("11110001", 2);
        cpu.setFlagC(Util.getNthBit(testCpuStatus, 0));
        cpu.setFlagZ(Util.getNthBit(testCpuStatus, 1));
        cpu.setFlagI(Util.getNthBit(testCpuStatus, 2));
        cpu.setFlagD(Util.getNthBit(testCpuStatus, 3));
        // bit 4 in the flags byte is empty
        // bit 5 in the flags byte is empty
        cpu.setFlagV(Util.getNthBit(testCpuStatus, 6));
        cpu.setFlagN(Util.getNthBit(testCpuStatus, 7));

        assertTrue(cpu.getStatus() == testCpuStatus);
    }

    @Test
    void testIsBreakpointTrue() {
        Address breakpoint = new Address(Integer.parseInt("C5F5", 16), 0, 65536);
        cpu.addBreakpoint(breakpoint);
        assertTrue(cpu.isBreakpoint(breakpoint));
    }

    @Test
    void testIsBreakpointFalse() {
        Address breakpoint = new Address(Integer.parseInt("C5F5", 16), 0, 65536);
        cpu.addBreakpoint(breakpoint);
        assertFalse(cpu.isBreakpoint(new Address(Integer.parseInt("C001", 16), 0, 65536)));
    }

    @Test
    void testCycleIntoABreakpoint() {
        cpu.addBreakpoint(cpu.getRegisterPC());
        cpu.cycle();
        assertFalse(cpu.isEnabled());
    }

    @Test
    void testHandleNMI() {
        cpu.setRegisterPC(Integer.parseInt("0200", 16));
        cpu.nmi = true;
        cpu.cycle();
        assertEquals(Integer.parseInt("AB25", 16), cpu.getRegisterPC().getValue());
    }

    @Test
    void testHandleDMAOddCycles() {
        // Force the cycles to be odd
        if (cpu.getCycles() % 2 != 1) {
            cpu.cycle();
        }
        assertTrue(cpu.getCycles() % 2 == 1);

        // Test that the CPU is on hold for 513 cycles
        cpu.setRegisterPC(Integer.parseInt("0200", 16));
        cpu.writeMemory(Integer.parseInt("0200", 16), Integer.parseInt("A9", 16)); // LDA Immediate
        cpu.writeMemory(Integer.parseInt("0201", 16), Integer.parseInt("02", 16)); // 2
        cpu.setRegisterA(0); // If register A is set to two within the next 513 cycles, DMA has failed.
        cpu.writeMemory(Integer.parseInt("4014", 16), 0); // Start DMA
        for (int i = 0; i < 514; i++) {
            cpu.cycle();
            assertEquals(cpu.getRegisterA().getValue(), 0);
        }
    }

    @Test
    void testHandleDMAEvenCycles() {
        // Force the cycles to be even
        if (cpu.getCycles() % 2 != 0) {
            cpu.cycle();
        }
        assertTrue(cpu.getCycles() % 2 == 0);

        // Test that the CPU is on hold for 513 cycles
        cpu.setRegisterPC(Integer.parseInt("0200", 16));
        cpu.writeMemory(Integer.parseInt("0200", 16), Integer.parseInt("A9", 16)); // LDA Immediate
        cpu.writeMemory(Integer.parseInt("0201", 16), Integer.parseInt("02", 16)); // 2
        cpu.setRegisterA(0); // If register A is set to two within the next 513 cycles, DMA has failed.
        cpu.writeMemory(Integer.parseInt("4014", 16), 0); // Start DMA
        for (int i = 0; i < 513; i++) {
            cpu.cycle();
            assertEquals(cpu.getRegisterA().getValue(), 0);
        }
    }
}
