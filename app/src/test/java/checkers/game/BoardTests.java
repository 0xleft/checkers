package checkers.game;

import uk.wwws.checkers.game.Board;
import uk.wwws.checkers.game.Checker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTests {
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testBoardInit() {
        assertEquals(64, board.getCheckers().length);
        board.empty();
        assertEquals(64, board.getCheckers().length);
    }

    @Test
    public void testToString() {
        assertEquals(Board.DIM, board.toString().split("\n").length);
        assertEquals(Board.DIM, board.toString().split("\n")[0].split(" ").length);
    }

    @Test
    public void testShouldPromote() {
        board.empty();
        assertFalse(board.shouldPromote(0));
        board.getCheckers()[0] = Checker.BLACK;
        assertFalse(board.shouldPromote(0));
        board.getCheckers()[0] = Checker.WHITE;
        assertTrue(board.shouldPromote(0));

        assertFalse(board.shouldPromote(63));
        board.getCheckers()[63] = Checker.WHITE;
        assertFalse(board.shouldPromote(63));
        board.getCheckers()[63] = Checker.BLACK;
        assertTrue(board.shouldPromote(63));
    }

    @RepeatedTest(100)
    public void testDistance() {
        int aRow = (int) (Math.random() * 100);
        int aCol = (int) (Math.random() * 100);
        int bRow = (int) (Math.random() * 100);
        int bCol = (int) (Math.random() * 100);

        double distance = Math.sqrt(Math.pow(bRow - aRow, 2) + Math.pow(bCol - aCol, 2));

        assertEquals(distance, board.getDistance(aRow, aCol, bRow, bCol));
    }

    @Test
    public void testOutOfBoundsEmpty() {
        assertNotEquals(Checker.EMPTY, board.getField(0));
        assertEquals(Checker.EMPTY, board.getField(-1));
        assertEquals(Checker.EMPTY, board.getField(Board.DIM * Board.DIM));
    }

    @Test
    public void testGetRow() {
        assertEquals(0, board.getRow(0));
        assertEquals(1, board.getRow(Board.DIM));
    }

    @Test
    public void testGetCol() {
        assertEquals(0, board.getCol(0));
        assertEquals(1, board.getCol(1));
        assertEquals(0, board.getCol(Board.DIM));
    }
}
