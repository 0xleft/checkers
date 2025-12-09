package checkers.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Checker {
    BLACK, WHITE, WHITE_QUEEN, BLACK_QUEEN, EMPTY;

    public Checker other() {
        if (this.sameColor(BLACK)) {
            return WHITE;
        } else if (this.sameColor(WHITE)) {
            return BLACK;
        } else {
            return EMPTY;
        }
    }

    public Checker queen() {
        if (this == BLACK_QUEEN || this == WHITE_QUEEN) {
            return this;
        }

        if (this == BLACK) {
            return BLACK_QUEEN;
        } else if (this == WHITE) {
            return WHITE_QUEEN;
        } else {
            return EMPTY;
        }
    }

    public boolean sameColor(@Nullable Checker c) {
        if (c == null) {
            return false;
        }

        return this.toString().equalsIgnoreCase(c.toString());
    }

    public boolean isQueen() {
        return this == BLACK_QUEEN || this == WHITE_QUEEN;
    }

    @Override
    public @NotNull String toString() {
        switch (this) {
            case BLACK -> {
                return "o";
            }
            case BLACK_QUEEN -> {
                return "O";
            }
            case WHITE -> {
                return "x";
            }
            case WHITE_QUEEN -> {
                return "X";
            }
            default -> {
                return "_";
            }
        }
    }
}
