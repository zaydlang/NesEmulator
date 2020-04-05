package ppu;

import model.Address;

// Class PaletteRamIndexes:
//     A list of indexes into ColorPalette.java. Contains 8 palettes of 4 colors each; the bottom half is for
//     background and the top half is for sprites.

public class PaletteRamIndexesSolutions {
    // Constants
    protected static final int PALETTE_RAM_INDEXES_SIZE = Integer.parseInt("0200", 16);

    // Fields
    private int[] indexes;

    // MODIFIES: this
    // EFFECTS:  initializes all the palette ram indexes and sets up the mirrors.
    public PaletteRamIndexesSolutions() {
        indexes = new int[PALETTE_RAM_INDEXES_SIZE];
        for (int i = 0; i < PALETTE_RAM_INDEXES_SIZE; i++) {
            indexes[i] = 0;
        }
    }

    // MODIFIES: this
    // EFFECTS:  mirrors the pointer and writes the value into the indexes.
    public void writeMemory(int pointer, int value) {
        pointer = mirrorAddress(pointer);
        indexes[pointer] = value;
    }

    // EFFECTS: mirrors the pointer such that:
    //          10 -> 00
    //          14 -> 04
    //          18 -> 08
    //          1C -> 0C
    private int mirrorAddress(int pointer) {
        if (pointer == Integer.parseInt("10", 16)) {
            return Integer.parseInt("00", 16);
        }
        if (pointer == Integer.parseInt("14", 16)) {
            return Integer.parseInt("04", 16);
        }
        if (pointer == Integer.parseInt("18", 16)) {
            return Integer.parseInt("08", 16);
        }
        if (pointer == Integer.parseInt("1C", 16)) {
            return Integer.parseInt("0C", 16);
        }
        return pointer;
    }


    // EFFECTS: returns the value in memory at indexes, taking into account n % 4 mirrors.
    public int readMemory(int pointer) {
        if (pointer % 4 == 0) {
            return indexes[0];
        }

        return indexes[pointer];
    }

    public int[] getIndexes() {
        return indexes;
    }
}
