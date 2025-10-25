package amoba.core;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

public class Scoreboard {
    public static void saveWin(java.nio.file.Path path, String playerName, Optional<Player> winner) {
        try {
            String line;
            if (winner.isPresent() && winner.get()==Player.X) {
                line = playerName + ":1" + System.lineSeparator();
            } else if (winner.isPresent()) {
                line = "AI:1" + System.lineSeparator();
            } else {
                line = "DRAW:1" + System.lineSeparator();
            }
            Files.writeString(path, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Score mentési hiba: " + e.getMessage());
        }
    }
}
