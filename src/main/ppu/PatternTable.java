package ppu;

import model.Address;
import model.Util;

public class PatternTable {
    // Constants
    private static final int MEMORY_SIZE        = Integer.parseInt("1000", 16);
    private static final int NUM_TILES          = 256;
    private static final int DEFAULT_TILE_VALUE = 0;

    // Fields
    private Address[] memory;

    public PatternTable() {
        memory = new Address[MEMORY_SIZE];

        for (int i = 0; i < MEMORY_SIZE; i++) {
            memory[i] = new Address(DEFAULT_TILE_VALUE);
        }
    }

    public Address[] getTileLow(int pointer) {
        int tile    = pointer;
        int offset  = (MEMORY_SIZE / NUM_TILES) * tile;

        Address[] tileLow = new Address[8];
        for (int i = 0; i < 8; i++) {
            tileLow[i - 0] = readMemory(offset + i);
        }
        return tileLow;
    }

    public Address[] getTileHigh(int pointer) {
        int tile    = pointer;
        int offset  = (MEMORY_SIZE / NUM_TILES) * tile;

        Address[] tileHigh = new Address[8];
        for (int i = 8; i < 16; i++) {
            tileHigh[i - 8] = readMemory(offset + i);
        }
        return tileHigh;
    }

    // REQUIRES: 0 <= pointer <= MEMORY_SIZE - 1
    // EFFECTS: returns the value in memory at the pointer
    public Address readMemory(int pointer) {
        return memory[pointer];
    }

    // REQUIRES: 0 <= pointer <= MEMORY_SIZE - 1
    // EFFECTS: sets the value in memory at the pointer to the given value.
    public void writeMemory(int pointer, int value) {
        memory[pointer].setValue(value);
    }
}
