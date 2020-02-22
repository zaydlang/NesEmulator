package ppu;

import model.Address;
import model.Util;

public class AttributeTable {
    private Address[] data;

    // REQUIRES: data.length == 64
    public AttributeTable() {
        data = new Address[64];

        for (int i = 0; i < data.length; i++) {
            data[i] = new Address(0);
        }
    }

    public Address readMemory(int pointer) {
        return data[pointer];
    }

    public void writeMemory(int pointer, int value) {
        data[pointer].setValue(value);
    }

    // REQUIRES: 0 <= pointer <= 64 - 1
    public Address getQuadrant(int pointer, int quadrant) {
        int bitOne = Util.getNthBit(readMemory(pointer).getValue(), quadrant + 0);
        int bitTwo = Util.getNthBit(readMemory(pointer).getValue(), quadrant + 1);
        return new Address(bitTwo * 2 + bitOne, 0, 4);
    }

    public Address getPalette(int row, int col) {
        int newRow = row / 4;
        int newCol = col / 4;
        int quadrant = (col % 2) * 2 + (row % 2);
        return getQuadrant(newRow + newCol * 32, quadrant);
    }
}
