package model;

public class NES {
    private static CPU cpu;

    public static void main(String[] args) {
        cpu = new CPU();

        NRom rom = new NRom();
        rom.loadCartridge("./test/nestest.nes");
        cpu.setMapper(rom);

        cpu.cycle();
    }
}
