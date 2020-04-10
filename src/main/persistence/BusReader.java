package persistence;

import mapper.Mapper;
import model.*;
import ppu.Mirroring;
import ppu.PPU;
import ppu.Sprite;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.io.IOException;

// Class BusReader:
//     BusReader is a class that can read from a save file and modify a Bus' state to match the contents of that
//     save file. Essentially allows the user to reload from a savestate.

public class BusReader {
    // Constants
    private static final String SAVE_DIRECTORY = BusWriter.SAVE_DIRECTORY;
    private static final String DELIMITER      = BusWriter.DELIMITER;
    private static final String EXTENSION      = BusWriter.EXTENSION;

    // EFFECTS: makes the code coverage autobot not complain about me not instantiating a NesReader class.
    public BusReader() {

    }

    // REQUIRES: fileName.EXTENSION exists in SAVE_DIRECTORY
    // EFFECTS: saves the NES' state to a file at filename. Throws IOException if the file could not be read from.
    public static Bus readFromFile(String fileName) {
        Bus bus = new Bus();
        bus.reset();

        try {
            Scanner scanner = new Scanner(new File(SAVE_DIRECTORY + fileName + EXTENSION)).useDelimiter(DELIMITER);

            CPU cpu       = readCpu(bus.getCpu(), scanner);
            PPU ppu       = readPpu(bus.getPpu(), scanner);
            Mapper mapper = readMapper(bus.getMapper(), scanner);
            bus.reload(cpu, ppu, mapper);
        } catch (IOException e) {
            // Do nothing; failed to load!
        }

        return bus;
    }

    // EFFECTS: reads the scanner to set the CPU's state
    private static CPU readCpu(CPU cpu, Scanner scanner) {
        readCpuRegisters(cpu, scanner);
        readCpuFlags(cpu, scanner);
        readCpuRam(cpu, scanner);
        readCpuState(cpu, scanner);
        return cpu;
    }

    // REQUIRES: scanner has at least 10 delimited integers
    // EFFECTS: reads the scanner to set the CPU's registers
    private static void readCpuRegisters(CPU cpu, Scanner scanner) {
        cpu.setRegisterA(getNextAddress(scanner).getValue());
        cpu.setRegisterX(getNextAddress(scanner).getValue());
        cpu.setRegisterY(getNextAddress(scanner).getValue());
        cpu.setRegisterPC(getNextAddress(scanner).getValue());
        cpu.setRegisterS(getNextAddress(scanner).getValue());
    }

    // REQUIRES: scanner has at least 1 delimited integer.
    // EFFECTS: reads the scanner to set the CPU's flags
    private static void readCpuFlags(CPU cpu, Scanner scanner) {
        cpu.setStatus(Integer.parseInt(scanner.next()));
    }

    // REQUIRES: scanner has at least 0x800 * 2 (4096) delimited integers.
    // EFFECTS: reads the scanner to set the CPU's ram
    private static void readCpuRam(CPU cpu, Scanner scanner) {
        for (int i = 0; i < 0x0800; i++) {
            cpu.writeMemory(i, getNextAddress(scanner).getValue());
        }
    }

    // REQUIRES: the scanner has at least 2n + 1 delimited integers, where n is the first delimited integer
    //           in the scanner.
    // EFFECTS: reads the scanner to set the CPU's state. (cycles and breakpoints)
    private static void readCpuState(CPU cpu, Scanner scanner) {
        cpu.setCycles(Integer.parseInt(scanner.next()));

        int numBreakpoints = Integer.parseInt(scanner.next());
        for (int i = 0; i < numBreakpoints; i++) {
            cpu.addBreakpoint(getNextAddress(scanner));
        }
    }

    // EFFECTS: reads the scanner to set the PPU's state
    private static PPU readPpu(PPU ppu, Scanner scanner) throws IOException {
        readPpuLatches(ppu, scanner);
        readPpuInternalRegisters(ppu, scanner);
        readPpuShiftRegisters(ppu, scanner);
        readPpuRegisters(ppu, scanner);
        readPpuNametables(ppu, scanner);
        readPpuPaletteRamIndexes(ppu, scanner);
        readPpuOam(ppu, scanner);
        readPpuSprites(ppu, scanner);
        readPpuCyclingData(ppu, scanner);
        return ppu;
    }

    // REQUIRES: scanner has at least 4 * 2 delimited integers
    // EFFECTS: reads the scanner to set the PPU's latches
    private static void readPpuLatches(PPU ppu, Scanner scanner) throws IOException {
        readSerializable(ppu.getLatchNametable(), scanner);
        readSerializable(ppu.getLatchAttributeTable(), scanner);
        readSerializable(ppu.getLatchPatternTableLow(), scanner);
        readSerializable(ppu.getLatchPatternTableHigh(), scanner);
    }

    // REQUIRES: scanner has at least 4 * 2 delimited integers
    // EFFECTS: reads the scanner to set the PPU's internal registers
    private static void readPpuInternalRegisters(PPU ppu, Scanner scanner) throws IOException {
        readSerializable(ppu.getRegisterT(), scanner);
        readSerializable(ppu.getRegisterV(), scanner);
        readSerializable(ppu.getRegisterX(), scanner);
        readSerializable(ppu.getRegisterW(), scanner);
    }

    // REQUIRES: scanner has at least 4 * 2 delimited integers.
    // EFFECTS: reads the scanner to set the PPU's shift registers
    private static void readPpuShiftRegisters(PPU ppu, Scanner scanner) throws IOException {
        readSerializable(ppu.getShiftRegisterSmall0(), scanner);
        readSerializable(ppu.getShiftRegisterSmall1(), scanner);
        readSerializable(ppu.getShiftRegisterLarge0(), scanner);
        readSerializable(ppu.getShiftRegisterLarge1(), scanner);
    }

    // REQUIRES: scanner has at least 7 * 2 delimited integers.
    // EFFECTS: reads the scanner to set the PPU's registers
    private static void readPpuRegisters(PPU ppu, Scanner scanner) throws IOException {
        readSerializable(ppu.peekPpuCtrl(), scanner);
        readSerializable(ppu.peekPpuMask(), scanner);
        readSerializable(ppu.peekPpuStatus(), scanner);
        readSerializable(ppu.peekOamAddr(), scanner);
        readSerializable(ppu.peekPpuScroll(), scanner);
        readSerializable(ppu.peekPpuData(), scanner);
        readSerializable(ppu.peekPpuDataBuffer(), scanner);
    }

    // REQUIRES: scanner has at least 1 + 0x400 * 2 + 1 delimited integers.
    // EFFECTS: reads the scanner to set the PPU's nametables
    private static void readPpuNametables(PPU ppu, Scanner scanner) throws IOException {
        int length = Integer.parseInt(scanner.next());
        for (int i = 0; i < length; i++) {
            readSerializable(ppu.getNametable()[i], scanner);
        }

        ppu.setNametableMirroring(Mirroring.valueOf(scanner.next()));
    }

    // REQUIRES: scanner has at least 0x0020 * 2 + 1 delimited integers.
    // EFFECTS: reads the scanner to set the PPU's palette ram indexes
    private static void readPpuPaletteRamIndexes(PPU ppu, Scanner scanner) throws IOException {
        int length = Integer.parseInt(scanner.next());
        for (int i = 0; i < length; i++) {
            ppu.getPaletteRamIndexes().getIndexes()[i].deserialize(scanner);
        }
    }

    // REQUIRES: scanner has at least 0x100 * 2 + 1 + 0x020 * 2 + 1 delimited integers.
    // EFFECTS: reads the scanner to set the PPU's primary OAM and secondary OAM.
    private static void readPpuOam(PPU ppu, Scanner scanner) throws IOException {
        int primaryOamLength = Integer.parseInt(scanner.next());
        for (int i = 0; i < primaryOamLength; i++) {
            readSerializable(ppu.getPrimaryOam()[i], scanner);
        }
        int secondaryOamLength = Integer.parseInt(scanner.next());
        for (int i = 0; i < secondaryOamLength; i++) {
            readSerializable(ppu.getSecondaryOam()[i], scanner);
        }
    }

    // REQUIRES: scanner has at least 4 * 8 + 1 delimited integers.
    // EFFECTS: reads the scanner to set the PPU's sprites
    private static void readPpuSprites(PPU ppu, Scanner scanner) throws IOException {
        int length = Integer.parseInt(scanner.next());
        for (int i = 0; i < length; i++) {
            readSerializable(ppu.getSprites()[i], scanner);
        }
    }

    // REQUIRES: scanner has at least 5 delimited integers.
    // EFFECTS: reads the scanner to set the PPU's Cycling Data
    private static void readPpuCyclingData(PPU ppu, Scanner scanner) throws IOException {
        ppu.setCycle(Integer.parseInt(scanner.next()));
        ppu.setScanline(Integer.parseInt(scanner.next()));
        ppu.setDrawX(Integer.parseInt(scanner.next()));
        ppu.setDrawY(Integer.parseInt(scanner.next()));
        ppu.setOddFrame(Integer.parseInt(scanner.next()) == 1);
    }

    // REQUIRES: scanner has at least 1 delimited string.
    // EFFECTS: reads the scanner to set the CPU's mapper.
    @SuppressWarnings("ParameterCanBeLocal")
    private static Mapper readMapper(Mapper mapper, Scanner scanner) throws IOException {
        int id = Integer.parseInt(scanner.next());

        mapper = Mapper.getMapper(id);
        mapper.deserialize(scanner);
        return mapper;
    }

    // MODIFIES: busSerializable, scanner
    // EFFECTS:  deserializes the busSerializable using the scanner.
    private static void readSerializable(BusSerializable busSerializable, Scanner scanner) {
        busSerializable.deserialize(scanner);
    }

    // EFFECTS: reads the scanner to get the next address.
    private static Address getNextAddress(Scanner scanner) {
        return new Address(Integer.parseInt(scanner.next()), Integer.parseInt(scanner.next()), 0, Integer.MAX_VALUE);
    }
}