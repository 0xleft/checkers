package uk.wwws.checkers.game;

import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.wwws.checkers.game.exceptions.InvalidMoveException;
import uk.wwws.checkers.game.moves.CheckersMove;
import uk.wwws.checkers.game.moves.CheckersMoveGenerator;
import uk.wwws.checkers.game.moves.Move;
import uk.wwws.checkers.game.players.HumanPlayer;

import static org.junit.jupiter.api.Assertions.*;

public class CheckersGameTests {
    private CheckersGame game;
    private Player player1;
    private Player player2;

    @BeforeEach
    public void setUp() {
        game = new CheckersGame();
        player1 = new HumanPlayer();
        player2 = new HumanPlayer();
    }

    @Test
    public void testAddPlayer() {
        assertEquals(0, game.getPlayers().size());
        game.addPlayer(player1, Checker.WHITE);
        assertEquals(1, game.getPlayers().size());
        assertEquals(player1, game.getPlayer(Checker.WHITE));

        game.addPlayer(player2, Checker.EMPTY);
        assertEquals(1, game.getPlayers().size());

        game.addPlayer(player2, Checker.WHITE);
        assertEquals(1, game.getPlayers().size());
    }

    @Test
    public void testSetLoser() {
        game.addPlayer(player1, Checker.WHITE);
        game.addPlayer(player2, Checker.BLACK);
        assertNull(game.getWinner());

        game.setSetLoser(player1);
        assertEquals(player2, game.getWinner());
    }

    @Test
    public void testDoMove() {
        assertThrows(InvalidMoveException.class, () -> game.doMove(new CheckersMove(0, 0)));
        assertThrows(InvalidMoveException.class, () -> game.doMove(new Move() {}));


        CheckersMove move = ((CheckersMove) game.getValidMoves().stream().findFirst().get());
        Checker original = game.getBoard().getField(move.startIndex());
        game.doMove(move);
        assertEquals(original, game.getBoard().getField(move.endIndex()));
        assertEquals(Checker.EMPTY, game.getBoard().getField(move.startIndex()));
        // todo test promotion
        // todo test capture
    }

    @Test
    public void testGetTurn() {
        game.addPlayer(player1, Checker.WHITE);
        game.addPlayer(player2, Checker.BLACK);

        assertEquals(player1, game.getTurn());
        game.doMove(game.getValidMoves().stream().findFirst().get());
        assertEquals(player2, game.getTurn());
    }

    @Test
    public void testValidMoves() {
        HashSet<Move> moves = game.getValidMoves();
        assertEquals(7, moves.size());

        game.getBoard().empty();
        moves = game.getValidMoves();
        assertEquals(0, moves.size());

        // todo add more complex positions.
    }

    @Test
    public void testGetWinner() {

    }

    @Test
    public void testToString() {
    }
}
