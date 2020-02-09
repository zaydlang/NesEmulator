package model;

public final class Util {
    // REQUIRES: value <= 2^n
    // EFFECTS: returns the nth bit of the binary representation of value.
    public static int getNthBit(int value, int n) {
        return (value >> n) & 1;
    }

    // TODO add test
    public static int getSign(int value) {
        if (value == 0) {
            return 0;
        } else {
            return (value <= 127) ? 1 : -1;
        }
    }
}
