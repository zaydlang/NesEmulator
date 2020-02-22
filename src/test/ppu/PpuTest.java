package ppu;

import mapper.NRom;
import model.Address;
import mapper.NRom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PpuTest {
    PPU ppu;
    NRom nrom;

    @BeforeEach
    void runBefore() {
        nrom = new NRom();
        try {
            nrom.loadCartridge("test/TestLoadRomTrainerPresent.nes");
        } catch (Exception e) {
            fail();
        }
        this.ppu = new PPU(nrom);
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
}
