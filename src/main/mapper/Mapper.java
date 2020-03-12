package mapper;

import com.sun.jdi.Mirror;
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

    public Address readMemoryCpu(int address) {
        return new Address(0);
    }
    public Address readMemoryPpu(int address) {
        return new Address(0);
    }

    public void writeMemory(int address, int value) {
        return;
    }

    public Address readChrRom(int address) {
        return new Address(0);
    }
}
