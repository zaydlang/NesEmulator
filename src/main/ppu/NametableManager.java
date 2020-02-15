package ppu;

import com.sun.tools.corba.se.idl.InterfaceGen;
import model.Address;

public class NametableManager {
    // Constants
    private static final int NAMETABLE_OFFSET        = Integer.parseInt("2000", 16);
    private static final int NAMETABLE_SIZE          = Integer.parseInt("0400", 16);
    private static final int INITIAL_NAMETABLE_STATE = Integer.parseInt("0000", 16);

    private static final int NUMBER_OF_NAMETABLES    = 4;

    // Fields
    private Nametable[] nametables;
    private Mirroring mirroring;

    public NametableManager(Mirroring mirroring) {
        this.mirroring = mirroring;

        setupNametables();
        setupNametableMirroring();
    }

    private void setupNametables() {
        for (int i = 0; i < NAMETABLE_SIZE; i++) {
            nametables[i] = new Nametable();
        }
    }

    private void setupNametableMirroring() {
        switch (mirroring) {
            case VERTICAL:
                nametables[0] = nametables[2];
                nametables[1] = nametables[3];
            case HORIZONTAL:
                nametables[0] = nametables[1];
                nametables[2] = nametables[3];
            case ONE_SCREEN:
                nametables[1] = nametables[0];
                nametables[2] = nametables[0];
                nametables[3] = nametables[0];
        }
    }
}
