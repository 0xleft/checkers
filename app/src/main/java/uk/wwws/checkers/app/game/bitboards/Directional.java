package uk.wwws.checkers.app.game.bitboards;

import org.jetbrains.annotations.NotNull;

public interface Directional {
    @NotNull Bitboard backward();

    @NotNull Bitboard forward();
}
