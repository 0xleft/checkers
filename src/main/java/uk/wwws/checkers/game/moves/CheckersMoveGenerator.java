package uk.wwws.checkers.game.moves;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.game.Board;
import uk.wwws.checkers.game.Checker;
import uk.wwws.checkers.game.bitboards.Bitboard;
import uk.wwws.checkers.game.bitboards.CaptureBitboard;
import uk.wwws.checkers.game.bitboards.MoveBitboard;
import uk.wwws.checkers.game.bitboards.PositionedBitboard;

/*
I am aware that bitboards are way more effective but this is a quick and dirty prototype implemetation.
 */
public class CheckersMoveGenerator implements MoveGenerator {
    private static CheckersMoveGenerator instance;

    public static CheckersMoveGenerator getInstance() {
        if (instance == null) {
            instance = new CheckersMoveGenerator();
        }

        return instance;
    }

    public HashSet<Move> generateMoves(@NotNull Board board, @NotNull Checker turn) {
        HashSet<Move> legalMoves = new HashSet<>();

        for (int i = 0; i < Board.DIM * Board.DIM; i++) {
            if (board.getField(i).sameColor(turn)) {
                generateMovesForPiece(board, i, legalMoves);
                generateCapturesForPiece(board, i, legalMoves);
            }
        }

        return legalMoves;
    }

    private void generateMovesForPiece(@NotNull Board board, int index,
                                       @NotNull HashSet<Move> legalMoves) {
        Checker piece = board.getField(index);

        Bitboard allPieces = new Bitboard(board.getCheckers(), null, Board.DIM);
        MoveBitboard moveBitboard =
                (MoveBitboard) new MoveBitboard(Board.DIM).reposition(board.getRow(index),
                                                                      board.getCol(index));

        Bitboard accountedForSide = accountForSide(piece, moveBitboard);

        for (Integer onIndex : accountedForSide.and(allPieces.not()).getOnIndexes()) {
            legalMoves.add(new CheckersMove(index, onIndex));
        }
    }

    private void generateCapturesForPiece(@NotNull Board board, int index,
                                          @NotNull HashSet<Move> legalMoves) {
        Checker piece = board.getField(index);

        Bitboard allPieces = new Bitboard(board.getCheckers(), null, Board.DIM);
        Bitboard oppPieces = new Bitboard(board.getCheckers(), piece.other(), Board.DIM);


        Bitboard captures = new CaptureBitboard(Board.DIM, 5).reposition(board.getRow(index),
                                                                         board.getCol(index))
                .and(allPieces.not());
        PositionedBitboard pCaptures =
                new PositionedBitboard(Board.DIM, 0, captures, board.getRow(index),
                                       board.getCol(index));
        Bitboard accountedCaptures = accountForSide(piece, pCaptures);


        for (Integer emptyCaptureSquare : accountedCaptures.getOnIndexes()) {
            int row1 = board.getRow(index);
            int col1 = board.getCol(index);
            int row2 = board.getRow(emptyCaptureSquare);
            int col2 = board.getCol(emptyCaptureSquare);

            external:
            for (int i = 0; i < 2; i++) {
                for (int j = -1; j < 2; j += 2) {
                    Bitboard rest = new Bitboard(Board.DIM);
                    rest.setPos(row2, col2, true);
                    rest.xor(oppPieces);
                    if (board.getDistance(row1, col1, row2, col2) >= 2 && rest.and(
                                    Bitboard.diagonalRay(3 * j, row1, col1, i == 0, rest.getBoardDim()))
                            .getOnIndexes().size() == 2) {
                        legalMoves.add(new CheckersMove(index, emptyCaptureSquare));
                        break external;
                    }
                }
            }
        }
    }

    private @NotNull Bitboard accountForSide(@NotNull Checker piece,
                                             @NotNull PositionedBitboard bitboard) {
        if (piece.isQueen()) {
            return bitboard;
        }

        if (piece.sameColor(Checker.WHITE)) {
            return bitboard.forward();
        }

        return bitboard.backward();
    }

    static void main() {
        Board a = new Board();

        System.out.print(a);

        System.out.println(CheckersMoveGenerator.getInstance().generateMoves(a, Checker.WHITE));
    }
}

// check one around
// check jumps