package uk.wwws.game;

import org.jetbrains.annotations.NotNull;

public record CheckersMove(int startIndex, int endIndex) implements Move {
    @Override
    public @NotNull String toString() {
        return "(Move FROM: c" + this.startIndex % Board.DIM + " r" + this.startIndex / Board.DIM +
                " TO: c" + this.endIndex % Board.DIM + " r" + this.endIndex / Board.DIM + ")";
    }
}