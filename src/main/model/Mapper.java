package model;

import java.io.FileInputStream;

public interface Mapper {
    String CARTRIDGE_LOCATION = "./data/";

    int readMemory(int address);

    void writeMemory(int address, int value);
}
