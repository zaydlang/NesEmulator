package persistence;

import mapper.Mapper;
import model.Address;
import model.Bus;
import model.CPU;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ppu.PPU;
import ppu.ShiftRegister;
import ppu.Sprite;

import java.io.IOException;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BusReaderTest {
    static Bus    expectedBus;
    static CPU    expectedCpu;
    static PPU    expectedPpu;
    static Mapper expectedMapper;

    static Bus    actualBus;
    static CPU    actualCpu;
    static PPU    actualPpu;
    static Mapper actualMapper;

    @BeforeAll
    static void runBeforeAll() {
        try {
            expectedBus = Bus.getInstance();
            expectedBus.loadCartridge(new File("./data/rom/nestest.nes"));
            expectedBus.getCpu().addBreakpoint(new Address(0xABCD));

            for (int i = 0; i < 10000; i++) {
                expectedBus.cycleComponents();
            }

            BusWriter.writeToFile(expectedBus, "test");

            actualBus = Bus.getInstance();
            actualBus.softReset();
            actualBus = BusReader.readFromFile("test");
        } catch (IOException e) {
            fail("IOException thrown! Are you sure the file exists?");
        }

        expectedCpu    = expectedBus.getCpu();
        expectedPpu    = expectedBus.getPpu();
        expectedMapper = expectedBus.getMapper();
        actualCpu      = actualBus.getCpu();
        actualPpu      = actualBus.getPpu();
        actualMapper   = actualBus.getMapper();
    }


    @AfterAll()
    static void runAfterAll() {
        new File("./data/save/test.sav").delete();
    }

    @Test
    void testReadFail() {
        try {
            Bus.hardReset();
            Bus bus = Bus.getInstance();
            bus.softReset();
            bus = BusReader.readFromFile("this/file/does/not.exist");
        } catch (Exception e) {
            fail("This should be handled by the BusReader!");
        }
    }

    @Test
    void testConstructor() {
        try {
            BusReader busReader = new BusReader();
        } catch (Exception e) {
            fail("Dummy constructor failed!");
        }
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
        for (int i = 0; i < 0x0800; i++) {
            assertAddressEquality(expectedCpu.readMemory(i), actualCpu.readMemory(i));
        }
    }

    @Test
    void testCpuState() {
        assertEquals(expectedCpu.getCycles(),      actualCpu.getCycles());
        for (int i = 0; i < expectedCpu.getBreakpoints().size(); i++) {
            assertAddressEquality(expectedCpu.getBreakpoints().get(i), actualCpu.getBreakpoints().get(i));
        }
    }

    @Test
    void testPpuLatches() {
        assertAddressEquality(expectedPpu.getLatchNametable(),        actualPpu.getLatchNametable());
        assertAddressEquality(expectedPpu.getLatchAttributeTable(),   actualPpu.getLatchAttributeTable());
        assertAddressEquality(expectedPpu.getLatchPatternTableLow(),  actualPpu.getLatchPatternTableLow());
        assertAddressEquality(expectedPpu.getLatchPatternTableHigh(), actualPpu.getLatchPatternTableHigh());
    }

    @Test
    void testPpuInternalRegisters() {
        assertAddressEquality(expectedPpu.getRegisterT(), actualPpu.getRegisterT());
        assertAddressEquality(expectedPpu.getRegisterX(), actualPpu.getRegisterX());
        assertAddressEquality(expectedPpu.getRegisterW(), actualPpu.getRegisterW());
    }

    @Test
    void testPpuShiftRegisters() {
        assertShiftRegisterEquality(expectedPpu.getShiftRegisterSmall0(), actualPpu.getShiftRegisterSmall0());
        assertShiftRegisterEquality(expectedPpu.getShiftRegisterSmall1(), actualPpu.getShiftRegisterSmall1());
        assertShiftRegisterEquality(expectedPpu.getShiftRegisterLarge0(), actualPpu.getShiftRegisterLarge0());
        assertShiftRegisterEquality(expectedPpu.getShiftRegisterLarge1(), actualPpu.getShiftRegisterLarge1());
    }

    @Test
    void testPpuRegisters() {
        assertAddressEquality(expectedPpu.getPpuCtrl(),       actualPpu.getPpuCtrl());
        assertAddressEquality(expectedPpu.getPpuMask(),       actualPpu.getPpuMask());
        assertAddressEquality(expectedPpu.getPpuStatus(),     actualPpu.getPpuStatus());
        assertAddressEquality(expectedPpu.getOamAddr(),       actualPpu.getOamAddr());
        assertAddressEquality(expectedPpu.getPpuScroll(),     actualPpu.getPpuScroll());
        assertAddressEquality(expectedPpu.getPpuData(),       actualPpu.getPpuData());
        assertAddressEquality(expectedPpu.getPpuDataBuffer(), actualPpu.getPpuDataBuffer());
    }

    @Test
    void testPpuNametables() {
        for (int i = 0; i < PPU.NAMETABLE_SIZE; i++) {
            assertAddressEquality(expectedPpu.getNametable()[i], actualPpu.getNametable()[i]);
        }
        assertEquals(expectedPpu.getNametableMirroring(), actualPpu.getNametableMirroring());
    }

    @Test
    void testPpuPaletteRamIndexes() {
        for (int i = 0; i < PPU.PALETTE_RAM_SIZE; i++) {
            int expected = expectedPpu.getPaletteRamIndexes().readMemory(i).getValue();
            int actual   = actualPpu.getPaletteRamIndexes().readMemory(i).getValue();
            assertEquals(expected, actual);
        }
    }

    @Test
    void testPpuOam() {
        for (int i = 0; i < PPU.PRIMARY_OAM_SIZE; i++) {
            assertAddressEquality(expectedPpu.getPrimaryOam()[i], actualPpu.getPrimaryOam()[i]);
        }
        for (int i = 0; i < PPU.SECONDARY_OAM_SIZE; i++) {
            assertAddressEquality(expectedPpu.getSecondaryOam()[i], actualPpu.getSecondaryOam()[i]);
        }
    }

    @Test
    void testPpuSprites() {
        for (int i = 0; i < 8; i++) {
            assertSpriteEquality(expectedPpu.getSprites()[i], actualPpu.getSprites()[i]);
        }
    }

    @Test
    void testPpuCyclingData() {
        assertEquals(expectedPpu.getCycle(),      actualPpu.getCycle());
        assertEquals(expectedPpu.getScanline(),   actualPpu.getScanline());
        assertEquals(expectedPpu.getDrawX(),      actualPpu.getDrawX());
        assertEquals(expectedPpu.getDrawY(),      actualPpu.getDrawY());
        assertEquals(expectedPpu.getIsOddFrame(), actualPpu.getIsOddFrame());
    }

    @Test
    void testMapper() {
        assertEquals(expectedMapper.getId(), actualMapper.getId());

        for (int i = 0x6000; i < 0xFFFF + 1; i++) {
            assertAddressEquality(expectedMapper.readMemoryCpu(i), actualMapper.readMemoryCpu(i));
        }

        assertEquals(expectedMapper.getChrRomSize(), actualMapper.getChrRomSize());
        for (int i = 0; i < expectedMapper.getChrRomSize(); i++) {
            assertAddressEquality(expectedMapper.readMemoryPpu(i), actualMapper.readMemoryPpu(i));
        }
    }

    void assertAddressEquality(Address expected, Address actual) {
        assertEquals(expected.getPointer(), actual.getPointer());
        assertEquals(expected.getValue(),   actual.getValue());
    }


    void assertShiftRegisterEquality(ShiftRegister expectedShiftRegister, ShiftRegister actualShiftRegisterLarge) {
        assertEquals(expectedShiftRegister.getValue(), actualShiftRegisterLarge.getValue());
    }

    void assertSpriteEquality(Sprite expectedSprite, Sprite actualSprite) {
        assertEquals(expectedSprite.getPriority(),              actualSprite.getPriority());
        assertEquals(expectedSprite.getNextColorAddressAsInt(), actualSprite.getNextColorAddressAsInt());
        assertEquals(expectedSprite.isActive(),                 actualSprite.isActive());
    }
}
