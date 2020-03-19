package persistence;

import mapper.Mapper;
import model.Address;
import model.Bus;
import model.CPU;
import ppu.PPU;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// Class NesWriter
//     NesWriter is a class that can write to a file given an NES, preserving the NES' state. Essentially allows the
//     user to create a savestate.

public class BusWriter {
    // Constants
    protected static final String SAVE_DIRECTORY = "./data/save/";
    protected static final String DELIMITER      = " ";
    protected static final String EXTENSION      = ".sav";

    // EFFECTS: makes the code coverage autobot not complain about me not instantiating a NesWriter class.
    public BusWriter() {

    }

    // REQUIRES: if fileName contains a file path, then the path is valid.
    // EFFECTS: writes the NES' state to SAVE_DIRETORY/fileName.EXTENSION
    public static void writeToFile(Bus bus, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(SAVE_DIRECTORY + fileName + EXTENSION, false);
        writeCpu(bus.getCpu(), fileWriter);
        writePpu(bus.getPpu(), fileWriter);
        writeMapper(bus.getMapper(), fileWriter);

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

    private static void writePpu(PPU ppu, FileWriter fileWriter) throws IOException {
        writePpuLatches(ppu, fileWriter);
        writePpuInternalRegisters(ppu, fileWriter);
        writePpuShiftRegisters(ppu, fileWriter);
        writePpuSprites(ppu, fileWriter);
        writePpuRegisters(ppu, fileWriter);
        writePpuNametables(ppu, fileWriter);
        writePpuPaletteRamIndexes(ppu, fileWriter);
        writePpuOam(ppu, fileWriter);
        writePpuSprites(ppu, fileWriter);
        writePpuCyclingData(ppu, fileWriter);
    }

    private static void writePpuLatches(PPU ppu, FileWriter fileWriter) throws IOException {
        fileWriter.write(ppu.getLatchNametable().serialize(DELIMITER));
        fileWriter.write(ppu.getLatchAttributeTable().serialize(DELIMITER));
        fileWriter.write(ppu.getLatchPatternTableLow().serialize(DELIMITER));
        fileWriter.write(ppu.getLatchPatternTableHigh().serialize(DELIMITER));
    }

    private static void writePpuInternalRegisters(PPU ppu, FileWriter fileWriter) throws IOException {
        fileWriter.write(ppu.getRegisterT().serialize(DELIMITER));
        fileWriter.write(ppu.getRegisterV().serialize(DELIMITER));
        fileWriter.write(ppu.getRegisterX().serialize(DELIMITER));
        fileWriter.write(ppu.getRegisterW().serialize(DELIMITER));
    }

    private static void writePpuShiftRegisters(PPU ppu, FileWriter fileWriter) throws IOException {

    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS: writes the mapper's cartridge name to the fileWriter
    //          NOTE: as the current mappers only contain ROM, only the cartridgeName need be preserved.
    private static void writeMapper(Mapper mapper, FileWriter fileWriter) throws IOException {
        int cartridgeId = mapper.getId();
        fileWriter.write(cartridgeId);
        fileWriter.write(DELIMITER);
        fileWriter.write(mapper.serialize(DELIMITER));
    }
}