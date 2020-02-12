package util;

// Class Util:
//     A collection of various utility methods.

public final class Util {
    // REQUIRES: value <= 2^n
    // EFFECTS: returns the nth bit of the binary representation of value.
    public static int getNthBit(int value, int n) {
        return (value >> n) & 1;
    }

    // EFFECTS: returns the sign of the given integer interpreted as a signed binary value.
    //          0 if zero, 1 if positive, -1 if negative.
    public static int getSign(int value) {
        if (value == 0) {
            return 0;
        } else {
            return (value <= 127) ? 1 : -1;
        }
    }
}
