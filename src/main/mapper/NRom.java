package mapper;

import java.util.Scanner;

// Class NROM:
//     NROM models an NROM Mapper. Can read through an NROM NES file, both NROM-128 and NROM-256.
//     See for more details: https://wiki.nesdev.com/w/index.php/NROM

public class NRom extends Mapper {
    public  static final int PRG_ROM_128_SIZE      = 0x4000;

    public  static final int INITIAL_PRG_RAM_STATE = 0x00;
    public  static final int PRG_RAM_SIZE          = 0x2000;

    private static final int ID                    = 000;

    private boolean isNRom128;

    // MODIFIES: this
    // EFFECTS: sets the ID to the given ID
    public NRom() {
        super(ID);
    }

    // MODIFIES: this
    // EFFECTS:  initialzies header, trainer, chrRom, and prgRom as empty arrays, and sets the NROM type to NROM-256.
    //           fills the prgRam with the initial state.
    public NRom(int[] prgRom, int[] chrRom) {
        super(prgRom, chrRom, ID);

        prgRam  = new int[PRG_RAM_SIZE];
        for (int i = 0; i < PRG_RAM_SIZE; i++) {
            prgRam[i] = INITIAL_PRG_RAM_STATE;
        }
        isNRom128 = prgRom.length <= PRG_ROM_128_SIZE;
        enable();
    }

    // REQUIRES: address is in between 0x6000 and 0xFFFF, inclusive.
    // EFFECTS: returns the value of the memory at the given address.
    // see the table below for a detailed description of what is stored at which address.
    @Override
    public int readMemoryCpu(int address) {
        if (!getEnabled()) {
            return 0;
        }

        // https://wiki.nesdev.com/w/index.php/NROM
        // ADDRESS RANGE | SIZE  | DEVICE
        // $6000 - $7FFF | $2000 | PRG RAM, mirrored as necessary to fill entire 8 KiB window
        // $8000 - $BFFF | $4000 | First 16 KB of ROM
        // $C000 - $FFFF | $4000 | Last 16 KB of ROM (for NROM-256). Else, this is a mirror of the first 16 KB.
        if (address < 0x6000) {            // Out of bounds
            return 0;
        } else if (address <= 0x7FFF) {    // PRG RAM
            return prgRam[(address - 0x6000)];
        } else {                                                           // PRG ROM
            if (isNRom128) {
                return prgRom[(address - 0x8000) & (PRG_ROM_128_SIZE - 1)];
            } else {
                return prgRom[address - 0x8000];
            }
        }
    }

    // REQUIRES: address is in between 0x6000 and 0xFFFF, inclusive.
    // EFFECTS: returns the value of the memory at the given address.
    // see the table below for a detailed description of what is stored at which address.
    @Override
    public int readMemoryPpu(int address) {
        return chrRom[address];
    }

    // REQUIRES: address is in between 0x6000 and 0xFFFF, inclusive.
    // MODIFIES: prgRam, prgRom
    // EFFECTS: check the table below for a detailed explanation of what is affected and how.
    @Override
    public void writeMemory(int address, int rawValue) {
        if (!getEnabled()) {
            return;
        }

        // https://wiki.nesdev.com/w/index.php/NROM
        // ADDRESS RANGE | SIZE  | DEVICE
        // $6000 - $7FFF | $2000 | PRG RAM, mirrored as necessary to fill entire 8 KiB window
        // $8000 - $BFFF | $4000 | First 16 KB of ROM
        // $C000 - $FFFF | $4000 | Last 16 KB of ROM (for NROM-256). Else, this is a mirror of the first 16 KB.

        if        (address < 0x6000) {    // Out of Bounds
            throw new ArrayIndexOutOfBoundsException("int out of bounds! NROM only supports addresses >= 0x6000");
        } else if (address <= 0x7FFF) {   // PRG RAM
            prgRam[address - 0x6000] = (rawValue);
        } else {                                                         // PRG ROM. mirrored for NROM-128.
            throw new ArrayIndexOutOfBoundsException("Cannot write to a Read-Only int!");
        }
    }

    // EFFECTS: serializes the NRom, storing the prgRom, prgRam, chrRom, and isNRom128 into a String.
    @Override
    public String serialize(String delimiter) {
        StringBuilder output = new StringBuilder();
        output.append(prgRom.length + delimiter);
        for (int address : prgRom) {
            output.append(address + delimiter);
        }
        output.append(prgRam.length + delimiter);
        for (int address : prgRam) {
            output.append(address + delimiter);
        }
        output.append(chrRom.length + delimiter);
        for (int address : chrRom) {
            output.append(address + delimiter);
        }
        output.append(isNRom128 ? 1 : 0 + delimiter);
        return output.toString();
    }

    // EFFECTS: deserializes the NRom to restore it from a savestate
    @Override
    public void deserialize(Scanner scanner) {
        prgRom = new int[Integer.parseInt(scanner.next())];
        for (int i = 0; i < prgRom.length; i++) {
            prgRom[i] = Integer.parseInt(scanner.next());
        }
        prgRam = new int[Integer.parseInt(scanner.next())];
        for (int i = 0; i < prgRam.length; i++) {
            prgRam[i] = Integer.parseInt(scanner.next());
        }
        chrRom = new int[Integer.parseInt(scanner.next())];
        for (int i = 0; i < chrRom.length; i++) {
            chrRom[i] = Integer.parseInt(scanner.next());
        }
        isNRom128 = scanner.next().equals("1");
        enable();
    }
}
