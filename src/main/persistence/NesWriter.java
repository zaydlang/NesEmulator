package persistence;

import model.Address;
import model.CPU;
import model.Mapper;
import model.NES;

import java.io.FileWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class NesWriter {
    // Constants
    protected static final String SAVE_DIRECTORY = "./data/save/";
    protected static final String DELIMITER      = " ";
    protected static final String EXTENSION      = ".sav";

    // Dummy Constructor
    public NesWriter() {

    }

    public static void writeToFile(NES nes, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(SAVE_DIRECTORY + fileName + EXTENSION, false);
        writeCpu(nes.getCPU(), fileWriter);
        writeMapper(nes.getMapper(), fileWriter);

        fileWriter.flush();
        fileWriter.close();
    }

    private static void writeCpu(CPU cpu, FileWriter fileWriter) throws IOException {
        writeCpuRegisters(cpu, fileWriter);
        writeCpuFlags(cpu, fileWriter);
        writeCpuRam(cpu, fileWriter);
        writeCpuState(cpu, fileWriter);
    }

    private static void writeCpuRegisters(CPU cpu, FileWriter fileWriter) throws IOException {
        fileWriter.write(cpu.getRegisterA().serialize(DELIMITER));
        fileWriter.write(cpu.getRegisterX().serialize(DELIMITER));
        fileWriter.write(cpu.getRegisterY().serialize(DELIMITER));
        fileWriter.write(cpu.getRegisterPC().serialize(DELIMITER));
        fileWriter.write(cpu.getRegisterS().serialize(DELIMITER));
    }

    private static void writeCpuFlags(CPU cpu, FileWriter fileWriter) throws IOException {
        fileWriter.write(cpu.getStatus());
        fileWriter.write(DELIMITER);
    }

    private static void writeCpuRam(CPU cpu, FileWriter fileWriter) throws IOException {
        for (int i = 0; i < Integer.parseInt("0800", 16); i++) {
            fileWriter.write(cpu.readMemory(i).serialize(DELIMITER));
        }
    }

    private static void writeCpuState(CPU cpu, FileWriter fileWriter) throws IOException {
        fileWriter.write(new Address(cpu.getCycles()).serialize(DELIMITER));

        ArrayList<Address> breakpoints = cpu.getBreakpoints();
        fileWriter.write(Integer.toString(breakpoints.size()));
        fileWriter.write(DELIMITER);
        for (Address breakpoint : breakpoints) {
            fileWriter.write(breakpoint.serialize(DELIMITER));
        }
    }

    private static void writeMapper(Mapper mapper, FileWriter fileWriter) throws IOException {
        String cartridgeName = mapper.getCartridgeName();
        fileWriter.write(cartridgeName);
        fileWriter.write(DELIMITER);
    }
}
