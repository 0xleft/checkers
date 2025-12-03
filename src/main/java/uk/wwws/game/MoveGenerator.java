package uk.wwws.game;

import java.util.Arrays;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

/*
I am aware that bitboards are way more effective but this is a quick and dirty prototype implemetation.
 */
public class MoveGenerator {
    public static HashSet<CheckersMove> generateMoves(@NotNull Board board, @NotNull Checker turn) {
        HashSet<CheckersMove> legalMoves = new HashSet<>();

        for (int i = 0; i < Board.DIM * Board.DIM; i++) {
            if (board.getField(i) == turn) {
                generateMovesForPiece(board, i, legalMoves);
            }
        }

        return legalMoves;
    }

    private static void generateMovesForPiece(@NotNull Board board, int index,
                                              @NotNull HashSet<CheckersMove> legalMoves) {
        Checker piece = board.getField(index);
        generateForwardCaptures(board, index, legalMoves);
        generateForward(board, index, legalMoves);

        if (!piece.isQueen()) {
            return;
        }

        generateBackwardCaptures(board, index, legalMoves);
        generateBackward(board, index, legalMoves);
    }

    private static void generateForwardCaptures(@NotNull Board board, int index,
                                                @NotNull HashSet<CheckersMove> legalMoves) {
        Checker piece = board.getField(index);
        if (piece == Checker.BLACK) {
            generateBackwardCaptures(board, index, legalMoves);
            return;
        }

        if (board.isEmptyField(board.getRow(index) - 2, board.getCol(index) + 2) && board.getField(board.getRow(index) - 1, board.getCol(index) + 1) == piece.other()) {
            legalMoves.add(new CheckersMove(index, board.index(board.getRow(index) - 2,
                                                               board.getCol(index) + 2)));
        }

        if (board.isEmptyField(board.getRow(index) - 2, board.getCol(index) - 2) && board.getField(board.getRow(index) - 1, board.getCol(index) - 1) == piece.other()) {
            legalMoves.add(new CheckersMove(index, board.index(board.getRow(index) - 2,
                                                               board.getCol(index) - 2)));
        }
    }

    private static void generateForward(@NotNull Board board, int index,
                                        @NotNull HashSet<CheckersMove> legalMoves) {
        Checker piece = board.getField(index);
        if (piece == Checker.BLACK) {
            generateBackward(board, index, legalMoves);
            return;
        }

        if (board.isEmptyField(board.getRow(index) - 1, board.getCol(index) + 1)) {
            legalMoves.add(new CheckersMove(index, board.index(board.getRow(index) - 1,
                                                               board.getCol(index) + 1)));
        }

        if (board.isEmptyField(board.getRow(index) - 1, board.getCol(index) - 1)) {
            legalMoves.add(new CheckersMove(index, board.index(board.getRow(index) - 1,
                                                               board.getCol(index) - 1)));
        }
    }

    private static void generateBackwardCaptures(@NotNull Board board, int index,
                                                 @NotNull HashSet<CheckersMove> legalMoves) {
        Checker piece = board.getField(index);
        if (piece == Checker.BLACK_QUEEN) {
            generateForwardCaptures(board, index, legalMoves);
            return;
        }

        if (board.isEmptyField(board.getRow(index) + 2, board.getCol(index) + 2) && board.getField(board.getRow(index) + 1, board.getCol(index) + 1) == piece.other()) {
            legalMoves.add(new CheckersMove(index, board.index(board.getRow(index) + 2,
                                                               board.getCol(index) + 2)));
        }

        if (board.isEmptyField(board.getRow(index) + 2, board.getCol(index) - 2) && board.getField(board.getRow(index) + 1, board.getCol(index) - 1) == piece.other()) {
            legalMoves.add(new CheckersMove(index, board.index(board.getRow(index) + 2,
                                                               board.getCol(index) - 2)));
        }
    }

    private static void generateBackward(@NotNull Board board, int index,
                                         @NotNull HashSet<CheckersMove> legalMoves) {
        Checker piece = board.getField(index);
        if (piece == Checker.BLACK_QUEEN) {
            generateForward(board, index, legalMoves);
            return;
        }

        if (board.getCol(index) < 7) {
            if (board.isEmptyField(board.getRow(index) + 1, board.getCol(index) + 1)) {
                legalMoves.add(new CheckersMove(index, board.index(board.getRow(index) + 1,
                                                                   board.getCol(index) + 1)));
            }
        }

        if (board.getCol(index) > 0) {
            if (board.isEmptyField(board.getRow(index) + 1, board.getCol(index) - 1)) {
                legalMoves.add(new CheckersMove(index, board.index(board.getRow(index) + 1,
                                                                   board.getCol(index) - 1)));
            }
        }
    }

    static void main() {
        Board a = new Board();

        System.out.print(a);

        System.out.println(MoveGenerator.generateMoves(a, Checker.BLACK));
    }
}

// check one around
// check jumps