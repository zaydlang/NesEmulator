package model;

import mapper.Mapper;
import mapper.NRom;
import ppu.Mirroring;
import ppu.PPU;
import ui.CpuFileOutput;
import ui.controller.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Bus {
    public static final int HEADER_SIZE           = 16;    // bytes
    public static final int TRAINER_SIZE          = 512;   // bytes
    public static final int PRG_ROM_SIZE          = 16384; // bytes
    public static final int CHR_ROM_SIZE          = 8192;  // bytes

    private CPU cpu;
    private PPU ppu;
    private Controller controller;
    private Mapper mapper;

    private boolean cartridgeLoaded;
    private boolean controllerConnected;

    private int trueCpuCycles;
    private int truePpuCycles;

    // TODO BUS:
    // TODO nmi flag
    // TODO reset when cartridge loaded
    // TODO controller


    private boolean enabled;

    public Bus() {
        cpu = new CPU(this);
        ppu = new PPU(this);

        cpu.setLoggingOutput(new CpuFileOutput());

        cartridgeLoaded     = false;
        controllerConnected = false;
        enabled             = true;
        trueCpuCycles       = 0;
        truePpuCycles       = 0;
    }

    public void loadCartridge(File file) throws IOException {
        readCartridge(file);

        cpu.reset();
        ppu.reset();
        cartridgeLoaded = true;
    }

    private void readCartridge(File file) throws IOException {
        // https://wiki.nesdev.com/w/index.php/INES
        // iNES file format:
        // SIZE            | DEVICE
        // 16 bytes        | Header
        // 0 or 512 bytes  | Trainer
        // 16384 * x bytes | PRG ROM data
        // 8192 * y bytes  | CHR ROM data
        FileInputStream fileInputStream = new FileInputStream(file);
        Address[] header = readFile(fileInputStream, 0, 0, HEADER_SIZE);

        boolean trainerPresent = Util.getNthBit(header[6].getValue(), 2) == 1;
        Address[] trainer = readFile(fileInputStream, 0, 0, trainerPresent ? TRAINER_SIZE : 0);

        Address[] prgRom = readFile(fileInputStream, 0, 8 * 4096,header[4].getValue() * PRG_ROM_SIZE);
        Address[] chrRom = readFile(fileInputStream, 0, 0, header[5].getValue() * CHR_ROM_SIZE);
        mapper = new NRom(prgRom, chrRom);
        ppu.setNametableMirroring(Mirroring.VERTICAL);
    }

    // REQUIRES: file has at least numBytes available, otherwise throws IOException.
    // MODIFIES: file now has numBytes less bytes available
    // EFFECTS: wrapper class for FileInputStream.read(). returns int[] result instead of bytes[] by reading
    //          numBytes from the file with the specified offset.
    public Address[] readFile(FileInputStream file, int offset, int pointerOffset, int numBytes) throws IOException {
        Address[] result = new Address[numBytes];
        for (int i = offset; i < offset + numBytes; i++) {
            result[i - offset] = new Address(file.read(), pointerOffset + i);
        }
        return result;
    }






    // REQUIRES: logFile is open.
    // MODIFIES: cpu, logfile
    // EFFECTS: cycles the cpu through one instruction, throws IOException if logfile has been closed.
    public void cycle() {
        if (!cartridgeLoaded || !enabled) {
            return;
        }

        /* uncomment for cycle check
        boolean doCheck = cpu.cyclesRemaining == 1;
        if (doCheck) {
            System.out.println(cpu.getCycles() + " " + ppu.getCycles());
            int actual   = ppu.getCycles();
            int expected = Integer.parseInt(scanner1.nextLine().trim());
            if (actual != expected) {
                int u = 3;
            }

            actual   = cpu.getCycles();
            expected = Integer.parseInt(scanner2.nextLine().trim());
            if (actual != expected) {
                int u = 3;
            }
        }*/

        cycleComponents();
    }

    public void cycleComponents() {
        ppu.cycle();
        ppu.cycle();
        ppu.cycle();
        cpu.cycle();
    }






    public Address ppuRead(int pointer) {
        return ppu.readRegister(pointer);
    }

    public Address mapperReadCpu(int pointer) {
        return mapper.readMemoryCpu(pointer);
    }

    public Address mapperReadPpu(int pointer) {
        return mapper.readMemoryPpu(pointer);
    }

    public Address controllerRead(int pointer) {
        if (controllerConnected && pointer == Integer.parseInt("4016", 16)) {
            Address address = controller.poll();
            System.out.println("POLLED: " + address);
            return address;
        } else {
            return new Address(0);
        }
    }

    public void ppuWrite(int pointer, int value) {
        ppu.writeRegister(pointer, value);
    }

    public void mapperWrite(int pointer, int value) {
        mapper.writeMemory(pointer, value);
    }

    public void controllerWrite(int pointer, int value) {
        if (controllerConnected && pointer == Integer.parseInt("4016", 16)) {
            System.out.println("STROBE: " + value);
            controller.setPolling(value == 1);
        }
    }

    public void setNmi(boolean nmi) {
        cpu.nmi = nmi;
    }

    public PPU getPpu() {
        return ppu;
    }

    public void setController(Controller controller) {
        this.controller = controller;
        controllerConnected = true;
    }

    public CPU getCpu() {
        return cpu;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public Controller getController() {
        return controller;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public boolean getCartridgeLoaded() {
        return cartridgeLoaded;
    }

    public boolean getControllerConnected() {
        return controllerConnected;
    }

    public void ppuDma(int value) {
        ppu.writeOam(value);
    }
}
