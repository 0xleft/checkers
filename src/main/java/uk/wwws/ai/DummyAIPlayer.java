package uk.wwws.ai;

import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.game.*;
import uk.wwws.game.players.AIPlayer;

public class DummyAIPlayer extends AIPlayer {

    @Override
    public @Nullable Move getBestMove(@NotNull CheckersGame game) {
        Set<CheckersMove> moves = game.getValidMoves();
        if (moves.stream().findFirst().isEmpty()) {
            return null;
        }

        return game.getValidMoves().stream().findFirst().get();
    }
}
