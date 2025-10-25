package amoba.core;

import java.util.Optional;
import java.util.Random;

/**
 * Game orchestrator: manages turns, winner detection and AI move.
 */
public class Game {
    private Board board;
    private Player human;
    private Player ai;
    private Player current;
    private Player winner;

    private final Random rnd = new Random();

    public Game(Board board, Player human, Player ai) {
        this.board = board;
        this.human = human;
        this.ai = ai;
        this.current = human; // human starts
    }

    public Player currentPlayer() { return current; }

    public void setBoard(Board b) { this.board = b; }

    public boolean playAt(Position p) {
        if (!board.isValidMove(p)) return false;
        board.place(p, current);
        checkWinnerInternal();
        switchTurn();
        return true;
    }

    public void playAiMove() {
        // naive random AI that picks among valid moves uniformly
        var empties = board.emptyPositions();
        var valid = empties.stream().filter(board::isValidMove).toList();
        if (valid.isEmpty()) { switchTurn(); return; }
        Position pick = valid.get(rnd.nextInt(valid.size()));
        board.place(pick, current);
        System.out.println("Gép lerakott: " + (char)('a'+pick.col()) + (pick.row()+1));
        checkWinnerInternal();
        switchTurn();
    }

    private void switchTurn() {
        if (winner != null) return;
        current = current == human ? ai : human;
    }

    private void checkWinnerInternal() {
        Optional<Player> w = board.checkWinner();
        if (w.isPresent()) {
            winner = w.get();
        }
    }

    public Optional<Player> winner() { return Optional.ofNullable(winner); }

    public boolean isFinished() {
        if (winner != null) return true;
        // no valid moves?
        boolean anyValid = board.allPositions().stream().anyMatch(board::isEmpty);
        return !anyValid;
    }
}
