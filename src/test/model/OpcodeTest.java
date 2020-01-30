package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpcodeTest {
    CPU cpu;

    @BeforeEach
    void runBefore() {
        cpu = new CPU();
    }
}