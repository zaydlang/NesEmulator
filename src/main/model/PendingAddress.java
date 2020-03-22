package model;

// Class PendingAddress:
//     An Address that only accesses its memory if it is needed. The PendingAddressValue.run() is accessed only if it
//     is needed. This is because some accesses can affect PPU state, so this allows accesses to only be made
//     if they are required.

public class PendingAddress extends Address {
    public interface PendingAddressValue {
        Address run();
    }

    private PendingAddressValue pendingAddressValue;

    // MODIFIES: pointer, pendingAddressValue
    // EFFECTS:  calls the super constructor and sets the pendingAddressValue to the given value
    public PendingAddress(int pointer, PendingAddressValue pendingAddressValue) {
        super(pointer);
        this.pendingAddressValue = pendingAddressValue;
    }

    // EFFECTS: runs the pendingAddressValue and returns its value.
    @Override
    public Integer getValue() {
        return pendingAddressValue.run().getValue();
    }

    // EFFECTS: runs the pendingAddressValue
    @Override
    public Address getReference() {
        return pendingAddressValue.run();
    }
}
