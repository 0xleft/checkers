package uk.wwws.checkers.game.bitboards;

import org.jetbrains.annotations.NotNull;

public class PositionedBitboard extends Bitboard implements Directional {
    private final int offset;
    protected int row;
    protected int col;

    public PositionedBitboard(int boardDim, int offset, @NotNull Bitboard bitboard) {
        this(boardDim, offset);

        from(bitboard);
    }

    public PositionedBitboard(int boardDim, int offset, @NotNull Bitboard bitboard, int row,
                              int col) {
        this(boardDim, offset);

        this.row = row;
        this.col = col;

        from(bitboard);
    }

    public PositionedBitboard(int boardDim, int offset) {
        super(boardDim);

        this.offset = offset;
    }

    public @NotNull PositionedBitboard reposition(int row, int col) {
        assert row < boardDim;
        assert col < boardDim;
        assert row >= 0;
        assert col >= 0;

        this.col = col;
        this.row = row;

        from(shiftV(row - offset));
        from(shiftH(col - offset));

        return this;
    }

    public @NotNull PositionedBitboard forward() {
        PositionedBitboard bitboard = (PositionedBitboard) this.clone();
        bitboard.set((row + 1) * boardDim, boardDim * boardDim, false);
        return bitboard;
    }

    public @NotNull PositionedBitboard backward() {
        PositionedBitboard bitboard = (PositionedBitboard) this.clone();
        bitboard.set(0, row * boardDim + col, false);
        return bitboard;
    }
}
