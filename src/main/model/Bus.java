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
import java.time.Instant;
import java.time.temporal.ChronoUnit;

// Class Bus:
//     Bus is a class that manages the CPU, PPU, controller, and mapper. Serves as a way for these four components
//     to communicate with each other.

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

    private boolean enabled;

    // MODIFIES: this
    // EFFECTS:  initializes the CPU and PPU and connects them to the bus (this).
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

    // MODIFIES: this
    // EFFECTS:  loads the cartridge into the mapper and resets the cpu and ppu
    public void loadCartridge(File file) throws IOException {
        readCartridge(file);
        cartridgeLoaded = true;

        reset();
    }

    // MODIFIES: cpu, ppu
    // EFFECTS:  resets the cpu and ppu.
    public void reset() {
        cpu.reset();
        ppu.reset();
    }

    // MODIFIES: mapper
    // EFFECTS:  reads the cartridge and sets the mapper's prgRom and chrRom according to the data read.
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

    // MODIFIES: cpu, ppu
    // EFFECTS:  cycles the ppu 3 times and the cpu 1 time.
    public void cycleComponents() {
        ppu.cycle();
        ppu.cycle();
        ppu.cycle();
        cpu.cycle();
    }





    // MODIFIES: ppu
    // EFFECTS:  reads the ppu at the given register and returns the value.
    public Address ppuRead(int pointer) {
        return ppu.readRegister(pointer);
    }

    // REQUIRES: mapper is not null, caller is CPU
    // EFFECTS:  reads the mapper at the given pointer and returns the value.
    public Address mapperReadCpu(int pointer) {
        try {
            return mapper.readMemoryCpu(pointer);
        } catch (NullPointerException e) {
            return new Address(0);
        }
    }

    // REQUIRES: mapper is not null, caller is PPU
    // EFFECTS:  reads the mapper at the given pointer and returns the value.
    public Address mapperReadPpu(int pointer) {
        try {
            return mapper.readMemoryPpu(pointer);
        } catch (NullPointerException e) {
            return new Address(0);
        }
    }

    // MODIFIES: controller
    // EFFECTS:  reads the address in controller and returns the value.
    public Address controllerRead(int pointer) {
        if (controllerConnected && pointer == Integer.parseInt("4016", 16)) {
            Address address = controller.poll();
            // System.out.println("POLLED: " + address);
            return address;
        } else {
            return new Address(0);
        }
    }

    // MODIFIES: ppu
    // EFFECTS:  writes the ppu register at the given pointer to the value.
    public void ppuWrite(int pointer, int value) {
        ppu.writeRegister(pointer, value);
    }

    // EFFECTS:  writes the mapper at the given pointer to the value.
    public void mapperWrite(int pointer, int value) {
        mapper.writeMemory(pointer, value);
    }

    // MODIFIES: controller
    // EFFECTS:  writes the controller at the given pointer to the value.
    public void controllerWrite(int pointer, int value) {
        if (controllerConnected && pointer == Integer.parseInt("4016", 16)) {
            // System.out.println("STROBE: " + value);
            controller.setPolling(value == 1);
        }
    }

    public void setNmi(boolean nmi) {
        cpu.nmi = nmi;
    }

    public PPU getPpu() {
        return ppu;
    }

    // MODIFIES: cpu, ppu, mapper, cartridgeLoaded
    // EFFECTS:  sets the cpu, ppu, and mapper to the given values and sets cartridgeLoaded to true
    public void reload(CPU cpu, PPU ppu, Mapper mapper) {
        this.cpu    = cpu;
        this.ppu    = ppu;
        this.mapper = mapper;

        cartridgeLoaded = true;
    }

    // MODIFIES: controller
    // EFFECTS:  connects the controller to the bus.
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
