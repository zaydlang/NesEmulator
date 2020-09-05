package mapper;

import persistence.BusSerializable;

import java.util.Scanner;

// Class Mapper:
//     Mapper is an abstract, serializable class that represents a cartridge mapper for the NES.
//     Serves as a mapping between CPU intes to the cartridge intes.

public abstract class Mapper implements BusSerializable {
    protected int[] prgRom;
    protected int[] prgRam;
    protected int[] chrRom;
    private boolean enabled;
    private final int id;

    public Mapper(int id) {
        this.id      = id;
        this.enabled = false;
    }

    public Mapper(int[] prgRom, int[] chrRom, int id) {
        this.prgRom    = prgRom;
        this.chrRom    = chrRom;
        this.id        = id;
    }

    public int getId() {
        return id;
    }

    public abstract int readMemoryCpu(int address);

    public abstract int readMemoryPpu(int address);

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

    public int getChrRomSize() {
        return chrRom.length;
    }
}
