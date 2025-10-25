package amoba.core;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Board representation and basic rules.
 * - 5 <= M <= N <= 25 is suggested at construction time (enforced with exception)
 * - '.' empty, 'x' human, 'o' ai
 */
public class Board {
    private final int rows;
    private final int cols;
    private final char[][] grid;

    public Board(int rows, int cols) {
        if (cols < 5 || rows < cols || rows > 25) {
            // Relaxed: only enforce sensible bounds but allow caller to handle exact limits
            if (!(cols >= 5 && rows >= cols && rows <= 25)) {
                throw new IllegalArgumentException("Invalid board size. Require 5<=cols<=rows<=25");
            }
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) grid[r][c]='.';
    }

    public int rows() { return rows; }
    public int cols() { return cols; }

    public char at(int r, int c) { return grid[r][c]; }
    public Optional<Character> at(Position p) {
        if (!inBounds(p)) return Optional.empty();
        return Optional.of(grid[p.row()][p.col()]);
    }

    public boolean inBounds(Position p) {
        return p.row() >= 0 && p.row() < rows && p.col() >=0 && p.col() < cols;
    }

    public boolean isEmpty(Position p) {
        return inBounds(p) && grid[p.row()][p.col()] == '.';
    }

    public boolean place(Position p, Player player) {
        if (!inBounds(p) || grid[p.row()][p.col()] != '.') return false;
        grid[p.row()][p.col()] = player.symbol();
        return true;
    }

    public void placeInitialCenter() {
        int r = rows/2;
        int c = cols/2;
        grid[r][c] = Player.X.symbol();
    }

    public List<Position> allPositions() {
        List<Position> out = new ArrayList<>();
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) out.add(new Position(r,c));
        return out;
    }

    public List<Position> emptyPositions() {
        return allPositions().stream().filter(this::isEmpty).collect(Collectors.toList());
    }

    /**
     * A move is allowed if:
     * - The cell is empty and in bounds
     * - If there are any marks already on board, the new mark must touch at least diagonally one existing mark.
     * - The initial move is treated by placeInitialCenter, so here we only check contact if board not empty
     */
    public boolean isValidMove(Position p) {
        if (!inBounds(p)) return false;
        if (grid[p.row()][p.col()] != '.') return false;
        // If board empty -> must be center (enforced externally by placeInitialCenter)
        boolean any = false;
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) if (grid[r][c] != '.') any = true;
        if (!any) {
            int r = rows/2, c = cols/2;
            return p.row()==r && p.col()==c;
        }
        // must touch at least diagonally one existing mark
        for (int dr=-1; dr<=1; dr++) for (int dc=-1; dc<=1; dc++) {
            if (dr==0 && dc==0) continue;
            int nr = p.row()+dr, nc = p.col()+dc;
            if (nr>=0 && nr<rows && nc>=0 && nc<cols && grid[nr][nc] != '.') return true;
        }
        return false;
    }

    public Optional<Player> checkWinner() {
        // search for sequence of four for any player
        int need=4;
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) {
            char ch = grid[r][c];
            if (ch=='.') continue;
            // check directions: right, down, diag right-down, diag left-down
            int[][] dirs = {{0,1},{1,0},{1,1},{1,-1}};
            for (int[] d: dirs) {
                int cnt=1;
                int rr=r+d[0], cc=c+d[1];
                while (rr>=0 && rr<rows && cc>=0 && cc<cols && grid[rr][cc]==ch) {
                    cnt++; rr+=d[0]; cc+=d[1];
                }
                if (cnt>=need) return ch==Player.X.symbol() ? Optional.of(Player.X) : Optional.of(Player.O);
            }
        }
        return Optional.empty();
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        // header letters
        sb.append("   ");
        for (int c=0;c<cols;c++) sb.append((char)('a'+c)).append(' ');
        sb.append('\n');
        for (int r=0;r<rows;r++) {
            sb.append(String.format("%2d ", r+1));
            for (int c=0;c<cols;c++) {
                sb.append(grid[r][c]).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public char[][] snapshot() {
        char[][] out = new char[rows][cols];
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) out[r][c]=grid[r][c];
        return out;
    }

    public static Board fromText(int rows, int cols, List<String> lines) {
        Board b = new Board(rows, cols);
        for (int r=0; r<Math.min(rows, lines.size()); r++) {
            String line = lines.get(r);
            for (int c=0;c<Math.min(cols, line.length()); c++) {
                char ch = line.charAt(c);
                if (ch=='x' || ch=='o' || ch=='.') b.grid[r][c]=ch;
            }
        }
        return b;
    }
}
