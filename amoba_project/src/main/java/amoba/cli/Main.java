package amoba.cli;

import amoba.core.*;
import amoba.persistence.BoardPersistence;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Amőba NxM - egyszerű parancssoros változat");
        Scanner sc = new Scanner(System.in);
        int rows = 10, cols = 10;
        System.out.print("Adja meg a tábla méreteit (például 10 10) [ENTER = 10 10]: ");
        String line = sc.nextLine().trim();
        if (!line.isEmpty()) {
            try {
                String[] parts = line.split("\\s+");
                rows = Integer.parseInt(parts[0]);
                cols = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                System.out.println("Érvénytelen bemenet, alapértelmezett 10x10 lesz.");
                rows = 10; cols = 10;
            }
        }
        Board board = new Board(rows, cols);
        // try to load from a default file if exists
        Optional<Board> fromFile = BoardPersistence.loadBoard(Path.of("board.txt"));
        if (fromFile.isPresent()) {
            board = fromFile.get();
            System.out.println("Betöltve board.txt-ből a játékállás.");
        } else {
            board.placeInitialCenter();
        }

        Player human = Player.X;
        Player aiPlayer = Player.O;
        Game game = new Game(board, human, aiPlayer);
        System.out.print("Adja meg a játékos nevét: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = "Játékos1";
        System.out.println("Játék indul. X = te, o = gép. Te kezdhetsz.");
        while (!game.isFinished()) {
            System.out.println(board.render());
            if (game.currentPlayer() == human) {
                System.out.print("Lépés (pl. a5) vagy parancs (save, load, exit): ");
                String cmd = sc.nextLine().trim();
                if (cmd.equalsIgnoreCase("exit")) {
                    System.out.println("Kilépés, mentés automatikus board.txt-be.");
                    BoardPersistence.saveBoard(board, Path.of("board.txt"));
                    break;
                } else if (cmd.equalsIgnoreCase("save")) {
                    BoardPersistence.saveBoard(board, Path.of("board.txt"));
                    System.out.println("Mentve: board.txt");
                    continue;
                } else if (cmd.equalsIgnoreCase("load")) {
                    Optional<Board> loaded = BoardPersistence.loadBoard(Path.of("board.txt"));
                    if (loaded.isPresent()) {
                        board = loaded.get();
                        game.setBoard(board);
                        System.out.println("Betöltve board.txt");
                    } else {
                        System.out.println("Nincs board.txt fájl.");
                    }
                    continue;
                } else {
                    try {
                        Position p = Position.fromString(cmd);
                        boolean ok = game.playAt(p);
                        if (!ok) System.out.println("Érvénytelen lépés, próbáld újra.");
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Érvénytelen koordináta formátum. Használat: a5");
                    }
                }
            } else {
                System.out.println("Gép lép...");
                game.playAiMove();
            }
        }

        System.out.println(board.render());
        if (game.winner().isPresent()) {
            System.out.println("Győztes: " + game.winner().get());
        } else {
            System.out.println("Döntetlen vagy megszakított játék.");
        }
        // save score basic text file (append)
        Scoreboard.saveWin(Path.of("scores.txt"), name, game.winner());
        System.out.println("Köszönöm, a játék véget ért.");
    }
}
