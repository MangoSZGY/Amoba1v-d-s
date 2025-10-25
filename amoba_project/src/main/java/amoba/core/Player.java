package amoba.core;

public enum Player {
    X('x'), O('o');

    private final char symbol;
    Player(char s) { this.symbol = s; }
    public char symbol() { return symbol; }
    @Override
    public String toString() { return String.valueOf(symbol); }
}
