package ppu;

import mapper.NRom;
import model.Address;
import mapper.NRom;
import model.Bus;
import model.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import ui.Pixels;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class PpuTest {
    Bus bus;
    PPU ppu;

    @BeforeEach
    void runBefore() {
        bus = new Bus();
        try {
            bus.loadCartridge(new File("./data/test/TestLoadRomTrainerPresent.nes"));
        } catch (IOException e) {
            fail("Bus failed to load cartridge!");
        }
        this.ppu = bus.getPpu();
    }

    @Test
    void testConstructor() {
        assertEquals(0, ppu.ppuData.getValue());
        assertEquals(0, ppu.ppuStatus.getValue());
        assertEquals(0, ppu.getRegisterT().getValue());
        assertEquals(0, ppu.getRegisterV().getValue());
        assertEquals(0, ppu.getRegisterX().getValue());
        assertEquals(0, ppu.getRegisterW().getValue());
    }

    @Test
    void testReset() {
        ppu.setRegisterT(1);
        ppu.setRegisterV(1);
        ppu.setRegisterX(1);
        ppu.setRegisterW(1);
        ppu.reset();

        assertEquals(0, ppu.getRegisterT().getValue());
        assertEquals(0, ppu.getRegisterV().getValue());
        assertEquals(0, ppu.getRegisterX().getValue());
        assertEquals(0, ppu.getRegisterW().getValue());
    }

    @Test
    void testCycle() {
        ppu.writeRegister(Integer.parseInt("2000", 16), Integer.parseInt("10000000", 2));
        ppu.writeRegister(Integer.parseInt("2001", 16), Integer.parseInt("00001000", 2));
        ppu.setPixels(new Pixels(1, 1, 256, 240));

        ppu.writeOam(Integer.parseInt("00000000", 2));
        ppu.writeOam(Integer.parseInt("00000000", 2));
        ppu.writeOam(Integer.parseInt("00100000", 2));
        ppu.writeOam(Integer.parseInt("00000000", 2));

        for (int frame = 0; frame < 2; frame++) {
            for (int scanline = -1; scanline < 261; scanline++) {
                if (frame == 0 && scanline == -1) {
                    scanline = 0;
                }

                for (int cycle = 0; cycle < 341; cycle++) {
                    if (frame % 2 != 1 || scanline != -1 || cycle != 1) {
                        assertEquals(scanline, ppu.scanline);
                        assertEquals(cycle,    ppu.cycle);
                        ppu.cycle();
                    }
                }
            }
        }
    }

    @Test
    void test$2000Write() {
        ppu.setRegisterT(Integer.parseInt("111111111111111", 2));

        ppu.writeRegister(Integer.parseInt("2000", 16), Integer.parseInt("11100100", 2));
        assertTrue(ppu.getRegisterT().getValue() == Integer.parseInt("111001111111111", 2));
    }

    @Test
    void test$2002Read() {
        ppu.setRegisterW(1);

        ppu.readRegister(Integer.parseInt("2002", 16));
        assertTrue(ppu.getRegisterW().getValue() == 0);
    }

    @Test
    void test$2005WriteFirst() {
        ppu.setRegisterT(Integer.parseInt("111111111111111", 2));
        ppu.setRegisterX(Integer.parseInt("111",             2));
        ppu.setRegisterW(0);

        ppu.writeRegister(Integer.parseInt("2005", 16), Integer.parseInt("00000000",2));
        assertTrue(ppu.getRegisterT().getValue() == Integer.parseInt("111111111100000", 2));
        assertTrue(ppu.getRegisterX().getValue() == Integer.parseInt("000",             2));
        assertTrue(ppu.getRegisterW().getValue() == Integer.parseInt("1",               2));
    }

    @Test
    void test$2005WriteSecond() {
        ppu.setRegisterT(Integer.parseInt("111111111111111", 2));
        ppu.setRegisterX(Integer.parseInt("111",             2));
        ppu.setRegisterW(1);

        ppu.writeRegister(Integer.parseInt("2005", 16), Integer.parseInt("00000000",2));
        assertTrue(ppu.getRegisterT().getValue() == Integer.parseInt("000110000011111", 2));
        assertTrue(ppu.getRegisterW().getValue() == Integer.parseInt("0",               2));
    }

    @Test
    void test$2006WriteFirst() {
        ppu.setRegisterT(Integer.parseInt("111111111111111", 2));
        ppu.setRegisterX(Integer.parseInt("111",             2));
        ppu.setRegisterW(0);

        ppu.writeRegister(Integer.parseInt("2006", 16), Integer.parseInt("00000000",2));
        assertTrue(ppu.getRegisterT().getValue() == Integer.parseInt("000000011111111", 2));
        assertTrue(ppu.getRegisterW().getValue() == Integer.parseInt("1",               2));
    }

    @Test
    void test$2006WriteSecond() {
        ppu.setRegisterT(Integer.parseInt("111111111111111", 2));
        ppu.setRegisterV(Integer.parseInt("111111111111111", 2));
        ppu.setRegisterX(Integer.parseInt("111",             2));
        ppu.setRegisterW(1);

        ppu.writeRegister(Integer.parseInt("2006", 16), Integer.parseInt("00000000",2));
        assertTrue(ppu.getRegisterT().getValue() == Integer.parseInt("111111100000000", 2));
        assertTrue(ppu.getRegisterV().getValue() == Integer.parseInt("111111100000000", 2));
        assertTrue(ppu.getRegisterW().getValue() == Integer.parseInt("0",               2));
    }

    @Test
    void test$2007ReadIncrement1() {
        ppu.writeRegister(Integer.parseInt("2000", 16), Integer.parseInt("00000000", 2));
        ppu.setRegisterV(0);
        ppu.readRegister(Integer.parseInt("2007", 16));

        assertEquals(1, ppu.getRegisterV().getValue());
    }

    @Test
    void test$2007ReadIncrement32() {
        ppu.writeRegister(Integer.parseInt("2000", 16), Integer.parseInt("11111111", 2));
        ppu.setRegisterV(0);
        ppu.readRegister(Integer.parseInt("2007", 16));

        assertEquals(32, ppu.getRegisterV().getValue());
    }

    @Test
    void test$2007WriteIncrement1() {
        ppu.writeRegister(Integer.parseInt("2000", 16), Integer.parseInt("00000000", 2));
        ppu.setRegisterV(0);
        ppu.writeRegister(Integer.parseInt("2007", 16), 0);

        assertEquals(1, ppu.getRegisterV().getValue());
    }

    @Test
    void test$2007WriteIncrement32() {
        ppu.writeRegister(Integer.parseInt("2000", 16), Integer.parseInt("11111111", 2));
        ppu.setRegisterV(0);
        ppu.writeRegister(Integer.parseInt("2007", 16), 0);

        assertEquals(32, ppu.getRegisterV().getValue());
    }

    @Test
    void test$2007ReadImmediateReturn() {
        ppu.writeRegister(Integer.parseInt("2006", 16), Integer.parseInt("3F", 16));
        ppu.writeRegister(Integer.parseInt("2006", 16), Integer.parseInt("00", 16));
        ppu.writeMemory(Integer.parseInt("3F00", 16), Integer.parseInt("A2", 16));
        int actual = ppu.readRegister(Integer.parseInt("2007", 16)).getValue();

        assertEquals(Integer.parseInt("A2", 16), actual);
    }

    @Test
    void testRenderNametables() {
        Pixels pixels = new Pixels(1, 1, 256 * 2, 256 * 2);
        ppu.writeMemory(Integer.parseInt("2000", 16), Integer.parseInt("00",       16));
        ppu.writeMemory(Integer.parseInt("0000", 16), Integer.parseInt("01010101", 2));
        ppu.writeMemory(Integer.parseInt("3F00", 16), Integer.parseInt("30",       16));
        ppu.writeMemory(Integer.parseInt("3F01", 16), Integer.parseInt("31",       16));
        ppu.writeMemory(Integer.parseInt("3F02", 16), Integer.parseInt("32",       16));
        ppu.writeMemory(Integer.parseInt("3F03", 16), Integer.parseInt("33",       16));

        ppu.renderNameTables(pixels, 0);
        assertEquals(new Color(212, 178, 236), pixels.getPixel(0,   0));
        assertEquals(new Color(212, 178, 236), pixels.getPixel(0,   96));
        assertEquals(new Color(0,   0,   0),   pixels.getPixel(511, 511));
    }

    @Test
    void testRenderPatterntables() {
        Pixels pixels = new Pixels(1, 1, 256 * 2, 256 * 2);
        ppu.writeMemory(Integer.parseInt("2000", 16), Integer.parseInt("00",       16));
        ppu.writeMemory(Integer.parseInt("0000", 16), Integer.parseInt("01010101", 2));
        ppu.writeMemory(Integer.parseInt("3F00", 16), Integer.parseInt("30",       16));
        ppu.writeMemory(Integer.parseInt("3F01", 16), Integer.parseInt("31",       16));
        ppu.writeMemory(Integer.parseInt("3F02", 16), Integer.parseInt("32",       16));
        ppu.writeMemory(Integer.parseInt("3F03", 16), Integer.parseInt("33",       16));
        ppu.writeMemory(Integer.parseInt("3F05", 16), Integer.parseInt("34",       16));
        // Note: 0x3F05 should not be rendered.

        ppu.renderPatternTables(pixels, 0);
        assertEquals(new Color(212, 178, 236), pixels.getPixel(0,   0));
        assertEquals(new Color(188, 188, 236), pixels.getPixel(0,   96));
        assertEquals(new Color(0,   0,   0),   pixels.getPixel(511, 511));
    }

    @Test
    void testRenderOAM() {
        ppu.writeOam(Integer.parseInt("00000000", 2));
        ppu.writeOam(Integer.parseInt("00000000", 2));
        ppu.writeOam(Integer.parseInt("00100000", 2));
        ppu.writeOam(Integer.parseInt("00000000", 2));
        ppu.writeMemory(Integer.parseInt("2000", 16), Integer.parseInt("00",       16));
        ppu.writeMemory(Integer.parseInt("0000", 16), Integer.parseInt("01010101", 2));
        ppu.writeMemory(Integer.parseInt("3F00", 16), Integer.parseInt("30",       16));
        ppu.writeMemory(Integer.parseInt("3F01", 16), Integer.parseInt("31",       16));
        ppu.writeMemory(Integer.parseInt("3F02", 16), Integer.parseInt("32",       16));
        ppu.writeMemory(Integer.parseInt("3F03", 16), Integer.parseInt("33",       16));

        Pixels pixels = new Pixels(1, 1, 256, 256);
        ppu.renderOAM(pixels, 1, 1);

        assertEquals(new Color(84,  84,  84),  pixels.getPixel(0, 0));
        assertEquals(new Color(236, 238, 236), pixels.getPixel(0, 4));
        assertEquals(new Color(236, 238, 236), pixels.getPixel(0, 33));
        assertEquals(new Color(0,   0,   0),   pixels.getPixel(0, 64));
    }

    @Test
    void testIncrementCoarseXOverflow() {
        for (int i = 0; i < 32; i++) {
            int coarseX = (ppu.getRegisterV().getValue() & Integer.parseInt("000000000011111", 2)) >> 0;
            assertEquals(i, coarseX);
            ppu.incrementCoarseX();
        }

        int coarseX = (ppu.getRegisterV().getValue() & Integer.parseInt("000000000011111", 2)) >> 0;
        assertEquals(0, coarseX);
    }

    @Test
    void testIncrementCoarseYOverflow() {
        for (int i = 0; i < 32; i++) {
            int coarseY = (ppu.getRegisterV().getValue() & Integer.parseInt("000001111100000", 2)) >> 5;
            assertEquals(i, coarseY);
            ppu.incrementCoarseY();
        }

        int coarseY = (ppu.getRegisterV().getValue() & Integer.parseInt("000001111100000", 2)) >> 5;
        assertEquals(0, coarseY);
    }

    @Test
    void testRenderSpritesOverflow() {
        int overflowFlag;

        overflowFlag = Util.getNthBit(ppu.readRegister(Integer.parseInt("2002", 16)).getValue(), 5);
        assertEquals(0, overflowFlag);

        for (int i = 0; i < 9; i++) {
            ppu.writeOam(Integer.parseInt("00000000", 2));
            ppu.writeOam(Integer.parseInt("00000000", 2));
            ppu.writeOam(Integer.parseInt("00100000", 2));
            ppu.writeOam(Integer.parseInt("00000000", 2));
        }
        ppu.evaluateSprites();

        overflowFlag = Util.getNthBit(ppu.readRegister(Integer.parseInt("2002", 16)).getValue(), 5);
        assertEquals(1, overflowFlag);
    }

    @Test
    void testPpuReadMemoryMapper() {
        assertSame(bus.mapperReadPpu(Integer.parseInt("0000", 16)), ppu.readMemory(Integer.parseInt("0000", 16)));
        assertSame(bus.mapperReadPpu(Integer.parseInt("03A3", 16)), ppu.readMemory(Integer.parseInt("03A3", 16)));
        assertSame(bus.mapperReadPpu(Integer.parseInt("089F", 16)), ppu.readMemory(Integer.parseInt("089F", 16)));
        assertSame(bus.mapperReadPpu(Integer.parseInt("103B", 16)), ppu.readMemory(Integer.parseInt("103B", 16)));
        assertSame(bus.mapperReadPpu(Integer.parseInt("1FFF", 16)), ppu.readMemory(Integer.parseInt("1FFF", 16)));
    }

    @Test
    void testPpuReadMemoryNametable() {
        assertSame(ppu.readNametable(Integer.parseInt("2000", 16)), ppu.readMemory(Integer.parseInt("2000", 16)));
        assertSame(ppu.readNametable(Integer.parseInt("23A3", 16)), ppu.readMemory(Integer.parseInt("23A3", 16)));
        assertSame(ppu.readNametable(Integer.parseInt("289F", 16)), ppu.readMemory(Integer.parseInt("289F", 16)));
        assertSame(ppu.readNametable(Integer.parseInt("2C3B", 16)), ppu.readMemory(Integer.parseInt("2C3B", 16)));
        assertSame(ppu.readNametable(Integer.parseInt("2FFF", 16)), ppu.readMemory(Integer.parseInt("2FFF", 16)));
    }

    @Test
    void testPpuReadMemoryNametableMirrors() {
        assertSame(ppu.readNametable(Integer.parseInt("2000", 16)), ppu.readMemory(Integer.parseInt("3000", 16)));
        assertSame(ppu.readNametable(Integer.parseInt("23A3", 16)), ppu.readMemory(Integer.parseInt("33A3", 16)));
        assertSame(ppu.readNametable(Integer.parseInt("289F", 16)), ppu.readMemory(Integer.parseInt("389F", 16)));
        assertSame(ppu.readNametable(Integer.parseInt("2C3B", 16)), ppu.readMemory(Integer.parseInt("3C3B", 16)));
    }

    @Test
    void testPpuReadMemoryPaletteRamIndexes() {
        PaletteRamIndexes p = ppu.paletteRamIndexes;
        assertSame(p.readMemory(Integer.parseInt("00", 16)), ppu.readMemory(Integer.parseInt("3F00", 16)));
        assertSame(p.readMemory(Integer.parseInt("03", 16)), ppu.readMemory(Integer.parseInt("3F03", 16)));
        assertSame(p.readMemory(Integer.parseInt("20", 16)), ppu.readMemory(Integer.parseInt("3F20", 16)));
    }

    @Test
    void testPpuWriteMemoryNametable() {
        ppu.setNametableMirroring(Mirroring.HORIZONTAL);
        ppu.writeMemory(Integer.parseInt("2000", 16), Integer.parseInt("A2", 16));
        assertEquals(Integer.parseInt("A2", 16), ppu.readNametable(Integer.parseInt("2000", 16)).getValue());
        ppu.writeMemory(Integer.parseInt("2ABC", 16), Integer.parseInt("A2", 16));
        assertEquals(Integer.parseInt("A2", 16), ppu.readNametable(Integer.parseInt("2ABC", 16)).getValue());
        ppu.writeMemory(Integer.parseInt("2FFF", 16), Integer.parseInt("A2", 16));
        assertEquals(Integer.parseInt("A2", 16), ppu.readNametable(Integer.parseInt("2FFF", 16)).getValue());
    }

    @Test
    void testPpuWriteMemoryNametableMirrors() {
        ppu.writeMemory(Integer.parseInt("3000", 16), Integer.parseInt("A2", 16));
        assertEquals(Integer.parseInt("A2", 16), ppu.readNametable(Integer.parseInt("3000", 16)).getValue());
        ppu.writeMemory(Integer.parseInt("3ABC", 16), Integer.parseInt("A2", 16));
        assertEquals(Integer.parseInt("A2", 16), ppu.readNametable(Integer.parseInt("3ABC", 16)).getValue());
    }
}
