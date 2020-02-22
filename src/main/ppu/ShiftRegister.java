package ppu;

import model.Address;
import model.Util;

public class ShiftRegister {
    boolean[] bits;
    Address index;

    public ShiftRegister(int size) {
        bits  = new boolean[size];
        index = new Address(0);
    }

    // EFFECTS: right-shifts the register by n
    public void shiftRight(int n) {
        index.setValue(index.getValue() + n);
    }

    // EFFECTS: left-shifts the register by n
    public void shiftLeft(int n) {
        index.setValue(index.getValue() - n);
    }

    // REQUIRES: 0 <= n <= bits.length - 1
    // EFFECTS: returns the nth bit in the shift register.
    public boolean getBit(int n) {
        return bits[(index.getValue() + n) % bits.length];
    }

    public void setNthBits(int beginning, int end, Address newBits) {
        for (int i = beginning; i < end; i++) {
            bits[i] = Util.getNthBit(newBits.getValue(), i - beginning) == 1;
        }
    }

    public void setBits(Address newBits) {
        for (int i = 0; i < bits.length; i++) {
            bits[i] = Util.getNthBit(newBits.getValue(), i) == 1;
        }
    }

    public int getValue() {
        int sum = 0;

        for (int i = 0; i < bits.length; i++) {
            sum += Math.pow(2, getBit(i) ? 1 : 0);
        }

        return sum;
    }
}
