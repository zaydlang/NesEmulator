package ppu;

import model.Address;
import model.Util;
import persistence.BusSerializable;

import java.util.Scanner;

public class ShiftRegister implements BusSerializable {
    private int value;
    private int max;

    public ShiftRegister(int size) {
        this.max  = (int) Math.pow(2, size);
        value     = 0;
    }

    // EFFECTS: right-shifts the register by n
    public void shiftRight(int n) {
        value *= Math.pow(2, n);
        while (value >= max) {
            value -= max;
        }
    }

    // EFFECTS: left-shifts the register by n
    public void shiftLeft(int n) {
        value /= Math.pow(2, n);
    }

    public void setNthBits(int beginning, int end, int newBits) {
        value = Util.maskNthBits(newBits, value, 0, beginning, end - beginning);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String serialize(String delimiter) {
        return value + delimiter + max + delimiter;
    }

    @Override
    public void deserialize(Scanner scanner) {
        this.value = Integer.parseInt(scanner.next());
        this.max   = Integer.parseInt(scanner.next());
    }
}
