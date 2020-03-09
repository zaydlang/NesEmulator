package mapper;

import model.Address;
import model.Util;
import ppu.Mirroring;

import java.io.FileInputStream;
import java.io.IOException;

// Class NROM:
//     NROM models an NROM Mapper. Can read through an NROM NES file, both NROM-128 and NROM-256.
//     See for more details: https://wiki.nesdev.com/w/index.php/NROM

public class NRom extends Mapper {
    public static final int PRG_ROM_128_SIZE      = Integer.parseInt("4000", 16);
    public static final int PRG_ROM_256_SIZE      = Integer.parseInt("8000", 16);

    public static final int INITIAL_PRG_RAM_STATE = Integer.parseInt("00",   16);
    public static final int PRG_RAM_SIZE          = Integer.parseInt("2000", 16);

    private boolean isNRom128;

    // EFFECTS: initialzies header, trainer, chrRom, and prgRom as empty arrays, and sets the NROM type to NROM-256.
    // fills the prgRam with the initial state.
    public NRom(Mirroring mirroring, Address[] prgRom, Address[] chrRom) {
        super(mirroring, prgRom, chrRom);

        prgRam  = new Address[PRG_RAM_SIZE];
        for (int i = 0; i < PRG_RAM_SIZE; i++) {
            prgRam[i] = new Address(INITIAL_PRG_RAM_STATE);
        }
        isNRom128 = prgRom.length <= PRG_ROM_128_SIZE;
    }

    // REQUIRES: address is in between 0x6000 and 0xFFFF, inclusive.
    // EFFECTS: returns the value of the memory at the given address.
    // see the table below for a detailed description of what is stored at which address.
    public Address readMemory(int address) {
        // https://wiki.nesdev.com/w/index.php/NROM
        // ADDRESS RANGE | SIZE  | DEVICE
        // $6000 - $7FFF | $2000 | PRG RAM, mirrored as necessary to fill entire 8 KiB window
        // $8000 - $BFFF | $4000 | First 16 KB of ROM
        // $C000 - $FFFF | $4000 | Last 16 KB of ROM (for NROM-256). Else, this is a mirror of the first 16 KB.
        if (address < Integer.parseInt("6000", 16)) {            // Out of bounds
            return new Address(0, address);
        } else if (address <= Integer.parseInt("7FFF", 16)) {    // PRG RAM
            return prgRam[(address - Integer.parseInt("6000", 16))];
        } else {                                                           // PRG ROM
            if (isNRom128) {
                return prgRom[(address - Integer.parseInt("8000", 16)) % PRG_ROM_128_SIZE];
            } else {
                return prgRom[address - Integer.parseInt("8000", 16)];
            }
        }
    }

    // REQUIRES: address is in between 0x6000 and 0xFFFF, inclusive.
    // MODIFIES: prgRam, prgRom
    // EFFECTS: check the table below for a detailed explanation of what is affected and how.
    public void writeMemory(int address, int rawValue) {
        // https://wiki.nesdev.com/w/index.php/NROM
        // ADDRESS RANGE | SIZE  | DEVICE
        // $6000 - $7FFF | $2000 | PRG RAM, mirrored as necessary to fill entire 8 KiB window
        // $8000 - $BFFF | $4000 | First 16 KB of ROM
        // $C000 - $FFFF | $4000 | Last 16 KB of ROM (for NROM-256). Else, this is a mirror of the first 16 KB.

        if        (address < Integer.parseInt("6000", 16)) {    // Out of Bounds
            throw new ArrayIndexOutOfBoundsException("Address out of bounds! NROM only supports addresses >= 0x6000");
        } else if (address <= Integer.parseInt("7FFF", 16)) {   // PRG RAM
            prgRam[address - Integer.parseInt("6000", 16)].setValue(rawValue);
        } else {                                                         // PRG ROM. mirrored for NROM-128.
            throw new ArrayIndexOutOfBoundsException("Cannot write to a Read-Only Address!");
        }
    }

    // REQUIRES: index ranges from 0 to PRG_RAM_SIZE, inclusive
    // EFFECTS: returns the PRG RAM
    public Address getPrgRam(int index) {
        return prgRam[index];
    }

    // REQUIRES: value ranges from 0x00 to 0xFF, inclusive
    //           index ranges from 0 to PRG_RAM_SIZE, inclusive.
    // MODIFIES: PRG RAM
    // EFFECTS: sets the PRG RAM at the specified index to the specified value
    public void setPrgRam(int index, int value) {
        this.prgRam[index] = new Address(value);
    }

    // REQUIRES: value ranges from 0x00 to 0xFF, inclusive
    //           index ranges from 0 to 16384 * x, inclusive.
    // MODIFIES: PRG ROM
    // EFFECTS: sets the PRG ROM at the specified index to the specified value
    public void setPrgRom(int index, int value) {
        this.prgRom[index] = new Address(value);
    }

    @Override
    public Address readChrRom(int address) {
        return chrRom[address];
    }
}
