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
        Bus.hardReset();
        bus = Bus.getInstance();
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
        ppu.writeRegister(0x2000, 0b10000000);
        ppu.writeRegister(0x2001, 0b00001000);
        ppu.setPixels(new Pixels(1, 1, 256, 240));

        ppu.writeOam(0b00000000);
        ppu.writeOam(0b00000000);
        ppu.writeOam(0b00100000);
        ppu.writeOam(0b00000000);

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
        ppu.setRegisterT(0b111111111111111);

        ppu.writeRegister(0x2000, 0b11100100);
        assertTrue(ppu.getRegisterT().getValue() == 0b111001111111111);
    }

    @Test
    void test$2002Read() {
        ppu.setRegisterW(1);

        ppu.readRegister(0x2002);
        assertTrue(ppu.getRegisterW().getValue() == 0);
    }

    @Test
    void test$2005WriteFirst() {
        ppu.setRegisterT(0b111111111111111);
        ppu.setRegisterX(0b111);
        ppu.setRegisterW(0);

        ppu.writeRegister(0x2005, 0b00000000);
        assertTrue(ppu.getRegisterT().getValue() == 0b111111111100000);
        assertTrue(ppu.getRegisterX().getValue() == 0b000);
        assertTrue(ppu.getRegisterW().getValue() == 0b1);
    }

    @Test
    void test$2005WriteSecond() {
        ppu.setRegisterT(0b111111111111111);
        ppu.setRegisterX(0b111);
        ppu.setRegisterW(1);

        ppu.writeRegister(0x2005, 0b00000000);
        assertTrue(ppu.getRegisterT().getValue() == 0b000110000011111);
        assertTrue(ppu.getRegisterW().getValue() == 0b0);
    }

    @Test
    void test$2006WriteFirst() {
        ppu.setRegisterT(0b111111111111111);
        ppu.setRegisterX(0b111);
        ppu.setRegisterW(0);

        ppu.writeRegister(0x2006, 0b00000000);
        assertTrue(ppu.getRegisterT().getValue() == 0b000000011111111);
        assertTrue(ppu.getRegisterW().getValue() == 0b1);
    }

    @Test
    void test$2006WriteSecond() {
        ppu.setRegisterT(0b111111111111111);
        ppu.setRegisterV(0b111111111111111);
        ppu.setRegisterX(0b111);
        ppu.setRegisterW(1);

        ppu.writeRegister(0x2006, 0b00000000);
        assertTrue(ppu.getRegisterT().getValue() == 0b111111100000000);
        assertTrue(ppu.getRegisterV().getValue() == 0b111111100000000);
        assertTrue(ppu.getRegisterW().getValue() == 0b0);
    }

    @Test
    void test$2007ReadIncrement1() {
        ppu.writeRegister(0x2000, 0b00000000);
        ppu.setRegisterV(0);
        ppu.readRegister(0x2007);

        assertEquals(1, ppu.getRegisterV().getValue());
    }

    @Test
    void test$2007ReadIncrement32() {
        ppu.writeRegister(0x2000, 0b11111111);
        ppu.setRegisterV(0);
        ppu.readRegister(0x2007);

        assertEquals(32, ppu.getRegisterV().getValue());
    }

    @Test
    void test$2007WriteIncrement1() {
        ppu.writeRegister(0x2000, 0b00000000);
        ppu.setRegisterV(0);
        ppu.writeRegister(0x2007, 0);

        assertEquals(1, ppu.getRegisterV().getValue());
    }

    @Test
    void test$2007WriteIncrement32() {
        ppu.writeRegister(0x2000, 0b11111111);
        ppu.setRegisterV(0);
        ppu.writeRegister(0x2007, 0);

        assertEquals(32, ppu.getRegisterV().getValue());
    }

    @Test
    void test$2007ReadImmediateReturn() {
        ppu.writeRegister(0x2006, 0x3F);
        ppu.writeRegister(0x2006, 0x00);
        ppu.writeMemory(0x3F00, 0xA2);
        int actual = ppu.readRegister(0x2007).getValue();

        assertEquals(0xA2, actual);
    }

    @Test
    void testRenderNametables() {
        Pixels pixels = new Pixels(1, 1, 256 * 2, 256 * 2);
        ppu.writeMemory(0x2000, 0x00);
        ppu.writeMemory(0x0000, 0b01010101);
        ppu.writeMemory(0x3F00, 0x30);
        ppu.writeMemory(0x3F01, 0x31);
        ppu.writeMemory(0x3F02, 0x32);
        ppu.writeMemory(0x3F03, 0x33);

        ppu.renderNameTables(pixels, 0);
        assertEquals(new Color(212, 178, 236), pixels.getPixel(0,   0));
        assertEquals(new Color(212, 178, 236), pixels.getPixel(0,   96));
        assertEquals(new Color(0,   0,   0),   pixels.getPixel(511, 511));
    }

    @Test
    void testRenderPatterntables() {
        Pixels pixels = new Pixels(1, 1, 256 * 2, 256 * 2);
        ppu.writeMemory(0x2000, 0x00);
        ppu.writeMemory(0x0000, 0b01010101);
        ppu.writeMemory(0x3F00, 0x30);
        ppu.writeMemory(0x3F01, 0x31);
        ppu.writeMemory(0x3F02, 0x32);
        ppu.writeMemory(0x3F03, 0x33);
        ppu.writeMemory(0x3F05, 0x34);
        // Note: 0x3F05 should not be rendered.

        ppu.renderPatternTables(pixels, 0);
        assertEquals(new Color(212, 178, 236), pixels.getPixel(0,   0));
        assertEquals(new Color(188, 188, 236), pixels.getPixel(0,   96));
        assertEquals(new Color(0,   0,   0),   pixels.getPixel(511, 511));
    }

    @Test
    void testRenderOAM() {
        ppu.writeOam(0b00000000);
        ppu.writeOam(0b00000000);
        ppu.writeOam(0b00100000);
        ppu.writeOam(0b00000000);
        ppu.writeMemory(0x2000, 0x00);
        ppu.writeMemory(0x0000, 0b01010101);
        ppu.writeMemory(0x3F00, 0x30);
        ppu.writeMemory(0x3F01, 0x31);
        ppu.writeMemory(0x3F02, 0x32);
        ppu.writeMemory(0x3F03, 0x33);

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
            int coarseX = (ppu.getRegisterV().getValue() & 0b000000000011111) >> 0;
            assertEquals(i, coarseX);
            ppu.incrementCoarseX();
        }

        int coarseX = (ppu.getRegisterV().getValue() & 0b000000000011111) >> 0;
        assertEquals(0, coarseX);
    }

    @Test
    void testIncrementCoarseYOverflow() {
        for (int i = 0; i < 32; i++) {
            int coarseY = (ppu.getRegisterV().getValue() & 0b000001111100000) >> 5;
            assertEquals(i, coarseY);
            ppu.incrementCoarseY();
        }

        int coarseY = (ppu.getRegisterV().getValue() & 0b000001111100000) >> 5;
        assertEquals(0, coarseY);
    }

    @Test
    void testRenderSpritesOverflow() {
        int overflowFlag;

        overflowFlag = Util.getNthBit(ppu.readRegister(0x2002).getValue(), 5);
        assertEquals(0, overflowFlag);

        for (int i = 0; i < 9; i++) {
            ppu.writeOam(0b00000000);
            ppu.writeOam(0b00000000);
            ppu.writeOam(0b00100000);
            ppu.writeOam(0b00000000);
        }
        ppu.evaluateSprites();

        overflowFlag = Util.getNthBit(ppu.readRegister(0x2002).getValue(), 5);
        assertEquals(1, overflowFlag);
    }

    @Test
    void testPpuReadMemoryMapper() {
        assertSame(bus.mapperReadPpu(0x0000), ppu.readMemory(0x0000));
        assertSame(bus.mapperReadPpu(0x03A3), ppu.readMemory(0x03A3));
        assertSame(bus.mapperReadPpu(0x089F), ppu.readMemory(0x089F));
        assertSame(bus.mapperReadPpu(0x103B), ppu.readMemory(0x103B));
        assertSame(bus.mapperReadPpu(0x1FFF), ppu.readMemory(0x1FFF));
    }

    @Test
    void testPpuReadMemoryNametable() {
        assertSame(ppu.readNametable(0x2000), ppu.readMemory(0x2000));
        assertSame(ppu.readNametable(0x23A3), ppu.readMemory(0x23A3));
        assertSame(ppu.readNametable(0x289F), ppu.readMemory(0x289F));
        assertSame(ppu.readNametable(0x2C3B), ppu.readMemory(0x2C3B));
        assertSame(ppu.readNametable(0x2FFF), ppu.readMemory(0x2FFF));
    }

    @Test
    void testPpuReadMemoryNametableMirrors() {
        assertSame(ppu.readNametable(0x2000), ppu.readMemory(0x3000));
        assertSame(ppu.readNametable(0x23A3), ppu.readMemory(0x33A3));
        assertSame(ppu.readNametable(0x289F), ppu.readMemory(0x389F));
        assertSame(ppu.readNametable(0x2C3B), ppu.readMemory(0x3C3B));
    }

    @Test
    void testPpuReadMemoryPaletteRamIndexes() {
        PaletteRamIndexes p = ppu.paletteRamIndexes;
        assertSame(p.readMemory(0x00), ppu.readMemory(0x3F00));
        assertSame(p.readMemory(0x03), ppu.readMemory(0x3F03));
        assertSame(p.readMemory(0x20), ppu.readMemory(0x3F20));
    }

    @Test
    void testPpuWriteMemoryNametable() {
        ppu.setNametableMirroring(Mirroring.HORIZONTAL);
        ppu.writeMemory(0x2000, 0xA2);
        assertEquals(0xA2, ppu.readNametable(0x2000).getValue());
        ppu.writeMemory(0x2ABC, 0xA2);
        assertEquals(0xA2, ppu.readNametable(0x2ABC).getValue());
        ppu.writeMemory(0x2FFF, 0xA2);
        assertEquals(0xA2, ppu.readNametable(0x2FFF).getValue());
    }

    @Test
    void testPpuWriteMemoryNametableMirrors() {
        ppu.writeMemory(0x3000, 0xA2);
        assertEquals(0xA2, ppu.readNametable(0x3000).getValue());
        ppu.writeMemory(0x3ABC, 0xA2);
        assertEquals(0xA2, ppu.readNametable(0x3ABC).getValue());
    }
}
