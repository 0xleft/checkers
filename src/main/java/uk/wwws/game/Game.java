package uk.wwws.game;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import uk.wwws.game.moves.Move;

public interface Game {
    boolean isGameOver();

    Player getTurn();

    Player getWinner();

    HashSet<? extends Move> getValidMoves();

    boolean isValidMove(@NotNull Move move);

    void doMove(@NotNull Move move);
}
