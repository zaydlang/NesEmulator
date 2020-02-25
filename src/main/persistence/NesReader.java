package persistence;

import model.*;

import java.io.File;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

public class NesReader {
    // Constants
    private static final String SAVE_DIRECTORY = NesWriter.SAVE_DIRECTORY;
    private static final String DELIMITER      = NesWriter.DELIMITER;
    private static final String EXTENSION      = NesWriter.EXTENSION;

    // Dummy Constructor
    public NesReader() {

    }

    public static void readFromFile(NES nes, String fileName) throws IOException {
        Scanner scanner = new Scanner(new File(SAVE_DIRECTORY + fileName + EXTENSION)).useDelimiter(DELIMITER);

        readCpu(nes.getCPU(), scanner);
        readMapper(nes.getMapper(), scanner);
    }

    private static void readCpu(CPU cpu, Scanner scanner) {
        readCpuRegisters(cpu, scanner);
        readCpuFlags(cpu, scanner);
        readCpuRam(cpu, scanner);
        readCpuState(cpu, scanner);
    }

    private static void readCpuRegisters(CPU cpu, Scanner scanner) {
        cpu.setRegisterA(getNextAddress(scanner).getValue());
        cpu.setRegisterX(getNextAddress(scanner).getValue());
        cpu.setRegisterY(getNextAddress(scanner).getValue());
        cpu.setRegisterPC(getNextAddress(scanner).getValue());
        cpu.setRegisterS(getNextAddress(scanner).getValue());
    }

    private static void readCpuFlags(CPU cpu, Scanner scanner) {
        cpu.setStatus(Integer.parseInt(scanner.next()));
    }

    private static void readCpuRam(CPU cpu, Scanner scanner) {
        for (int i = 0; i < Integer.parseInt("0800", 16); i++) {
            cpu.writeMemory(i, getNextAddress(scanner).getValue());
        }
    }

    private static void readCpuState(CPU cpu, Scanner scanner) {
        cpu.setCycles(getNextAddress(scanner).getValue());

        int numBreakpoints = Integer.parseInt(scanner.next());
        for (int i = 0; i < numBreakpoints; i++) {
            cpu.addBreakpoint(getNextAddress(scanner));
        }
    }

    @SuppressWarnings("ParameterCanBeLocal")
    private static void readMapper(Mapper mapper, Scanner scanner) throws IOException {
        String cartridgeName = scanner.next();
        mapper = new NRom();
        mapper.loadCartridge(cartridgeName);
    }

    private static Address getNextAddress(Scanner scanner) {
        return new Address(Integer.parseInt(scanner.next()), Integer.parseInt(scanner.next()), 0, Integer.MAX_VALUE);
    }
}
