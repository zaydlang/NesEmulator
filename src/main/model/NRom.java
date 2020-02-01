package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class NRom implements Mapper {
    private static final int HEADER_SIZE         = 16;    // bytes
    private static final int TRAINER_SIZE        = 512;   // bytes
    private static final int PRG_ROM_SIZE        = 16384; // bytes
    private static final int CHR_ROM_SIZE        = 8192;  // bytes

    private static final int NROM_RAM_LENGTH     = Integer.parseInt("2000", 16);
    private static final int NROM_ROM_128_LENGTH = Integer.parseInt("4000", 16);
    private static final int NROM_ROM_256_LENGTH = Integer.parseInt("4000", 16);

    // TODO: is it right to have these at default visibility?
    int[] header;
    int[] trainer;
    int[] prgRam;
    int[] chrRom;
    int[] prgRom;

    public NRom() {
        header  = new int[HEADER_SIZE];
        trainer = new int[TRAINER_SIZE];
        prgRam  = new int[NROM_RAM_LENGTH];
        chrRom  = new int[CHR_ROM_SIZE];
        prgRom  = new int[NROM_ROM_128_LENGTH + NROM_ROM_256_LENGTH];
    }

    // MODIFIES: header, trainer, prgRom, chrRom
    // REQUIRES: the file associated with cartridgeName is a valid iNES NROM-mapper cartridge. Throws IOException
    // otherwise.
    // EFFECTS: the header, trainer, prgRom, and chrRom are extracted from the iNES file.
    public void loadCartridge(String cartridgeName) {
        // https://wiki.nesdev.com/w/index.php/INES
        // iNES file format:
        // SIZE            | DEVICE
        // 16 bytes        | Header
        // 0 or 512 bytes  | Trainer
        // 16384 * x bytes | PRG ROM data
        // 8192 * y bytes  | CHR ROM data
        try {
            FileInputStream file = new FileInputStream(CARTRIDGE_LOCATION + cartridgeName);
            header = readFile(file, 0, HEADER_SIZE);
            if (Util.getNthBit(header[6], 2) == 1) { // If the "trainer is present" flag is set
                trainer = readFile(file, 0, TRAINER_SIZE);
            }
            prgRom = readFile(file, 0, header[4] * PRG_ROM_SIZE);
            chrRom = readFile(file, 0, header[5] * CHR_ROM_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // REQUIRES: file has at least numBytes available, otherwise throws IOException.
    // MODIFIES: file now has numBytes less bytes available
    // EFFECTS: wrapper class for FileInputStream.read(). returns int[] result instead of bytes[] by reading
    //          numBytes from the file with the specified offset.
    public int[] readFile(FileInputStream file, int offset, int numBytes) throws IOException {
        int[] result = new int[numBytes];
        for (int i = offset; i < offset + numBytes; i++) {
            result[i - offset] = file.read();
        }
        return result;
    }

    // REQUIRES: address is in between 0x6000 and 0xFFFF, inclusive.
    // EFFECTS: returns the value of the memory at the given address.
    // see the table below for a detailed description of what is stored at which address.
    public int readMemory(int address) {
        // https://wiki.nesdev.com/w/index.php/NROM
        // ADDRESS RANGE | SIZE  | DEVICE
        // $6000 - $7FFF | $2000 | PRG RAM, mirrored as necessary to fill entire 8 KiB window
        // $8000 - $BFFF | $4000 | First 16 KB of ROM
        // $C000 - $F7FF | $4000 | Last 16 KB of ROM (for NROM-256). Else, this is a mirror of the first 16 KB.
        if (address <= Integer.parseInt("7FFF", 16)) {   // PRG RAM
            return prgRam[address - Integer.parseInt("6000", 16)];
        } else {                                                   // PRG ROM. mirrored for NROM-128 in the constructor.
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
        // $C000 - $F7FF | $4000 | Last 16 KB of ROM (for NROM-256). Else, this is a mirror of the first 16 KB.
        int value = rawValue % 256;
        if (value < 0) {
            value += 256;
        }

        if (address <= Integer.parseInt("7FFF", 16)) {   // PRG RAM
            prgRam[address - Integer.parseInt("6000", 16)] = value;
        } else {                                                   // PRG ROM. mirrored for NROM-128 in the constructor.
            prgRom[address - Integer.parseInt("8000", 16)] = value;
        }
    }

    // REQUIRES: index ranges from 0 to 16, inclusive
    // EFFECTS: returns the header
    public int getHeader(int index) {
        return header[index];
    }

    // REQUIRES: index ranges from 0 to 512, inclusive
    // EFFECTS: returns the trainer
    public int getTrainer(int index) {
        return trainer[index];
    }

    // REQUIRES: index ranges from 0 to NROM_RAM_LENGTH, inclusive
    // EFFECTS: returns the PRG RAM
    public int getPrgRam(int index) {
        return prgRam[index];
    }

    // REQUIRES: index ranges from 0 to 8192 * y, inclusive
    // EFFECTS: returns the CHR ROM
    public int getChrRom(int index) {
        return chrRom[index];
    }

    // REQUIRES: index ranges from 0 to 16384 * x, inclusive.
    // EFFECTS: returns the PRG ROM
    public int getPrgRom(int index) {
        return prgRom[index];
    }

    // REQUIRES: value ranges from 0x00 to 0xFF, inclusive
    //           index ranges from 0 to 16, inclusive.
    // MODIFIES: header
    // EFFECTS: sets the header at the specified index to the specified value
    public void setHeader(int index, int value) {
        this.header[index] = value;
    }

    // REQUIRES: value ranges from 0x00 to 0xFF, inclusive
    //           index ranges from 0 to 512, inclusive.
    // MODIFIES: trainer
    // EFFECTS: sets the HEADER at the specified index to the specified value
    public void setTrainer(int index, int value) {
        this.trainer[index] = value;
    }

    // REQUIRES: value ranges from 0x00 to 0xFF, inclusive
    //           index ranges from 0 to NROM_RAM_LENGTH, inclusive.
    // MODIFIES: PRG RAM
    // EFFECTS: sets the PRG RAM at the specified index to the specified value
    public void setPrgRam(int index, int value) {
        this.prgRam[index] = value;
    }

    // REQUIRES: value ranges from 0x00 to 0xFF, inclusive
    //           index ranges from 0 to 8192 * y, inclusive.
    // MODIFIES: CHR ROM
    // EFFECTS: sets the CHR ROM at the specified index to the specified value
    public void setChrRom(int index, int value) {
        this.chrRom[index] = value;
    }

    // REQUIRES: value ranges from 0x00 to 0xFF, inclusive
    //           index ranges from 0 to 16384 * x, inclusive.
    // MODIFIES: PRG ROM
    // EFFECTS: sets the PRG ROM at the specified index to the specified value
    public void setPrgRom(int index, int value) {
        this.prgRom[index] = value;
    }
}
