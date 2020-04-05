package ppu;

public class PaletteRamIndexes {
    // Constants
    protected static final int PALETTE_RAM_INDEXES_SIZE = Integer.parseInt("0200", 16);

    // Fields
    private int[] indexes;

    // MODIFIES: this
    // EFFECTS:  initializes all the palette ram indexes and sets up the mirrors.
    public PaletteRamIndexes() {

    }

    // MODIFIES: this
    // EFFECTS:  mirrors the pointer and writes the value into the indexes.
    public void writeMemory(int pointer, int value) {
    }

    // EFFECTS: mirrors the pointer such that:
    //          10 -> 00
    //          14 -> 04
    //          18 -> 08
    //          1C -> 0C
    private int mirrorAddress(int pointer) {
        return pointer;
    }


    // EFFECTS: returns the value in memory at indexes, taking into account n % 4 mirrors.
    public int readMemory(int pointer) {
        return 0;
    }

    public int[] getIndexes() {
        return indexes;
    }
}
