package persistence;

import java.util.Scanner;

public interface BusSerializable {
    String serialize(String delimiter);

    void deserialize(Scanner scanner);
}
