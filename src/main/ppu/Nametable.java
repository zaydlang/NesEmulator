package ppu;

import model.Address;

public class Nametable {
    private static final int NAMETABLE_NUM_ROWS = 30;
    private static final int NAMETABLE_NUM_COLS = 32;

    private Address[] tiles;
    private AttributeTable attributeTable;

    public Nametable() {
        tiles = new Address[NAMETABLE_NUM_ROWS * NAMETABLE_NUM_COLS];
        attributeTable = new AttributeTable();
    }

    public Address readMemory(int pointer) {
        if (pointer < NAMETABLE_NUM_ROWS * NAMETABLE_NUM_COLS) {
            return tiles[pointer];
        } else {
            return attributeTable.readMemory(pointer - NAMETABLE_NUM_ROWS * NAMETABLE_NUM_COLS);
        }
    }
}
