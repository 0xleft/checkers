package checkers.game.players;

import org.jetbrains.annotations.NotNull;
import checkers.game.CheckersGame;
import checkers.game.Player;
import checkers.game.moves.Move;

public abstract class AIPlayer implements Player {
    public abstract Move getBestMove(@NotNull CheckersGame game);
}
