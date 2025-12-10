package uk.wwws.checkers.game.moves;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.game.Board;
import uk.wwws.checkers.game.Checker;

public interface MoveGenerator {
    HashSet<Move> generateMoves(@NotNull Board board, @NotNull Checker turn);
}
