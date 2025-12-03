package uk.wwws.game;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.game.exceptions.InvalidMoveException;

public class CheckersGame implements Game {
    private Board board;
    private HashMap<Checker, Player> players = new HashMap<>();
    private Checker turn;

    public CheckersGame() {
        board = new Board();
        turn = Checker.WHITE;
    }

    //@ pure
    @Override
    public boolean isGameOver() {
        return board.gameOver();
    }

    @Override
    public @Nullable Player getTurn() {
        return players.get(turn);
    }

    @Override
    public @Nullable Player getWinner() {
        if (!board.hasWinner()) {
            return null;
        }

        return players.get(board.isWinner(Checker.WHITE) ? Checker.WHITE : Checker.BLACK);
    }

    @Override
    public @NotNull HashSet<CheckersMove> getValidMoves() {
        return MoveGenerator.generateMoves(board, this.turn);
    }

    /*@
        requires move instanceof CheckersMove;
    */
    //@ pure
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
        }

        this.turn = turn.other();
    }

    private void resolveCapture(@NotNull CheckersMove move) {
        board.setField(board.getRow(move.startIndex()) + ( board.getRow(move.endIndex()) - board.getRow(move.startIndex())) / 2,
                       board.getCol(move.startIndex()) + (board.getCol(move.endIndex()) - board.getCol(move.startIndex())) / 2,
                       Checker.EMPTY);
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

    public void addPlayer(@NotNull Player player, @NotNull Checker c) {
        if (players.get(c) != null) {
            return;
        }
        players.put(c, player);
    }

    public void resetPlayers() {
        players.clear();
    }

    @Override
    public String toString() {
        return board.toString() + "\n" + players + "\n" + turn + "\n" + getValidMoves();
    }
}
