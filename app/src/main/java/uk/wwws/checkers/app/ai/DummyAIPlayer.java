package uk.wwws.checkers.app.ai;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.app.game.CheckersGame;
import uk.wwws.checkers.app.game.moves.Move;
import uk.wwws.checkers.app.game.players.AIPlayer;

public class DummyAIPlayer extends AIPlayer {

    @Override
    public @Nullable Move getBestMove(@NotNull CheckersGame game) {
        @NotNull HashSet<Move> moves = game.getValidMoves();
        if (moves.stream().findFirst().isEmpty()) {
            return null;
        }

        return game.getValidMoves().stream().findFirst().get();
    }
}
