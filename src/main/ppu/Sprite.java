package ppu;

import model.Util;
import persistence.BusSerializable;

import java.awt.*;
import java.util.Scanner;

public class Sprite implements BusSerializable {
    // Constants
    private static final int SHIFT_REGISTER_SIZE = 8;

    // Fields
    private ShiftRegister shiftRegister0;
    private ShiftRegister shiftRegister1;
    private int latch;
    private int counter;
    private int priority;

    public Sprite(int patternTableData0, int patternTableData1, int attribute, int spriteX, int priority) {
        shiftRegister0 = new ShiftRegister(SHIFT_REGISTER_SIZE);
        shiftRegister1 = new ShiftRegister(SHIFT_REGISTER_SIZE);

        shiftRegister0.setNthBits(0, SHIFT_REGISTER_SIZE, patternTableData0);
        shiftRegister1.setNthBits(0, SHIFT_REGISTER_SIZE, patternTableData1);
        latch          = attribute;
        counter        = spriteX;
        this.priority  = priority;
    }

    public void decrementCounter() {
        counter--;
    }

    public boolean isActive() {
        return -7 <= counter && counter <= 0;
    }

    private void shiftRegisters() {
        shiftRegister0.shiftLeft(1);
        shiftRegister1.shiftLeft(1);
    }

    public int getPriority() {
        return priority;
    }

    // isActive() must be true
    public int getNextColorAddressAsInt() {
        int patternTableLow  = Util.getNthBit(shiftRegister0.getValue(), 0);
        int patternTableHigh = Util.getNthBit(shiftRegister1.getValue(), 0);
        int fullByte = (latch << 2) + (patternTableHigh << 1) + patternTableLow;

        shiftRegisters();
        return fullByte;
    }

    @Override
    public String serialize(String delimiter) {
        String output = "";
        output += shiftRegister0.serialize(delimiter);
        output += shiftRegister1.serialize(delimiter);
        output += latch    + delimiter;
        output += counter  + delimiter;
        output += priority + delimiter;
        return output;
    }

    @Override
    public void deserialize(Scanner scanner) {
        shiftRegister0.deserialize(scanner);
        shiftRegister1.deserialize(scanner);
        latch    = Integer.parseInt(scanner.next());
        counter  = Integer.parseInt(scanner.next());
        priority = Integer.parseInt(scanner.next());
    }
}
