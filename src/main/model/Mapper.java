package model;

import java.io.FileInputStream;

public interface Mapper {
    String CARTRIDGE_LOCATION = "./data/";

    int readMemory(int address);

    boolean writeMemory(int address, int value);
}
