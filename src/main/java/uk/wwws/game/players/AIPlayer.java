package uk.wwws.game.players;

import org.jetbrains.annotations.NotNull;
import uk.wwws.game.*;
import uk.wwws.game.moves.Move;

public abstract class AIPlayer implements Player {
    public abstract Move getBestMove(@NotNull CheckersGame game);
}
