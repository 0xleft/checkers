package uk.wwws.checkers.game;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class Board {
    public static final int DIM = 8;
    private Checker[] fields;

    public Board() {
        empty();
        defaultBoard();
    }

    public Checker[] getCheckers() {
        return fields;
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

    public int getCol(int index) {
        return index % DIM;
    }

    public int getRow(int index) {
        return index / DIM;
    }

    public int index(int row, int col) {
        return row * DIM + col;
    }

    public @NotNull Checker getField(int i) {
        if (i >= DIM * DIM || i < 0) {
            return Checker.EMPTY;
        }

        return this.fields[i];
    }

    public @NotNull Checker getField(int row, int col) {
        return getField(index(row, col));
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

    public void setField(int i, @NotNull Checker c) {
        this.fields[i] = c;
    }

    public void setField(int row, int col, @NotNull Checker c) {
        this.setField(index(row, col), c);
    }

    public void empty() {
        this.fields = new Checker[DIM * DIM];
        Arrays.fill(this.fields, Checker.EMPTY);
    }

    public boolean shouldPromote(int index) {
        return getField(index) == Checker.WHITE && getRow(index) == 0 ||
                getField(index) == Checker.BLACK && getRow(index) == 7;
    }

    public double getDistance(int row1, int col1, int row2, int col2) {
        return Math.sqrt(Math.pow(row2 - row1, 2) + Math.pow(col2 - col1, 2));
    }
}
