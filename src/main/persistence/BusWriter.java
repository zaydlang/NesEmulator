package persistence;

import mapper.Mapper;
import model.Address;
import model.Bus;
import model.CPU;
import ppu.PPU;
import ppu.Sprite;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// Class BusWriter
//     BusWriter is a class that can write to a file given an Bus, preserving the Bus' state. Essentially allows the
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
    public static void writeToFile(Bus bus, String fileName) {
        try {
            bus.setEnabled(false);
            FileWriter fileWriter = new FileWriter(SAVE_DIRECTORY + fileName + EXTENSION, false);
            writeCpu(bus.getCpu(), fileWriter);
            writePpu(bus.getPpu(), fileWriter);
            writeMapper(bus.getMapper(), fileWriter);

            fileWriter.flush();
            fileWriter.close();
            bus.setEnabled(true);
        } catch (IOException e) {
            // Do nothing; failed to write to file.
        }
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
        writeSerializable(cpu.getRegisterA(), fileWriter);
        writeSerializable(cpu.getRegisterX(), fileWriter);
        writeSerializable(cpu.getRegisterY(), fileWriter);
        writeSerializable(cpu.getRegisterPC(), fileWriter);
        writeSerializable(cpu.getRegisterS(), fileWriter);
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
        for (int i = 0; i < 0x0800; i++) {
            writeSerializable(cpu.readMemory(i), fileWriter);
        }
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS: writes the CPU's state (cycles and breakpoints) to the fileWriter
    private static void writeCpuState(CPU cpu, FileWriter fileWriter) throws IOException {
        fileWriter.write(cpu.getCycles() + "");
        System.out.println(cpu.getCycles());
        fileWriter.write(DELIMITER);

        ArrayList<Address> breakpoints = cpu.getBreakpoints();
        fileWriter.write(Integer.toString(breakpoints.size()));
        fileWriter.write(DELIMITER);
        for (Address breakpoint : breakpoints) {
            writeSerializable(breakpoint, fileWriter);
        }
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS:  writes the PPU's state to the fileWriter
    private static void writePpu(PPU ppu, FileWriter fileWriter) throws IOException {
        writePpuLatches(ppu, fileWriter);
        writePpuInternalRegisters(ppu, fileWriter);
        writePpuShiftRegisters(ppu, fileWriter);
        writePpuRegisters(ppu, fileWriter);
        writePpuNametables(ppu, fileWriter);
        writePpuPaletteRamIndexes(ppu, fileWriter);
        writePpuOam(ppu, fileWriter);
        writePpuSprites(ppu, fileWriter);
        writePpuCyclingData(ppu, fileWriter);
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS:  writes the PPU's latches to the fileWriter
    private static void writePpuLatches(PPU ppu, FileWriter fileWriter) throws IOException {
        writeSerializable(ppu.getLatchNametable(), fileWriter);
        writeSerializable(ppu.getLatchAttributeTable(), fileWriter);
        writeSerializable(ppu.getLatchPatternTableLow(), fileWriter);
        writeSerializable(ppu.getLatchPatternTableHigh(), fileWriter);
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS:  writes the PPU's internal registers to the fileWriter
    private static void writePpuInternalRegisters(PPU ppu, FileWriter fileWriter) throws IOException {
        writeSerializable(ppu.getRegisterT(), fileWriter);
        writeSerializable(ppu.getRegisterV(), fileWriter);
        writeSerializable(ppu.getRegisterX(), fileWriter);
        writeSerializable(ppu.getRegisterW(), fileWriter);
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS:  writes the PPU's shift registers to the fileWriter
    private static void writePpuShiftRegisters(PPU ppu, FileWriter fileWriter) throws IOException {
        writeSerializable(ppu.getShiftRegisterSmall0(), fileWriter);
        writeSerializable(ppu.getShiftRegisterSmall1(), fileWriter);
        writeSerializable(ppu.getShiftRegisterLarge0(), fileWriter);
        writeSerializable(ppu.getShiftRegisterLarge1(), fileWriter);
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS:  writes the PPU's registers to the fileWriter
    private static void writePpuRegisters(PPU ppu, FileWriter fileWriter) throws IOException {
        writeSerializable(ppu.getPpuCtrl(), fileWriter);
        writeSerializable(ppu.getPpuMask(), fileWriter);
        writeSerializable(ppu.getPpuStatus(), fileWriter);
        writeSerializable(ppu.getOamAddr(), fileWriter);
        writeSerializable(ppu.getPpuScroll(), fileWriter);
        writeSerializable(ppu.getPpuData(), fileWriter);
        writeSerializable(ppu.getPpuDataBuffer(), fileWriter);
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS:  writes the PPU's nametables to the fileWriter
    private static void writePpuNametables(PPU ppu, FileWriter fileWriter) throws IOException {
        fileWriter.write(ppu.getNametable().length + "");
        fileWriter.write(DELIMITER);
        for (Address address : ppu.getNametable()) {
            writeSerializable(address, fileWriter);
        }

        fileWriter.write(ppu.getNametableMirroring().toString());
        fileWriter.write(DELIMITER);
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS:  writes the PPU's palette ram indexes to the fileWriter
    private static void writePpuPaletteRamIndexes(PPU ppu, FileWriter fileWriter) throws IOException {
        fileWriter.write(ppu.getPaletteRamIndexes().getIndexes().length + "");
        fileWriter.write(DELIMITER);
        for (Address address : ppu.getPaletteRamIndexes().getIndexes()) {
            writeSerializable(address, fileWriter);
        }
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS:  writes the PPU's OAM to the fileWriter
    private static void writePpuOam(PPU ppu, FileWriter fileWriter) throws IOException {
        fileWriter.write(ppu.getPrimaryOam().length + "");
        fileWriter.write(DELIMITER);
        for (Address address : ppu.getPrimaryOam()) {
            writeSerializable(address, fileWriter);
        }
        fileWriter.write(ppu.getSecondaryOam().length + "");
        fileWriter.write(DELIMITER);
        for (Address address : ppu.getSecondaryOam()) {
            writeSerializable(address, fileWriter);
        }
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS:  writes the PPU's sprites to the fileWriter
    private static void writePpuSprites(PPU ppu, FileWriter fileWriter) throws IOException {
        fileWriter.write(ppu.getSprites().length + "");
        fileWriter.write(DELIMITER);
        for (Sprite sprite : ppu.getSprites()) {
            writeSerializable(sprite, fileWriter);
        }
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS:  writes the PPU's cycling data to the fileWriter
    private static void writePpuCyclingData(PPU ppu, FileWriter fileWriter) throws IOException {
        fileWriter.write(ppu.getCycle()                + DELIMITER);
        fileWriter.write(ppu.getScanline()             + DELIMITER);
        fileWriter.write(ppu.getDrawX()                + DELIMITER);
        fileWriter.write(ppu.getDrawY()                + DELIMITER);
        fileWriter.write((ppu.getIsOddFrame() ? 1 : 0) + DELIMITER);
    }

    // MODIFIES: fileWriter
    // REQUIRES: fileWriter is open and can be written to.
    // EFFECTS: writes the mapper's cartridge name to the fileWriter
    //          NOTE: as the current mappers only contain ROM, only the cartridgeName need be preserved.
    private static void writeMapper(Mapper mapper, FileWriter fileWriter) throws IOException {
        fileWriter.write(mapper.getId() + "");
        fileWriter.write(DELIMITER);
        writeSerializable(mapper, fileWriter);
    }

    // EFFECTS: using busSerializable to write to the fileWriter
    private static void writeSerializable(BusSerializable busSerializable, FileWriter fileWriter) throws IOException {
        fileWriter.write(busSerializable.serialize(DELIMITER));
    }
}