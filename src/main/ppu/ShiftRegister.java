package ppu;

import model.Util;
import persistence.BusSerializable;

import java.util.Scanner;

// Class ShiftRegister:
//     A non-carry binary ShiftRegister that can shift its bits left and right

public class ShiftRegister implements BusSerializable {
    private int value;
    private int max;

    // MODFIIES: this
    // EFFECTS:  sets the max value of the shiftRegister to 2^size and sets the value to 0
    public ShiftRegister(int size) {
        this.max  = (int) Math.pow(2, size);
        value     = 0;
    }

    // MODIFIES: this
    // EFFECTS:  right-shifts the register by n
    public void shiftRight(int n) {
        value *= Math.pow(2, n);
        while (value >= max) {
            value -= max;
        }
    }

    // MODIFIES: this
    // EFFECTS: left-shifts the register by n
    public void shiftLeft(int n) {
        value /= Math.pow(2, n);
    }

    // MODIFIES: this
    // EFFECTS:  sets the bits from beginning to end to newBits.
    public void setNthBits(int beginning, int end, int newBits) {
        value = Util.maskNthBits(newBits, value, 0, beginning, end - beginning);
    }

    public int getValue() {
        return value;
    }

    // EFFECTS:  serializes using the format value + delimiter + max + delimiter.
    @Override
    public String serialize(String delimiter) {
        return value + delimiter + max + delimiter;
    }

    // MODIFIES: this
    // EFFECTS:  deserializes using the format value + delimiter + max + delimiter.
    @Override
    public void deserialize(Scanner scanner) {
        this.value = Integer.parseInt(scanner.next());
        this.max   = Integer.parseInt(scanner.next());
    }
}
