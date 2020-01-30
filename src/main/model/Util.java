package model;

public final class Util {
    // REQUIRES: value <= 2^n
    // EFFECTS: returns the nth bit of the binary representation of value.
    public static int getNthBit(int value, int n) {
        return (value >> n) & 1;
    }
}
