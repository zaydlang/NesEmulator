package mapper;

import model.Address;
import model.Util;

import java.io.FileInputStream;
import java.io.IOException;

// Class NROM:
//     NROM models an NROM Mapper. Can read through an NROM NES file, both NROM-128 and NROM-256.
//     See for more details: https://wiki.nesdev.com/w/index.php/NROM

public class NRom implements Mapper {
    public static final int HEADER_SIZE           = 16;    // bytes
    public static final int TRAINER_SIZE          = 512;   // bytes
    public static final int PRG_ROM_SIZE          = 16384; // bytes
    public static final int CHR_ROM_SIZE          = 8192;  // bytes
    public static final int PRG_ROM_128_SIZE      = Integer.parseInt("4000", 16);
    public static final int PRG_ROM_256_SIZE      = Integer.parseInt("8000", 16);

    public static final int INITIAL_PRG_RAM_STATE = Integer.parseInt("00",   16);
    public static final int PRG_RAM_SIZE          = Integer.parseInt("2000", 16);

    // TODO: is it right to have these at default visibility?
    public Address[] header;
    public Address[] trainer;
    public Address[] chrRom;
    public Address[] prgRom;

    public Address[] prgRam;

    private boolean isNRom128;

    // EFFECTS: initialzies header, trainer, chrRom, and prgRom as empty arrays, and sets the NROM type to NROM-256.
    // fills the prgRam with the initial state.
    public NRom() {
        header  = new Address[0];
        trainer = new Address[0];
        chrRom  = new Address[0];
        prgRom  = new Address[0];
        isNRom128 = false;

        prgRam  = new Address[PRG_RAM_SIZE];
        for (int i = 0; i < PRG_RAM_SIZE; i++) {
            prgRam[i] = new Address(INITIAL_PRG_RAM_STATE);
        }
    }

    // MODIFIES: header, trainer, prgRom, chrRom
    // REQUIRES: the file associated with cartridgeName is a valid iNES NROM-mapper cartridge. Throws IOException
    // otherwise.
    // EFFECTS: the header, trainer, prgRom, and chrRom are extracted from the iNES file.
    public void loadCartridge(String cartridgeName) throws IOException {
        // https://wiki.nesdev.com/w/index.php/INES
        // iNES file format:
        // SIZE            | DEVICE
        // 16 bytes        | Header
        // 0 or 512 bytes  | Trainer
        // 16384 * x bytes | PRG ROM data
        // 8192 * y bytes  | CHR ROM data
        FileInputStream file = new FileInputStream(CARTRIDGE_LOCATION + cartridgeName);
        loadHeader(readFile(file, 0, 0, HEADER_SIZE));

        boolean trainerPresent = Util.getNthBit(header[6].getValue(), 2) == 1;
        loadTrainer(readFile(file, 0, 0, trainerPresent ? TRAINER_SIZE : 0));

        loadPrgRom(readFile(file, 0, Integer.parseInt("8000", 16),header[4].getValue() * PRG_ROM_SIZE));
        loadChrRom(readFile(file, 0, 0,header[5].getValue() * CHR_ROM_SIZE));
    }

    // REQUIRES: data is exactly 16 bytes long.
    // EFFECTS: sets the header to the specified data.
    private void loadHeader(Address[] data) {
        this.header = data;
    }

    // REQUIRES: data is exactly either 0 or 512 bytes long.
    // EFFECTS: sets the trainer to the specified data.
    private void loadTrainer(Address[] data) {
        this.trainer = data;
    }

    // REQUIRES: data is exactly a multiple of 16384 bytes long.
    // EFFECTS: sets the PRG ROM to the specified data, and sets isNRom128 if the specified ROM is an NROM128 file.
    private void loadPrgRom(Address[] data) {
        isNRom128 = data.length == PRG_ROM_128_SIZE;

        if (isNRom128) {
            prgRom = new Address[PRG_ROM_256_SIZE];
            for (int i = 0; i < PRG_ROM_128_SIZE; i++) {
                Address addressOne = new Address(data[i].getValue(), data[i].getPointer());
                Address addressTwo = new Address(data[i].getValue(), data[i].getPointer() + PRG_ROM_128_SIZE);

                prgRom[i]                    = addressOne;
                prgRom[i + PRG_ROM_128_SIZE] = addressTwo;
            }
        } else {
            this.prgRom = data;
        }
    }

    // REQUIRES: data is exactly a multiple of 8192 bytes long.
    // EFFECTS: sets the CHR ROM to the specified data.
    private void loadChrRom(Address[] data) {
        this.chrRom = data;
    }

    // REQUIRES: file has at least numBytes available, otherwise throws IOException.
    // MODIFIES: file now has numBytes less bytes available
    // EFFECTS: wrapper class for FileInputStream.read(). returns int[] result instead of bytes[] by reading
    //          numBytes from the file with the specified offset.
    public Address[] readFile(FileInputStream file, int offset, int pointerOffset, int numBytes) throws IOException {
        Address[] result = new Address[numBytes];
        for (int i = offset; i < offset + numBytes; i++) {
            result[i - offset] = new Address(file.read(), pointerOffset + i);
        }
        return result;
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
            return prgRam[address - Integer.parseInt("6000", 16)];
        } else {                                                           // PRG ROM
            return prgRom[address - Integer.parseInt("8000", 16)];
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
        int value = rawValue % 256;
        if (value < 0) {
            value += 256;
        }

        if        (address < Integer.parseInt("6000", 16)) {    // Out of Bounds
            throw new ArrayIndexOutOfBoundsException("Address out of bounds! NROM only supports addresses >= 0x6000");
        } else if (address <= Integer.parseInt("7FFF", 16)) {   // PRG RAM
            prgRam[address - Integer.parseInt("6000", 16)].setValue(value);
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
