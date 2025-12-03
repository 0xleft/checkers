package uk.wwws.game;

import org.jetbrains.annotations.NotNull;

public class Board {
    public static final int DIM = 8;
    private Checker[] fields = new Checker[DIM * DIM];

    public Board() {
        defaultBoard();
    }

    private void defaultBoard() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (j % 2 == i % 2) {
                    if (i < 3) {
                        setField(i, j, Checker.BLACK);
                        continue;
                    }
                    if (i > 4) {
                        setField(i, j, Checker.WHITE);
                        continue;
                    }
                }

                setField(i, j, Checker.EMPTY);
            }
        }
    }

    //@ pure
    public int getCol(int index) {
        return index % DIM;
    }

    //@ pure
    public int getRow(int index) {
        return index / DIM;
    }

    //@ pure
    public int index(int row, int col) {
        return row * DIM + col;
    }

    //@ pure
    public boolean isField(int index) {
        return (0 <= index) && (index < DIM * DIM);
    }

    //@ pure
    public boolean isField(int row, int col) {
        return isField(index(row, col));
    }

    //@ pure
    public Checker getField(int i) {
        if (i >= DIM * DIM) {
            return Checker.EMPTY;
        }

        return this.fields[i];
    }

    //@ pure
    public Checker getField(int row, int col) {
        return getField(index(row, col));
    }

    //@ pure
    public boolean isEmptyField(int i) {
        return getField(i) == Checker.EMPTY;
    }

    //@ pure
    public boolean isEmptyField(int row, int col) {
        if (row < 0 || col > 7) {
            return false;
        }

        return isEmptyField(index(row, col));
    }

    /*@
        requires c == Checker.WHITE || c == Checker.BLACK;
    */
    public boolean isOnlyChecker(@NotNull Checker c) {
        for (int i = 0; i < DIM * DIM; i++) {
            if (getField(i) != c && getField(i) != c.queen()) {
                return false;
            }
        }

        return true;
    }

    //@ pure
    public boolean isEmpty() {
        return isOnlyChecker(Checker.EMPTY);
    }

    //@ pure
    public boolean gameOver() {
        return hasWinner() || isEmpty();
    }

    //@ pure
    public boolean isWinner(@NotNull Checker c) {
        return isOnlyChecker(c);
    }

    public @NotNull String toString() {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                s.append(getField(i, j)).append(" ");
            }
            s.append("\n");
        }

        return s.toString();
    }

    //@ pure
    public boolean hasWinner() {
        return isWinner(Checker.WHITE) || isWinner(Checker.BLACK);
    }

    public void reset() {
        this.fields = new Board().fields;
    }

    public void setField(int i, @NotNull Checker c) {
        this.fields[i] = c;
    }

    public void setField(int row, int col, @NotNull Checker c) {
        this.fields[index(row, col)] = c;
    }

    public boolean shouldPromote(int index) {
        return getField(index) == Checker.WHITE && getRow(index) == 0 ||
                getField(index) == Checker.BLACK && getRow(index) == 7;
    }
}
