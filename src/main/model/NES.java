package model;

import ppu.PPU;
import ui.Pixels;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

// Class NES:
//     NES is just a class (for now) that controls the CPU. When the PPU is implemented, the NES will have a lot more
//     to do besides just logging files.

public class NES {
    private static String LOG_DESTINATION_HEADER = "./data/log/";

    private CPU cpu;

    private FileWriter logFile;
    private String filePath;     // This variable shouldn't have to exist; it's redundant... but such is life.

    private int trueCycles;
    private boolean enabled;

    // EFFECTS: initializes the CPU, and starts a logfile based on the current timestamp.
    public NES() throws IOException {
        this.cpu    = new CPU();

        // https://stackoverflow.com/questions/23068676/how-to-get-current-timestamp-in-string-format-in-java-yyyy-mm-dd-hh-mm-ss
        filePath = LOG_DESTINATION_HEADER + new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date()) + ".log";
        logFile  = new FileWriter(filePath);

        trueCycles = 0;
        enabled    = false;
    }

    // REQUIRES: cartridgeName is a valid file name for a valid NES NROM cartridge. If file not found, throws
    // IOException.
    // EFFECTS: loads a cartridge into the cpu
    public void loadCartridge(String cartridgeName) throws IOException {
        cpu.loadCartridge(cartridgeName);
        enabled = true;
    }

    // REQUIRES: logFile is open.
    // MODIFIES: cpu, logfile
    // EFFECTS: cycles the cpu through one instruction, throws IOException if logfile has been closed.
    public String cycle() throws IOException {
        if (!enabled) {
            return "";
        }

        String cpuStatus = "";
        if (cpu.getCycles() <= trueCycles) {
            try {
                cpuStatus = cpu.cycle();
            } catch (Exception e) {
                e.printStackTrace();
                int x = 2;
            }
            logFile.write(cpuStatus + "\n");
        }

        if (cpu.ppu.getCycles() <= trueCycles) {
            cpu.ppu.cycle();
            cpu.ppu.cycle();
            cpu.ppu.cycle();
        }

        trueCycles += 1;
        return cpuStatus;
    }

    /*
    public void renderPatternTables() {
        cpu.ppu.renderPatternTables();
    }*/

    // REQUIRES: logFile is open.
    // MODIFIES: flushes and closes the logfile. throws IOException if logfile has already been closed.
    public void close() throws IOException {
        logFile.flush();
        logFile.close();
    }

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

    public void addBreakpoint(Address breakpoint) {
        cpu.addBreakpoint(breakpoint);
    }

    public void setPixels(Pixels pixels) {
        cpu.ppu.setPixels(pixels);
    }

    public Pixels getPixels() {
        return cpu.ppu.getPixels();
    }

    public void renderPatternTables(Pixels pixels, int basePalette) {
        cpu.ppu.renderPatternTables(pixels, basePalette);
    }

    public void renderNameTables(Pixels pixels, int basePalette) {
        cpu.ppu.renderNameTables(pixels, basePalette);
    }

    public PPU getPPU() {
        return cpu.ppu;
    }
}
