package checkers.game;

import uk.wwws.checkers.game.Board;
import uk.wwws.checkers.game.Checker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.wwws.checkers.game.bitboards.Bitboard;

import static org.junit.jupiter.api.Assertions.*;

public class BitboardTests {

    private static final int BOARD_DIM = 8;
    private Bitboard bitboard1;
    private Bitboard bitboard2;
    private Board board;

    @BeforeEach
    public void setUp() {
        bitboard1 = new Bitboard(BOARD_DIM);
        bitboard2 = new Bitboard(BOARD_DIM);
        board = new Board();
        board.empty();

        board.setField(5, 5, Checker.WHITE);

        bitboard1.set(0);
        bitboard2.set(1);
    }

    @Test
    public void testFrom() {
        bitboard1.from(bitboard2);

        assertEquals(bitboard2, bitboard1);
    }

    @Test
    public void testConstructors() {
        Bitboard newBitboard = new Bitboard(bitboard1, BOARD_DIM);
        assertEquals(newBitboard, bitboard1);

        Bitboard allPieces = new Bitboard(board.getCheckers(), null, BOARD_DIM);
        assertEquals(1, allPieces.cardinality());

        Bitboard whitePieces = new Bitboard(board.getCheckers(), Checker.WHITE, BOARD_DIM);
        assertEquals(1, whitePieces.cardinality());

        Bitboard blackPieces = new Bitboard(board.getCheckers(), Checker.BLACK, BOARD_DIM);
        assertEquals(0, blackPieces.cardinality());
    }

    @Test
    public void testRay() {
        Bitboard ray = Bitboard.diagonalRay(-2, 0, 0, false, BOARD_DIM);
        assertEquals(1, ray.cardinality());

        ray = Bitboard.diagonalRay(-2, 5, 5, false, BOARD_DIM);
        assertEquals(2, ray.cardinality());

        ray = Bitboard.diagonalRay(-2, 5, 5, true, BOARD_DIM);
        assertEquals(2, ray.cardinality());

        ray = Bitboard.diagonalRay(2, 5, 5, false, BOARD_DIM);
        assertEquals(2, ray.cardinality());

        ray = Bitboard.diagonalRay(2, 5, 5, true, BOARD_DIM);
        assertEquals(2, ray.cardinality());

        ray = Bitboard.diagonalRay(1000, 0, 0, true, BOARD_DIM);
        assertEquals(BOARD_DIM, ray.cardinality());

        ray = Bitboard.diagonalRay(-1000, 0, 0, false, BOARD_DIM);
        assertEquals(1, ray.cardinality());
    }

    @Test
    public void testPos() {
        Bitboard original = new Bitboard(bitboard1, bitboard1.getBoardDim());

        bitboard1.setPos(1, 1, true);
        assertNotEquals(original, bitboard1);

        bitboard1.setPos(1, 1, false);
        bitboard1.setPos(-1, -1, true);
        assertEquals(bitboard1, original);

        bitboard1.setPos(1, -1, true);
        assertEquals(bitboard1, original);

        bitboard1.setPos(8, 8, true);
        assertEquals(bitboard1, original);

        bitboard1.setPos(8, 9, true);
        assertEquals(bitboard1, original);

        bitboard1.setPos(0, 9, true);
        assertEquals(bitboard1, original);
    }

    @Test
    public void testAnd() {
        Bitboard andedBitboard = bitboard1.and(bitboard1);
        assertEquals(andedBitboard, bitboard1);

        andedBitboard = bitboard1.and(bitboard2);
        assertEquals(0, andedBitboard.cardinality());
    }

    @Test
    public void testNot() {
        Bitboard notBitboard1 = bitboard1.not();
        assertEquals(63, notBitboard1.cardinality());

        notBitboard1 = new Bitboard(BOARD_DIM).not();
        assertEquals(64, notBitboard1.cardinality());

        assertEquals(0, notBitboard1.not().cardinality());
    }

    @Test
    public void testIndexes() {
        assertEquals(1, bitboard1.getOnIndexes().size());

        assertTrue(bitboard1.getOnIndexes().stream().findFirst().isPresent());
        assertEquals(0, bitboard1.getOnIndexes().stream().findFirst().get());

        assertEquals(63, bitboard1.getOffIndexes().size());

        bitboard1.clear();
        bitboard1.set(55);
        assertEquals(55, bitboard1.getOnIndexes().stream().findFirst().get());

        bitboard1.set(32);
        assertEquals(2, bitboard1.getOnIndexes().size());
    }

    @Test
    public void testShifts() {
        assertEquals(1, bitboard1.shiftH(1).getOnIndexes().stream().findFirst().get());
        assertEquals(0, bitboard2.shiftH(-1).getOnIndexes().stream().findFirst().get());

        assertEquals(8, bitboard1.shiftV(1).getOnIndexes().stream().findFirst().get());
        assertEquals(0, bitboard1.shiftV(-1).getOnIndexes().size());

        bitboard1.clear();
        bitboard1.setPos(5, 5, true);
        assertEquals(4 * BOARD_DIM + 5, bitboard1.shiftV(-1).getOnIndexes().stream().findFirst().get());

        bitboard1.clear();
        bitboard1.setPos(5, 5, true);
        assertEquals(6 * BOARD_DIM + 5, bitboard1.shiftV(1).getOnIndexes().stream().findFirst().get());
    }

    @Test
    public void testToString() {
        assertTrue(bitboard1.toString().contains("1"));
        bitboard1.clear();
        assertFalse(bitboard1.toString().contains("1"));

        assertEquals(bitboard1.getBoardDim(), bitboard1.toString().split("\n").length);
    }

    @Test
    public void testEquals() {
        assertFalse(bitboard1.equals(bitboard2));
        assertTrue(bitboard1.equals(bitboard1));

        assertFalse(bitboard1.equals(board));
    }
}
