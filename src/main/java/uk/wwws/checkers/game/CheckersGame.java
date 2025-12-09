package uk.wwws.checkers.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.game.exceptions.InvalidMoveException;
import uk.wwws.checkers.game.moves.CheckersMove;
import uk.wwws.checkers.game.moves.CheckersMoveGenerator;
import uk.wwws.checkers.game.moves.Move;

public class CheckersGame implements Game {
    private final Board board;
    private final HashMap<Checker, Player> players = new HashMap<>();
    private Checker turn;
    private Player setLoser; // match fixing

    public CheckersGame() {
        board = new Board();
        turn = Checker.WHITE;
    }

    public void setSetLoser(@Nullable Player setLoser) {
        this.setLoser = setLoser;
    }

    @Override
    public boolean isGameOver() {
        return getWinner() != null;
    }

    @Override
    public @Nullable Player getTurn() {
        return players.get(turn);
    }

    public @NotNull Checker getSideToMove() {
        return turn;
    }

    private @Nullable Player getOtherPlayer(@NotNull Player player) {
        for (Player other : getPlayers()) {
            if (other != player) {
                return other;
            }
        }

        return null;
    }

    @Override
    public @Nullable Player getWinner() {
        if (setLoser != null) {
            return getOtherPlayer(setLoser);
        }

        if (getValidMoves().isEmpty()) {
            return players.get(turn.other());
        }

        return null;
    }

    @Override
    public @NotNull HashSet<Move> getValidMoves() {
        return CheckersMoveGenerator.getInstance().generateMoves(board, this.turn);
    }

    @Override
    public boolean isValidMove(@NotNull Move move) {
        if (move instanceof CheckersMove checkersMove) {
            return getValidMoves().contains(checkersMove);
        }

        return false;
    }

    private boolean isCapture(CheckersMove move) {
        return Math.abs(board.getRow(move.endIndex()) - board.getRow(move.startIndex())) == 2;
    }

    @Override
    public void doMove(@NotNull Move move) throws InvalidMoveException {
        if (!isValidMove(move)) {
            throw new InvalidMoveException("This is not a valid move");
        }
        if (move instanceof CheckersMove checkersMove) {
            if (isCapture(checkersMove)) {
                resolveCapture(checkersMove);
            }
            resolveMove(checkersMove);

            if (board.shouldPromote(checkersMove.endIndex())) {
                resolvePromotion(checkersMove);
            }

            this.turn = turn.other();
        }
    }

    @Override
    public @NotNull Board getBoard() {
        return board;
    }

    private void resolveCapture(@NotNull CheckersMove move) {
        board.setField(board.getRow(move.startIndex()) +
                               (board.getRow(move.endIndex()) - board.getRow(move.startIndex())) /
                                       2, board.getCol(move.startIndex()) +
                               (board.getCol(move.endIndex()) - board.getCol(move.startIndex())) /
                                       2, Checker.EMPTY);
    }

    private void resolveMove(@NotNull CheckersMove move) {
        board.setField(move.endIndex(), board.getField(move.startIndex()));
        board.setField(move.startIndex(), Checker.EMPTY);
    }

    private void resolvePromotion(@NotNull CheckersMove move) {
        board.setField(move.endIndex(), board.getField(move.endIndex()).queen());
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public @Nullable Player getPlayer(@NotNull Checker color) {
        return players.get(color);
    }

    public void addPlayer(@NotNull Player player, @NotNull Checker c) {
        if (c == Checker.EMPTY) {
            return;
        }

        if (players.get(c) != null) {
            return;
        }
        players.put(c, player);
    }

    @Override
    public String toString() {
        return board + "\n" + players + "\n" + turn + "\n" + getValidMoves();
    }
}
