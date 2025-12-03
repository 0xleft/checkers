package uk.wwws.game.players;

import org.jetbrains.annotations.NotNull;
import uk.wwws.game.*;

public abstract class AIPlayer implements Player {
    public abstract Move getBestMove(@NotNull CheckersGame game);
}
