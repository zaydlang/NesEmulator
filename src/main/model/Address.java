package model;

// Wrapper class for Integer
public class Address {
    private static int DEFAULT_LOWEST_VALUE  = 0;
    private static int DEFAULT_HIGHEST_VALUE = 255;

    private int pointer;
    private Integer value;
    private int lowestValue;
    private int highestValue;

    public Address(Integer value, int pointer, int lowestValue, int highestValue) {
        this.pointer      = pointer;
        this.lowestValue  = lowestValue;
        this.highestValue = highestValue;

        setValue(value);
    }

    public Address(int value, int lowestValue, int highestValue) {
        this.pointer      = value;
        this.lowestValue  = lowestValue;
        this.highestValue = highestValue;

        setValue(value);
    }

    public Address(Integer value, int pointer) {
        this.pointer      = pointer;
        this.lowestValue  = DEFAULT_LOWEST_VALUE;
        this.highestValue = DEFAULT_HIGHEST_VALUE;

        setValue(value);

    }

    public Address(int value) {
        this.pointer      = value;
        this.lowestValue  = 0;
        this.highestValue = 255;

        setValue(value);
    }

    public Integer getValue() {
        return value;
    }

    public int getPointer() {
        return pointer;
    }

    public void setValue(int rawValue) {
        rawValue = (rawValue - lowestValue) % (highestValue - lowestValue + 1) + lowestValue;
        if (rawValue < lowestValue) {
            rawValue += highestValue - lowestValue + 1;
        }

        this.value = rawValue;
    }

    // TODO: ADD TESTS
    @Override
    public String toString() {
        String rawValue = Integer.toHexString(getValue()).toUpperCase();
        int length = (int) Math.floor(Math.log(highestValue - lowestValue + 1
        ) / Math.log(16));

        while (rawValue.length() < length) {
            rawValue = "0" + rawValue;
        }

        return rawValue;
    }
}
