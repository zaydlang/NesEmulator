package ppu;

import model.Address;

public class PaletteRamIndexes {
    // Constants
    protected static final int PALETTE_RAM_INDEXES_SIZE = Integer.parseInt("0200", 16);
    protected static final int INITIAL_INDEX_VALUE      = Integer.parseInt("00",   16);

    // Fields
    private Address[] indexes;

    public PaletteRamIndexes() {
        indexes = new Address[PALETTE_RAM_INDEXES_SIZE];
        for (int i = 0; i < PALETTE_RAM_INDEXES_SIZE; i++) {
            indexes[i] = new Address(INITIAL_INDEX_VALUE);
        }

        // Setting up mirrors
        indexes[16] = indexes[0];
        indexes[20] = indexes[4];
        indexes[24] = indexes[8];
        indexes[28] = indexes[12];
    }

    public void writeMemory(int pointer, int value) {
        indexes[pointer].setValue(value);
    }

    public Address readMemory(int pointer) {
        if (pointer % 4 == 0) {
            return indexes[0];
        }

        return indexes[pointer];
    }

    public Address[] getIndexes() {
        return indexes;
    }

    public Address peekMemory(int pointer) {
        return indexes[pointer];
    }
}
