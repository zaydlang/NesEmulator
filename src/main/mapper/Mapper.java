package mapper;

import model.Address;

import java.io.FileInputStream;
import java.io.IOException;

public interface Mapper {
    String CARTRIDGE_LOCATION = "./data/";

    Address readMemory(int address);

    void writeMemory(int address, int value);

    void loadCartridge(String cartridgeName) throws IOException;
}
