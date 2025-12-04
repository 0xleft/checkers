package uk.wwws.game;

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


        if (!piece.isQueen()) {
            return;
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