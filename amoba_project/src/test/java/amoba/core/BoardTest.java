package amoba.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class BoardTest {

    @Test
    public void testInitialCenterPlacement() {
        Board b = new Board(10,10);
        b.placeInitialCenter();
        int r = b.rows()/2, c = b.cols()/2;
        assertEquals('x', b.at(r,c));
    }

    @Test
    public void testValidMoveRequiresContact() {
        Board b = new Board(7,5);
        b.placeInitialCenter();
        // position far away should be invalid
        Position p = new Position(0,0);
        assertFalse(b.isValidMove(p));
        // adjacent diagonal to center should be valid (touching)
        Position centerAdj = new Position(b.rows()/2 -1, b.cols()/2 -1);
        assertTrue(b.isValidMove(centerAdj));
    }

    @Test
    public void testWinnerDetectionHorizontal() {
        Board b = new Board(6,5);
        // place four x in a row
        int r = 2;
        for (int c=1;c<=4;c++) b.place(new Position(r,c), Player.X);
        assertTrue(b.checkWinner().isPresent());
        assertEquals(Player.X, b.checkWinner().get());
    }

}
