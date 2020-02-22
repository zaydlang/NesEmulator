package ppu;

public class Sprite {
    // Constants
    private static final int SHIFT_REGISTER_SIZE = 8;

    // Fields
    private ObjectAttributeMemory oam;
    private ShiftRegister shiftRegister0;
    private ShiftRegister shiftRegister1;
    private boolean latch;
    private int counter;

    public Sprite() {
        shiftRegister0 = new ShiftRegister(SHIFT_REGISTER_SIZE);
        shiftRegister1 = new ShiftRegister(SHIFT_REGISTER_SIZE);
    }

    public void decrementCounter() {
        counter--;
    }

    public int getCounter() {
        return counter;
    }

    public void shiftRegisters() {
        shiftRegister0.shiftRight(1);
        shiftRegister1.shiftRight(1);
    }
}
