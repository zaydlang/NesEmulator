package ppu;

import model.Util;
import persistence.BusSerializable;

import java.util.Scanner;

// Class Sprite:
//     Sprite is a reinterpretation of the secondary OAM of the PPU. It stores it in a more easily modfiiable
//     and accessible way, and contains methods to calculate the next pixel color of the sprite.

public class Sprite implements BusSerializable {
    // Constants
    private static final int SHIFT_REGISTER_SIZE = 8;

    // Fields
    private ShiftRegister shiftRegister0;
    private ShiftRegister shiftRegister1;
    private int latch;
    private int counter;
    private int priority;

    private boolean isMirroredHorizontally;
    private boolean isMirroredVertically;

    // MODIFIES: this
    // EFFECTS:  initializes all the fields according to the given data.
    public Sprite(int patternTableData0, int patternTableData1, int attribute, int spriteX, int priority,
                  boolean isMirroredHorizontally, boolean isMirroredVertically) {
        shiftRegister0 = new ShiftRegister(SHIFT_REGISTER_SIZE);
        shiftRegister1 = new ShiftRegister(SHIFT_REGISTER_SIZE);

        shiftRegister0.setNthBits(0, SHIFT_REGISTER_SIZE, patternTableData0);
        shiftRegister1.setNthBits(0, SHIFT_REGISTER_SIZE, patternTableData1);
        latch          = attribute;
        counter        = spriteX;
        this.priority  = priority;

        this.isMirroredHorizontally = isMirroredHorizontally;
        this.isMirroredVertically   = isMirroredVertically;
    }

    public void decrementCounter() {
        counter--;
    }

    // EFFECTS: a sprite is active if its counter is between -7 and 0 inclusive. Since the counter represents the
    //          x position of the sprite relative to the ppu drawX, this range indicates whether or not the sprite
    //          is currently being drawn.
    public boolean isActive() {
        return -7 <= counter && counter <= 0;
    }

    // MODIFIES: shiftRegister0, shiftRegister1
    // EFFECTS:  shifts both shift registers to the left by 1.
    private void shiftRegisters() {
        if (isMirroredHorizontally) {
            shiftRegister0.shiftRight(1);
            shiftRegister1.shiftRight(1);
        } else {
            shiftRegister0.shiftLeft(1);
            shiftRegister1.shiftLeft(1);
        }
    }

    public int getPriority() {
        return priority;
    }

    // REQUIRES: isActive() must be true
    // MODFIIES: shiftRegister0, shiftRegister1
    // EFFECTS:  calculates the next color address of the sprite
    public int getNextColorAddressAsInt() {
        int patternTableLow  = Util.getNthBit(shiftRegister0.getValue(), isMirroredHorizontally ? 7 : 0);
        int patternTableHigh = Util.getNthBit(shiftRegister1.getValue(), isMirroredHorizontally ? 7 : 0);
        int fullByte = (latch << 2) + (patternTableHigh << 1) + patternTableLow;

        shiftRegisters();
        return fullByte;
    }

    // EFFECTS: serializes the sprite's data into a string
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

    // MODIFIES: this
    // EFFECTS:  deserializes the sprite's data from the scanner.
    @Override
    public void deserialize(Scanner scanner) {
        shiftRegister0.deserialize(scanner);
        shiftRegister1.deserialize(scanner);
        latch    = Integer.parseInt(scanner.next());
        counter  = Integer.parseInt(scanner.next());
        priority = Integer.parseInt(scanner.next());
    }

    public int getCounter() {
        return counter;
    }
}
