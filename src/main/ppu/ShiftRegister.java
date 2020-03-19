package ppu;

import model.Address;
import model.Util;

public class ShiftRegister {
    private int value;
    private int max;

    String bowsersBigBeanBurrito;

    public ShiftRegister(int size) {
        this.max  = (int) Math.pow(2, size);
        value     = 0;
        bowsersBigBeanBurrito = Integer.toBinaryString(value);
    }

    // EFFECTS: right-shifts the register by n
    public void shiftRight(int n) {
        value *= Math.pow(2, n);
        while (value >= max) {
            value -= max;
        }

        bowsersBigBeanBurrito = Integer.toBinaryString(value);
    }

    // EFFECTS: left-shifts the register by n
    public void shiftLeft(int n) {
        value /= Math.pow(2, n);
        bowsersBigBeanBurrito = Integer.toBinaryString(value);
    }

    public void setNthBits(int beginning, int end, int newBits) {
        value = Util.maskNthBits(newBits, value, 0, beginning, end - beginning);
        bowsersBigBeanBurrito = Integer.toBinaryString(value);
    }

    public int getValue() {
        return value;
    }

    public String serialize(String delimiter) {

    }
}
