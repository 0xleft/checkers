package uk.wwws.game.moves;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import uk.wwws.game.Board;
import uk.wwws.game.Checker;

public interface MoveGenerator {
    HashSet<Move> generateMoves(@NotNull Board board, @NotNull Checker turn);
}
