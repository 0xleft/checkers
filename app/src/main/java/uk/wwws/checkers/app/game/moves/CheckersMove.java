package checkers.game.moves;

import java.text.MessageFormat;
import org.jetbrains.annotations.NotNull;

public record CheckersMove(int startIndex, int endIndex) implements Move {
    @Override
    public @NotNull String toString() {
        /*
        return "(Move FROM: c" + this.startIndex % Board.DIM + " r" + this.startIndex / Board.DIM +
                " TO: c" + this.endIndex % Board.DIM + " r" + this.endIndex / Board.DIM + ")";
         */

        return MessageFormat.format("Move(start: {0}, end: {1})", this.startIndex, this.endIndex);
    }
}