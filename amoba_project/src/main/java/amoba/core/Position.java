package amoba.core;

import java.util.Locale;
import java.util.Objects;

/**
 * Immutable value object representing a board coordinate.
 * Format: letter+number like a5 (column letter, row number)
 */
public final class Position {
    private final int row; // 0-based
    private final int col; // 0-based

    public Position(int row, int col) {
        if (row < 0 || col < 0) throw new IllegalArgumentException("Negative coordinates");
        this.row = row;
        this.col = col;
    }

    public int row() { return row; }
    public int col() { return col; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position p)) return false;
        return row == p.row && col == p.col;
    }

    @Override
    public int hashCode() { return Objects.hash(row, col); }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%d,%d", row, col);
    }

    public static Position fromString(String s) {
        s = s.trim().toLowerCase(Locale.ROOT);
        if (s.length() < 2) throw new IllegalArgumentException("Invalid pos");
        char c = s.charAt(0);
        if (c < 'a' || c > 'z') throw new IllegalArgumentException("Invalid column");
        int col = c - 'a';
        int row;
        try {
            row = Integer.parseInt(s.substring(1)) - 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row");
        }
        return new Position(row, col);
    }
}
