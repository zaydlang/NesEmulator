package ui;

import ui.window.CpuOutput;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CpuFileOutput implements CpuOutput {
    private FileWriter fileWriter;

    public CpuFileOutput() {
        try {
            fileWriter = new FileWriter(new File("data/log/log.txt"));
        } catch (IOException e) {
            // eek
        }
    }

    @Override
    public void log(String cpuLog) {
        try {
            fileWriter.write(cpuLog + "\n");
        } catch (IOException e) {
            // eek
        }
    }
}
