package model;

import persistence.NesReader;
import persistence.NesWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Class NES:
//     NES is just a class (for now) that controls the CPU. When the PPU is implemented, the NES will have a lot more
//     to do besides just logging files.

public class NES {
    // Constants
    private static String LOG_DESTINATION_HEADER = "./data/log/";

    // Fields
    private CPU cpu;

    private FileWriter logFile;
    private String filePath;     // This variable shouldn't have to exist; it's redundant... but such is life.

    // EFFECTS: initializes the CPU, and starts a logfile based on the current timestamp.
    public NES() throws IOException {
        this.cpu = new CPU();

        // https://stackoverflow.com/questions/23068676/how-to-get-current-timestamp-in-string-format-in-java-yyyy-mm-dd-hh-mm-ss
        filePath = LOG_DESTINATION_HEADER + new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date()) + ".log";
        logFile  = new FileWriter(filePath);
    }

    // REQUIRES: cartridgeName is a valid file name for a valid NES NROM cartridge. If file not found, throws
    // IOException.
    // EFFECTS: loads a cartridge into the cpu
    public void loadCartridge(String cartridgeName) throws IOException {
        cpu.loadCartridge(cartridgeName);
    }

    // REQUIRES: logFile is open.
    // MODIFIES: cpu, logfile
    // EFFECTS: cycles the cpu through one instruction, throws IOException if logfile has been closed.
    public String cycle() throws IOException {
        String cpuStatus = cpu.cycle();
        logFile.write(cpuStatus + "\n");

        return cpuStatus;
    }

    // REQUIRES: logFile is open.
    // MODIFIES: flushes and closes the logfile. throws IOException if logfile has already been closed.
    public void close() throws IOException {
        logFile.flush();
        logFile.close();
    }

    // EFFECTS: saves the NES' state to a given file
    public void save(String fileName) throws IOException {
        NesWriter.writeToFile(this, fileName);
    }

    // MODIFIES: this
    // EFFECTS: loads the NES' state from a given file
    public void load(String fileName) throws IOException {
        NesReader.readFromFile(this, fileName);
    }

    // REQUIRES: 0x0000 <= breakpoint <= 0xFFFF
    // EFFECTS: adds a breakpoint to the cpu.
    public void addBreakpoint(Address breakpoint) {
        cpu.addBreakpoint(breakpoint);
    }

    // MODIFIES: cpu
    // EFFECTS: returns whether or not all components of the NES are enabled
    public boolean isEnabled() {
        return cpu.isEnabled();
    }

    // EFFECTS: enables all components of the NES
    public void enable() {
        cpu.setEnabled(true);
    }

    // EFFECTS: returns the logFile.
    public FileWriter getLogFile() {
        return logFile;
    }

    // EFFECTS: returns the filePath.
    public String getFilePath() {
        return filePath;
    }

    public Mapper getMapper() {
        return cpu.getMapper();
    }

    public CPU getCPU() {
        return cpu;
    }
}
