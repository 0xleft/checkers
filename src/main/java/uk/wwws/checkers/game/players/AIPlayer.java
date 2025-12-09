package uk.wwws.checkers.game.players;

import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.game.CheckersGame;
import uk.wwws.checkers.game.Player;
import uk.wwws.checkers.game.moves.Move;

public abstract class AIPlayer implements Player {
    public abstract Move getBestMove(@NotNull CheckersGame game);
}
