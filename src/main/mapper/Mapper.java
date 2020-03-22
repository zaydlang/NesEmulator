package mapper;

import model.Address;
import persistence.BusSerializable;

import java.util.Scanner;

public abstract class Mapper implements BusSerializable {
    protected Address[] prgRom;
    protected Address[] prgRam;
    protected Address[] chrRom;
    private boolean enabled;
    private final int id;

    public Mapper(int id) {
        this.id      = id;
        this.enabled = false;
    }

    public Mapper(Address[] prgRom, Address[] chrRom, int id) {
        this.prgRom    = prgRom;
        this.chrRom    = chrRom;
        this.id        = id;
    }

    public int getId() {
        return id;
    }

    public abstract Address readMemoryCpu(int address);

    public abstract Address readMemoryPpu(int address);

    public abstract void writeMemory(int address, int value);

    @Override
    public abstract String serialize(String delimiter);

    @Override
    public abstract void deserialize(Scanner scanner);

    public static Mapper getMapper(int id) {
        // There's a lot of mappers, so I have this system in place for later.
        switch (id) {
            case 000:
                return new NRom();
        }

        return null;
    }

    protected void enable() {
        enabled = true;
    }

    protected boolean getEnabled() {
        return enabled;
    }
}
