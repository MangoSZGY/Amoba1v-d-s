package amoba.persistence;

import amoba.core.Board;
import amoba.core.Position;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Simple text persistence for boards.
 * Format: each row as a line of characters (cols characters).
 */
public class BoardPersistence {
    public static void saveBoard(Board b, java.nio.file.Path path) {
        StringBuilder sb = new StringBuilder();
        for (int r=0;r<b.rows();r++) {
            for (int c=0;c<b.cols();c++) sb.append(b.at(r,c));
            sb.append(System.lineSeparator());
        }
        try {
            Files.writeString(path, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Mentési hiba: " + e.getMessage());
        }
    }

    public static Optional<Board> loadBoard(java.nio.file.Path path) {
        if (!Files.exists(path)) return Optional.empty();
        try {
            List<String> lines = Files.readAllLines(path);
            // attempt to infer size from file
            int rows = lines.size();
            int cols = lines.get(0).length();
            return Optional.of(Board.fromText(rows, cols, lines));
        } catch (IOException e) {
            System.err.println("Betöltési hiba: " + e.getMessage());
            return Optional.empty();
        }
    }
}
