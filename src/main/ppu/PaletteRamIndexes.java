package ppu;

// Class PaletteRamIndexes:
//     A list of indexes into ColorPalette.java. Contains 8 palettes of 4 colors each; the bottom half is for
//     background and the top half is for sprites.

public class PaletteRamIndexes {
    // Constants
    protected static final int PALETTE_RAM_INDEXES_SIZE = 0x0200;
    protected static final int INITIAL_INDEX_VALUE      = 0x00;

    // Fields
    private int[] indexes;

    // MODIFIES: this
    // EFFECTS:  initializes all the palette ram indexes and sets up the mirrors.
    public PaletteRamIndexes() {
        indexes = new int[PALETTE_RAM_INDEXES_SIZE];
        for (int i = 0; i < PALETTE_RAM_INDEXES_SIZE; i++) {
            indexes[i] = INITIAL_INDEX_VALUE;
        }

        // Setting up mirrors
        indexes[16] = indexes[0];
        indexes[20] = indexes[4];
        indexes[24] = indexes[8];
        indexes[28] = indexes[12];
    }

    public void writeMemory(int pointer, int value) {
        indexes[pointer] = (value);
    }

    // EFFECTS: returns the value in memory at indexes, taking into account n % 4 mirrors.
    public int readMemory(int pointer) {
        if ((pointer & 0x3) == 0) {
            return indexes[0];
        }

        return indexes[pointer];
    }

    public int[] getIndexes() {
        return indexes;
    }

    public int peekMemory(int pointer) {
        return indexes[pointer];
    }
}