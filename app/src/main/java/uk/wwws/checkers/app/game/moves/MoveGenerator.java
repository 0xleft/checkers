package checkers.game.moves;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import checkers.game.Board;
import checkers.game.Checker;

public interface MoveGenerator {
    HashSet<Move> generateMoves(@NotNull Board board, @NotNull Checker turn);
}
