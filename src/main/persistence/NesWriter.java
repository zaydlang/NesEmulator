package persistence;

import model.Address;
import model.CPU;
import model.Mapper;
import model.NES;

import java.io.FileWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// Class NesWriter
//     NesWriter is a class that can write to a file given an NES, preserving the NES' state. Essentially allows the
//     user to create a savestate.

public class NesWriter {
    // Constants
    protected static final String SAVE_DIRECTORY = "./data/save/";
    protected static final String DELIMITER      = " ";
    protected static final String EXTENSION      = ".sav";

    // EFFECTS: makes the code coverage autobot not complain about me not instantiating a NesWriter class.
    public NesWriter() {

    }

    // REQUIRES: if fileName contains a file path, then the path is valid.
    // EFFECTS: writes the NES' state to SAVE_DIRETORY/fileName.EXTENSION
    public static void writeToFile(NES nes, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(SAVE_DIRECTORY + fileName + EXTENSION, false);
        writeCpu(nes.getCPU(), fileWriter);
        writeMapper(nes.getMapper(), fileWriter);

        fileWriter.flush();
        fileWriter.close();
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS: writes the CPU's state to the fileWriter
    private static void writeCpu(CPU cpu, FileWriter fileWriter) throws IOException {
        writeCpuRegisters(cpu, fileWriter);
        writeCpuFlags(cpu, fileWriter);
        writeCpuRam(cpu, fileWriter);
        writeCpuState(cpu, fileWriter);
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS: writes the CPU's Registers to the fileWriter
    private static void writeCpuRegisters(CPU cpu, FileWriter fileWriter) throws IOException {
        fileWriter.write(cpu.getRegisterA().serialize(DELIMITER));
        fileWriter.write(cpu.getRegisterX().serialize(DELIMITER));
        fileWriter.write(cpu.getRegisterY().serialize(DELIMITER));
        fileWriter.write(cpu.getRegisterPC().serialize(DELIMITER));
        fileWriter.write(cpu.getRegisterS().serialize(DELIMITER));
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS: writes the CPU's flags to the fileWriter
    private static void writeCpuFlags(CPU cpu, FileWriter fileWriter) throws IOException {
        fileWriter.write(cpu.getStatus());
        fileWriter.write(DELIMITER);
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS: writes the CPU's ram to the fileWriter
    private static void writeCpuRam(CPU cpu, FileWriter fileWriter) throws IOException {
        for (int i = 0; i < Integer.parseInt("0800", 16); i++) {
            fileWriter.write(cpu.readMemory(i).serialize(DELIMITER));
        }
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS: writes the CPU's state (cycles and breakpoints) to the fileWriter
    private static void writeCpuState(CPU cpu, FileWriter fileWriter) throws IOException {
        fileWriter.write(new Address(cpu.getCycles()).serialize(DELIMITER));

        ArrayList<Address> breakpoints = cpu.getBreakpoints();
        fileWriter.write(Integer.toString(breakpoints.size()));
        fileWriter.write(DELIMITER);
        for (Address breakpoint : breakpoints) {
            fileWriter.write(breakpoint.serialize(DELIMITER));
        }
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS: writes the mapper's cartridge name to the fileWriter
    //          NOTE: as the current mappers only contain ROM, only the cartridgeName need be preserved.
    private static void writeMapper(Mapper mapper, FileWriter fileWriter) throws IOException {
        String cartridgeName = mapper.getCartridgeName();
        fileWriter.write(cartridgeName);
        fileWriter.write(DELIMITER);
    }
}
