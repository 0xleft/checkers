package uk.wwws.game;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Bitboard extends BitSet {
    public Bitboard() {
        super(Board.DIM * Board.DIM);
    }

    /*@
        ensures this.cardinality() == \old(this.cardinality());
    */
    public Bitboard(BitSet bitSet) {
        this();
        for (int i = 0; i < bitSet.size(); i++) {
            if (bitSet.get(i)) {
                set(i);
            }
        }
    }

    public Bitboard(@NotNull Checker[] checkers, @Nullable Checker mask) {
        this();

        for (int i = 0; i < checkers.length; i++) {
            if (checkers[i] == mask || (mask == null && Checker.EMPTY != checkers[i])) {
                set(i);
            }
        }
    }

    public @NotNull Set<Integer> getMaskIndexes(@NotNull Bitboard board, boolean mask) {
        Set<Integer> indexes = new HashSet<>();

        for (int i = 0; i < board.length(); i++) {
            if (get(i) == mask) {
                indexes.add(i);
            }
        }

        return indexes;
    }


    public @NotNull Set<Integer> getOnIndexes(@NotNull Bitboard board) {
        return getMaskIndexes(board, true);
    }

    public @NotNull Set<Integer> getOffIndexes(@NotNull Bitboard board) {
        return getMaskIndexes(board, false);
    }

    private @NotNull Bitboard shiftL() {
        Bitboard bitboard = new Bitboard();

        for (int i = 0; i < size(); i++) {
            if (get(i + 1)) {
                bitboard.set(i);
            }
        }

        return bitboard;
    }

    private @NotNull Bitboard shiftR() {
        Bitboard bitboard = new Bitboard();

        for (int i = 1; i < size() - 1; i++) {
            if (get(i - 1)) {
                bitboard.set(i);
            }
        }

        return bitboard;
    }

    private @NotNull Bitboard shiftH(int n) {
        Bitboard bitboard = new Bitboard(this);

        for (int i = 0; i < Math.abs(n); i++) {
            if (n > 0) {
                bitboard = bitboard.shiftR();
            } else {
                bitboard = bitboard.shiftL();
            }
        }

        return bitboard;
    }

    private @NotNull Bitboard shiftV(int n) {
        Bitboard bitboard = new Bitboard(this);

        for (int i = 0; i < Math.abs(n); i++) {
            if (n > 0) {
                bitboard = bitboard.shiftH(Board.DIM);
            } else {
                bitboard = bitboard.shiftH(-Board.DIM);
            }
        }

        return bitboard;
    }

    static void main() {
        Bitboard bitboard = new Bitboard();
        bitboard.set(0);

        System.out.println(bitboard.shiftV(4).shiftV(-4));
    }
}
