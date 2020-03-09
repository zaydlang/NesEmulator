package mapper;

import com.sun.jdi.Mirror;
import model.Address;
import ppu.Mirroring;

import java.io.FileInputStream;
import java.io.IOException;

public abstract class Mapper {
    protected Mirroring mirroring;
    protected Address[] prgRom;
    protected Address[] prgRam;
    protected Address[] chrRom;

    public Mapper(Mirroring mirroring, Address[] prgRom, Address[] chrRom) {
        this.mirroring = mirroring;
        this.prgRom    = prgRom;
        this.chrRom    = chrRom;
    }

    public void mirrorNametables(Address[] nametables) {
        switch (mirroring) {
            case VERTICAL:
                for (int i = Integer.parseInt("0000", 16); i < Integer.parseInt("0400", 16); i++) {
                    nametables[i] = nametables[i + Integer.parseInt("0800", 16)];
                }
                for (int i = Integer.parseInt("0400", 16); i < Integer.parseInt("0800", 16); i++) {
                    nametables[i] = nametables[i + Integer.parseInt("0800", 16)];
                }
            case HORIZONTAL:
                for (int i = Integer.parseInt("0000", 16); i < Integer.parseInt("0400", 16); i++) {
                    nametables[i] = nametables[i + Integer.parseInt("0400", 16)];
                }
                for (int i = Integer.parseInt("0800", 16); i < Integer.parseInt("0C00", 16); i++) {
                    nametables[i] = nametables[i + Integer.parseInt("0400", 16)];
                }
        }
    }

    public Address readMemory(int address) {
        return new Address(0);
    }

    public void writeMemory(int address, int value) {
        return;
    }

    public Address readChrRom(int address) {
        return new Address(0);
    }
}
