package mapper;

import model.Address;
import ppu.Mirroring;

import java.io.FileInputStream;
import java.io.IOException;

public abstract class Mapper {
    protected Address[] prgRom;
    protected Address[] prgRam;
    protected Address[] chrRom;

    public Mapper(Address[] prgRom, Address[] chrRom) {
        this.prgRom    = prgRom;
        this.chrRom    = chrRom;
    }

    public abstract Address readMemoryCpu(int address);

    public abstract Address readMemoryPpu(int address);

    public abstract void    writeMemory(int address, int value);
}
