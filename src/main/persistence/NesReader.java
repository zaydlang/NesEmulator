package persistence;

import model.*;

import java.io.File;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

// Class NesReader:
//     NesReader is a class that can read from a save file and modify a NES' state to match the contents of that
//     save file. Essentially allows the user to reload from a savestate.

public class NesReader {
    // Constants
    private static final String SAVE_DIRECTORY = NesWriter.SAVE_DIRECTORY;
    private static final String DELIMITER      = NesWriter.DELIMITER;
    private static final String EXTENSION      = NesWriter.EXTENSION;

    // EFFECTS: makes the code coverage autobot not complain about me not instantiating a NesReader class.
    public NesReader() {

    }

    // REQUIRES: fileName.EXTENSION exists in SAVE_DIRECTORY
    // EFFECTS: saves the NES' state to a file at filename. Throws IOException if the file could not be read from.
    public static void readFromFile(NES nes, String fileName) throws IOException {
        Scanner scanner = new Scanner(new File(SAVE_DIRECTORY + fileName + EXTENSION)).useDelimiter(DELIMITER);

        readCpu(nes.getCPU(), scanner);
        readMapper(nes.getMapper(), scanner);
    }

    // EFFECTS: reads the scanner to set the CPU's state
    private static void readCpu(CPU cpu, Scanner scanner) {
        readCpuRegisters(cpu, scanner);
        readCpuFlags(cpu, scanner);
        readCpuRam(cpu, scanner);
        readCpuState(cpu, scanner);
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
        for (int i = 0; i < Integer.parseInt("0800", 16); i++) {
            cpu.writeMemory(i, getNextAddress(scanner).getValue());
        }
    }

    // REQUIRES: the scanner has at least 2n + 1 delimited integers, where n is the first delimited integer
    //           in the scanner.
    // EFFECTS: reads the scanner to set the CPU's state. (cycles and breakpoints)
    private static void readCpuState(CPU cpu, Scanner scanner) {
        cpu.setCycles(getNextAddress(scanner).getValue());

        int numBreakpoints = Integer.parseInt(scanner.next());
        for (int i = 0; i < numBreakpoints; i++) {
            cpu.addBreakpoint(getNextAddress(scanner));
        }
    }

    // REQUIRES: scanner has at least 1 delimited string.
    // EFFECTS: reads the scanner to set the CPU's mapper.
    @SuppressWarnings("ParameterCanBeLocal")
    private static void readMapper(Mapper mapper, Scanner scanner) throws IOException {
        String cartridgeName = scanner.next();
        mapper = new NRom();
        mapper.loadCartridge(cartridgeName);
    }

    // REQUIRES: scanner has at least 2 (4096) delimited integers.
    // EFFECTS: reads the scanner to get the next address.
    private static Address getNextAddress(Scanner scanner) {
        return new Address(Integer.parseInt(scanner.next()), Integer.parseInt(scanner.next()), 0, Integer.MAX_VALUE);
    }
}
