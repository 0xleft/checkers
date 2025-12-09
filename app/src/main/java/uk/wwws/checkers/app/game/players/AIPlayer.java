package uk.wwws.checkers.app.game.players;

import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.app.game.CheckersGame;
import uk.wwws.checkers.app.game.Player;
import uk.wwws.checkers.app.game.moves.Move;

public abstract class AIPlayer implements Player {
    public abstract Move getBestMove(@NotNull CheckersGame game);
}
