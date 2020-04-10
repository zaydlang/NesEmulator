package ppu;

import model.Address;

// Class PaletteRamIndexes:
//     A list of indexes into ColorPalette.java. Contains 8 palettes of 4 colors each; the bottom half is for
//     background and the top half is for sprites.

public class PaletteRamIndexes {
    // Constants
    protected static final int PALETTE_RAM_INDEXES_SIZE = 0x0200;
    protected static final int INITIAL_INDEX_VALUE      = 0x00;

    // Fields
    private Address[] indexes;

    // MODIFIES: this
    // EFFECTS:  initializes all the palette ram indexes and sets up the mirrors.
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

    // EFFECTS: returns the value in memory at indexes, taking into account n % 4 mirrors.
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