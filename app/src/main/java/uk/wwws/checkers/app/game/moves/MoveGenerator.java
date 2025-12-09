package uk.wwws.checkers.app.game.moves;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.app.game.Board;
import uk.wwws.checkers.app.game.Checker;

public interface MoveGenerator {
    HashSet<Move> generateMoves(@NotNull Board board, @NotNull Checker turn);
}
