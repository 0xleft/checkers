package checkers.game.bitboards;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import checkers.game.Checker;

public class Bitboard extends BitSet {
    protected final int boardDim;

    public Bitboard(int boardDim) {
        super(boardDim * boardDim);
        this.boardDim = boardDim;
    }

    public int getBoardDim() {
        return boardDim;
    }

    /*@
        ensures this.cardinality() == \old(this.cardinality());
    */
    public Bitboard(@NotNull BitSet bitSet, int boardDim) {
        this(boardDim);
        from(bitSet);
    }

    public void from(@NotNull BitSet board) {
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i)) {
                set(i);
            } else {
                clear(i);
            }
        }
    }

    public Bitboard(@NotNull Checker[] checkers, @Nullable Checker mask, int boardDim) {
        this(boardDim);

        for (int i = 0; i < checkers.length; i++) {
            if (checkers[i] == mask || (mask == null && Checker.EMPTY != checkers[i])) {
                set(i);
            }
        }
    }

    public @NotNull Set<Integer> getMaskIndexes(boolean mask) {
        Set<Integer> indexes = new HashSet<>();

        for (int i = 0; i < size(); i++) {
            if (get(i) == mask) {
                indexes.add(i);
            }
        }

        return indexes;
    }

    public void setPos(int row, int col, boolean value) {
        if (row < 0 || col < 0 || col >= boardDim || row * boardDim + col > boardDim * boardDim) {
            return;
        }

        super.set(row * boardDim + col, value);
    }

    public @NotNull Set<Integer> getOnIndexes() {
        return getMaskIndexes(true);
    }

    public @NotNull Set<Integer> getOffIndexes() {
        return getMaskIndexes(false);
    }

    public @NotNull Bitboard and(@NotNull Bitboard board) {
        Bitboard bitboard = new Bitboard(boardDim);

        for (int i = 0; i < size(); i++) {
            bitboard.set(i, board.get(i) && get(i));
        }

        return bitboard;
    }

    public @NotNull Bitboard not() {
        Bitboard bitboard = new Bitboard(boardDim);

        for (int i = 0; i < size(); i++) {
            bitboard.set(i, !get(i));
        }

        return bitboard;
    }

    public @NotNull Bitboard shiftH(int n) {
        Bitboard bitboard = new Bitboard(boardDim);

        for (int i = 0; i < size(); i++) {
            // fucking modular shitfuckery
            // todo simplify
            if ((i + n) % boardDim == i % boardDim + n % boardDim && i + n >= 0 && get(i)) {
                bitboard.set(i + n);
            }
        }

        return bitboard;
    }

    public @NotNull Bitboard shiftV(int n) {
        Bitboard bitboard = new Bitboard(boardDim);

        for (int i = 0; i < size(); i++) {
            if (i + n < size() && (i + n * boardDim) >= 0 && get(i)) {
                bitboard.set(i + n * boardDim);
            }
        }

        return bitboard;
    }

    public static @NotNull Bitboard diagonalRay(int range, int row, int col, boolean down, int boardDim) {
        Bitboard bitboard = new Bitboard(boardDim);

        // todo simplify since cyclomatic complexity
        for (int i = 0; i < Math.abs(range); i++) {
            if (down) {
                if (range < 0) {
                    bitboard.setPos(row - i, col - i, true);
                } else {
                    bitboard.setPos(row + i, col + i, true);
                }
            } else {
                if (range < 0) {
                    bitboard.setPos(row + i, col - i, true);
                } else {
                    bitboard.setPos(row - i, col + i, true);
                }
            }
        }

        return bitboard;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < boardDim; i++) {
            for (int j = 0; j < boardDim; j++) {
                sb.append(get(i * boardDim + j) ? 1 : 0).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(@NotNull Object obj) {
        if (obj instanceof Bitboard b) {
            return Arrays.equals(this.toByteArray(), b.toByteArray());
        }

        return false;
    }
}
