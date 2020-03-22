package model;

// Class Address:
//     Address is a wrapper class for Integer. It's also a wrapper class in a more literal, punny sense, in that it
//     wraps the integer between a given lowest value and highest value.

import persistence.BusSerializable;

import java.util.Scanner;

public class Address implements BusSerializable {
    private static int DEFAULT_LOWEST_VALUE  = 0;
    private static int DEFAULT_HIGHEST_VALUE = 255;

    private int pointer;
    private Integer value;
    private int lowestValue;
    private int highestValue;

    // EFFECTS: sets the given pointer, lowestValue, and highestValue to the specified arguments and wraps the given
    // value in between the lowestValue and highestValue.
    public Address(Integer value, int pointer, int lowestValue, int highestValue) {
        this.pointer      = pointer;
        this.lowestValue  = lowestValue;
        this.highestValue = highestValue;

        setValue(value);
    }

    // EFFECTS: sets the given lowestValue, and highestValue to the specified arguments and wraps the given
    // value in between the lowestValue and highestValue. Sets the pointer to the value.
    public Address(int value, int lowestValue, int highestValue) {
        this.lowestValue  = lowestValue;
        this.highestValue = highestValue;

        setValue(value);
        this.pointer      = value;
    }

    // EFFECTS: sets the given lowestValue, and highestValue to their default values and wraps the given
    // value in between the lowestValue and highestValue. Sets the pointer to the given argument.
    public Address(Integer value, int pointer) {
        this.pointer      = pointer;
        this.lowestValue  = DEFAULT_LOWEST_VALUE;
        this.highestValue = DEFAULT_HIGHEST_VALUE;

        setValue(value);

    }

    // EFFECTS: sets the given lowestValue, and highestValue to their default values and wraps the given
    // value in between the lowestValue and highestValue. Sets the pointer to the value.
    public Address(int value) {
        this.lowestValue  = 0;
        this.highestValue = 255;

        setValue(value);
        this.pointer = value;
    }

    // EFFECTS: returns a String representation of the Address in hexidecimal, padded according to its highest and
    // lowest value.
    @Override
    public String toString() {
        StringBuilder rawValue = new StringBuilder(Integer.toHexString(getValue()).toUpperCase());
        int length = (int) Math.floor(Math.log(highestValue - lowestValue + 1
        ) / Math.log(16));

        while (rawValue.length() < length) {
            rawValue.insert(0, "0");
        }

        return rawValue.toString();
    }

    // EFFECTS: returns the value.
    public Integer getValue() {
        return value;
    }

    // EFFECTS: returns the pointer.
    public int getPointer() {
        return pointer;
    }

    // MODIFIES: rawValue
    // EFFECTS: sets the given value to the value wrapped around highestValue and lowestValue
    public void setValue(int rawValue) {
        rawValue = (rawValue - lowestValue) % (highestValue - lowestValue + 1) + lowestValue;
        if (rawValue < lowestValue) {
            rawValue += highestValue - lowestValue + 1;
        }

        this.value = rawValue;
    }

    public Address getReference() {
        return this;
    }

    // EFFECTS: returns a string version of the Address of the format: value + delimiter + pointer + delimiter.
    @Override
    public String serialize(String delimiter) {
        return value + delimiter + pointer + delimiter;
    }

    @Override
    public void deserialize(Scanner scanner) {
        this.value   = Integer.parseInt(scanner.next());
        this.pointer = Integer.parseInt(scanner.next());
    }
}
