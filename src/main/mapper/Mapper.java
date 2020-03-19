package mapper;

import model.Address;

public abstract class Mapper {
    protected Address[] prgRom;
    protected Address[] prgRam;
    protected Address[] chrRom;
    private final int id;

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

    public abstract void    writeMemory(int address, int value);

    public abstract String serialize(String delimiter);
}
