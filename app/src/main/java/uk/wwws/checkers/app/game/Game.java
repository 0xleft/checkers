package checkers.game;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import checkers.game.moves.Move;

public interface Game {
    boolean isGameOver();

    Player getTurn();

    Player getWinner();

    HashSet<? extends Move> getValidMoves();

    boolean isValidMove(@NotNull Move move);

    void doMove(@NotNull Move move);

    @NotNull Board getBoard();
}
