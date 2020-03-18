package model;

public class PendingAddress extends Address {
    public interface PendingAddressValue {
        Address run();
    }

    private PendingAddressValue pendingAddressValue;

    public PendingAddress(int pointer, PendingAddressValue pendingAddressValue) {
        super(pointer);
        this.pendingAddressValue = pendingAddressValue;
    }

    @Override
    public Integer getValue() {
        return pendingAddressValue.run().getValue();
    }

    @Override
    public Address getReference() {
        return pendingAddressValue.run();
    }
}
