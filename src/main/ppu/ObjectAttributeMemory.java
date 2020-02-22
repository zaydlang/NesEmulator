package ppu;

import model.Address;

public class ObjectAttributeMemory {
    // Constants
    private static final int OAM_LENGTH = 4;

    // Fields
    private Address[] data;

    // REQUIRES: data.length == OAM_LENGTH
    public ObjectAttributeMemory(Address[] data) {
        this.data = data;
    }
}
