package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SimplifiableJUnitAssertion")
public class CpuTest {
    CPU cpu;

    @BeforeEach
    void runBefore() {
        cpu = new CPU();
    }

    @Test
    void testConstructor() {
        assertTrue(cpu.getRegisterA()  == CPU.INITIAL_REGISTER_A);
        assertTrue(cpu.getRegisterX()  == CPU.INITIAL_REGISTER_X);
        assertTrue(cpu.getRegisterY()  == CPU.INITIAL_REGISTER_Y);
        assertTrue(cpu.getRegisterPC() == CPU.INITIAL_REGISTER_PC);
        assertTrue(cpu.getRegisterP()  == CPU.INITIAL_REGISTER_P);
        assertTrue(cpu.getRegisterS()  == CPU.INITIAL_REGISTER_S);
        assertTrue(cpu.getCycles()     == CPU.INITIAL_CYCLES);
        assertTrue(cpu.peekStack()     == CPU.INITIAL_STACK_STATE);

        for (int i : cpu.ram) {
            assertTrue(i == CPU.INITIAL_RAM_STATE);
        }
    }

    // ########## TESTS FOR READING MEMORY: INTERNAL RAM ##########

    @Test
    void testReadMemoryInternalRam() {
        int address = Integer.parseInt("0547", 16);
        cpu.ram[address] = 157;
        assertTrue(cpu.readMemory(address) == 157);
    }

    @Test
    void testReadMemoryInternalRamLowerBound() {
        int address = Integer.parseInt("0000", 16);
        cpu.ram[address] = 157;
        assertTrue(cpu.readMemory(address) == 157);
    }

    @Test
    void testReadMemoryInternalRamUpperBound() {
        int address = Integer.parseInt("07FF", 16);
        cpu.ram[address] = 157;
        assertTrue(cpu.readMemory(address) == 157);
    }




    // ########## TESTS FOR READING MEMORY: INTERNAL RAM MIRRORS ##########

    @Test
    void testReadMemoryInternalRamMirrors() {
        int baseAddress = Integer.parseInt("0547", 16);
        cpu.ram[baseAddress] = 157;
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16);
            assertTrue(cpu.readMemory(address) == 157);
        }
    }

    @Test
    void testReadMemoryInternalRamMirrorsLowerBound() {
        int baseAddress = Integer.parseInt("0000", 16);
        cpu.ram[baseAddress] = 157;
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16);
            assertTrue(cpu.readMemory(address) == 157);
        }
    }

    @Test
    void testReadMemoryInternalRamMirrorsUpperBound() {
        int baseAddress = Integer.parseInt("07FF", 16);
        cpu.ram[baseAddress] = 157;
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16);
            assertTrue(cpu.readMemory(address) == 157);
        }
    }





    // ########## TESTS FOR WRITING MEMORY: INTERNAL RAM ##########

    @Test
    void testWriteMemoryInternalRam() {
        int address = Integer.parseInt("0547", 16);
        cpu.writeMemory(address, 157);
        assertTrue(cpu.ram[address] == 157);
    }

    @Test
    void testWriteMemoryInternalRamUnderflow() {
        int address = Integer.parseInt("0547", 16);
        cpu.writeMemory(address, 157 - 256);
        assertTrue(cpu.ram[address] == 157);
    }

    @Test
    void testWriteMemoryInternalRamOverflow() {
        int address = Integer.parseInt("0547", 16);
        cpu.writeMemory(address, 157 + 256);
        assertTrue(cpu.ram[address] == 157);
    }

    @Test
    void testWriteMemoryInternalRamLowerBound() {
        int address = Integer.parseInt("0000", 16);
        cpu.writeMemory(address, 157 - 256);
        assertTrue(cpu.ram[address] == 157);
    }

    @Test
    void testWriteMemoryInternalRamUpperBound() {
        int address = Integer.parseInt("07FF", 16);
        cpu.writeMemory(address, 157 - 256);
        assertTrue(cpu.ram[address] == 157);
    }




    // ########## TESTS FOR WRITING MEMORY: INTERNAL RAM MIRRORS ##########

    @Test
    void testWriteMemoryInternalRamMirrors() {
        int baseAddress = Integer.parseInt("0547", 16);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16) - i;
            cpu.writeMemory(address, 157 + i);
            assertTrue(cpu.ram[baseAddress - i] == 157 + i);
        }
    }

    @Test
    void testWriteMemoryInternalRamMirrorsUnderflow() {
        int baseAddress = Integer.parseInt("0547", 16);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16) - i;
            cpu.writeMemory(address, 157 + i - Integer.parseInt("0800",16));
            assertTrue(cpu.ram[baseAddress - i] == 157 + i);
        }
    }

    @Test
    void testWriteMemoryInternalRamMirrorsOverflow() {
        int baseAddress = Integer.parseInt("0547", 16);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16) - i;
            cpu.writeMemory(address, 157 + i + Integer.parseInt("0800",16));
            assertTrue(cpu.ram[baseAddress - i] == 157 + i);
        }
    }

    @Test
    void testWriteMemoryInternalRamMirrorsLowerBound() {
        int baseAddress = Integer.parseInt("0000", 16);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16) + i;
            cpu.writeMemory(address, 157 + i + Integer.parseInt("0800",16));
            assertTrue(cpu.ram[baseAddress + i] == 157 + i);
        }
    }

    @Test
    void testWriteMemoryInternalRamMirrorsUpperBound() {
        int baseAddress = Integer.parseInt("07FF", 16);
        for (int i = 1; i < 4; i++) {
            int address = baseAddress + i * Integer.parseInt("0800",16) - i;
            cpu.writeMemory(address, 157 + i + Integer.parseInt("0800",16));
            assertTrue(cpu.ram[baseAddress - i] == 157 + i);
        }
    }




    // ########## TESTS FOR WRITING MEMORY: MAPPER ##########

    @Test
    void testWriteMemoryMapper() {
        int address = Integer.parseInt("6017", 16);
        cpu.setMapper(new NRom());
        cpu.writeMemory(address, 137);
        assertTrue(cpu.getMapper().readMemory(address) == 137);
    }

    @Test
    void testWriteMemoryMapperUnderflow() {
        int address = Integer.parseInt("6017", 16);
        cpu.setMapper(new NRom());
        cpu.writeMemory(address, 137 - 256);
        assertTrue(cpu.getMapper().readMemory(Integer.parseInt("6017", 16)) == 137);
    }

    @Test
    void testWriteMemoryMapperOverflow() {
        int address = Integer.parseInt("6017", 16);
        cpu.setMapper(new NRom());
        cpu.writeMemory(address, 137 + 256);
        assertTrue(cpu.getMapper().readMemory(address) == 137);
    }

    @Test
    void testWriteMemoryMapperLowerBound() {
        int address = Integer.parseInt("4020", 16);
        cpu.setMapper(new NRom());
        boolean isSuccessful = cpu.writeMemory(address, 137);
        assertTrue(cpu.getMapper().readMemory(address) == 137 || !isSuccessful);
    }

    @Test
    void testWriteMemoryMapperUpperBound() {
        int address = Integer.parseInt("FFFF", 16);
        cpu.setMapper(new NRom());
        boolean isSuccessful = cpu.writeMemory(address, 137);
        assertTrue(cpu.getMapper().readMemory(address) == 137 || !isSuccessful);
    }

    @Test
    void testGetStatus() {
        int testCpuStatus = Integer.parseInt("11010001", 2);
        cpu.setFlagC(Util.getNthBit(testCpuStatus, 0));
        cpu.setFlagZ(Util.getNthBit(testCpuStatus, 1));
        cpu.setFlagI(Util.getNthBit(testCpuStatus, 2));
        cpu.setFlagD(Util.getNthBit(testCpuStatus, 3));
        cpu.setFlagB(Util.getNthBit(testCpuStatus, 4));
        // bit 5 in the flags byte is empty
        cpu.setFlagV(Util.getNthBit(testCpuStatus, 6));
        cpu.setFlagN(Util.getNthBit(testCpuStatus, 7));

        assertTrue(cpu.getStatus() == testCpuStatus);
    }




    // ########## TESTS FOR MISCELLANEOUS CPU METHODS ##########

    // TODO: unsure how to implement such a test, considering there's 192 different instructions.
    @Test
    void testCycle() {

    }

    @Test
    void testSetStatus() {
        int testCpuStatus = Integer.parseInt("11010001", 2);
        cpu.setStatus(testCpuStatus);
        assertTrue(cpu.getFlagC() == Util.getNthBit(testCpuStatus, 0));
        assertTrue(cpu.getFlagZ() == Util.getNthBit(testCpuStatus, 1));
        assertTrue(cpu.getFlagI() == Util.getNthBit(testCpuStatus, 2));
        assertTrue(cpu.getFlagD() == Util.getNthBit(testCpuStatus, 3));
        assertTrue(cpu.getFlagB() == Util.getNthBit(testCpuStatus, 4));
        // bit 5 in the flags byte is empty
        assertTrue(cpu.getFlagV() == Util.getNthBit(testCpuStatus, 6));
        assertTrue(cpu.getFlagN() == Util.getNthBit(testCpuStatus, 7));
    }

    @Test
    void testPushStack() {
        cpu.pushStack(100);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 1);
        assertTrue(cpu.peekStack()    == 100);

        cpu.pushStack(370);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 2);
        assertTrue(cpu.peekStack()    == 370);

        cpu.pushStack(123);
        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 3);
        assertTrue(cpu.peekStack()    == 123);
    }

    @Test
    void testPullStack() {
        cpu.pushStack(100);
        cpu.pushStack(370);
        cpu.pushStack(123);

        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 3);
        assertTrue(cpu.pullStack()    == 123);

        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 2);
        assertTrue(cpu.pullStack()    == 370);

        assertTrue(cpu.getRegisterS() == CPU.INITIAL_REGISTER_S - 1);
        assertTrue(cpu.pullStack()    == 100);
    }
}
