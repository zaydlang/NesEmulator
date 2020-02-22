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

        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Address(0);
        }
    }

    public Address readTile(int row, int col) {
        return tiles[col * 32 + row];
    }

    public Address readMemory(int pointer) {
        if (pointer < NAMETABLE_NUM_ROWS * NAMETABLE_NUM_COLS) {
            return tiles[pointer];
        } else {
            return attributeTable.readMemory(pointer - NAMETABLE_NUM_ROWS * NAMETABLE_NUM_COLS);
        }
    }

    public void writeMemory(int pointer, int value) {
        if (pointer < NAMETABLE_NUM_ROWS * NAMETABLE_NUM_COLS) {
            tiles[pointer].setValue(value);
        } else {
            attributeTable.writeMemory(pointer - NAMETABLE_NUM_ROWS * NAMETABLE_NUM_COLS, value);
        }
    }

    public AttributeTable getAttributeTable() {
        return attributeTable;
    }
}
