package uk.wwws.checkers.app.game.moves;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.app.game.Board;
import uk.wwws.checkers.app.game.Checker;
import uk.wwws.checkers.app.game.bitboards.Bitboard;
import uk.wwws.checkers.app.game.bitboards.CaptureBitboard;
import uk.wwws.checkers.app.game.bitboards.MoveBitboard;
import uk.wwws.checkers.app.game.bitboards.PositionedBitboard;

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

    static void main() {
        Board a = new Board();

        System.out.print(a);

        System.out.println(CheckersMoveGenerator.getInstance().generateMoves(a, Checker.WHITE));
    }

    public HashSet<Move> generateMoves(@NotNull Board board, @NotNull Checker turn) {
        HashSet<Move> captureMoves = new HashSet<>();
        HashSet<Move> legalMoves = new HashSet<>();

        for (int i = 0; i < Board.DIM * Board.DIM; i++) {
            if (board.getField(i).sameColor(turn)) {
                generateCapturesForPiece(board, i, captureMoves);
                generateMovesForPiece(board, i, legalMoves);
            }
        }

        // If there are no capture moves, any move is legal.
        if (captureMoves.isEmpty()) {
            return legalMoves;
        }

        // If there are captures, those are mandatory.
        return captureMoves;
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
        oppPieces.or(new Bitboard(board.getCheckers(), piece.other().queen(), Board.DIM));

        Bitboard captures = new CaptureBitboard(Board.DIM, 5).reposition(board.getRow(index),
                                                                         board.getCol(index))
                .and(allPieces.not());
        PositionedBitboard pCaptures =
                new PositionedBitboard(Board.DIM, 0, captures, board.getRow(index),
                                       board.getCol(index));
        Bitboard accountedCaptures = accountForSide(piece, pCaptures);


        for (Integer emptyCaptureSquare : accountedCaptures.getOnIndexes()) {
            int piceRow = board.getRow(index);
            int pieceCol = board.getCol(index);
            int emptyCaptureRow = board.getRow(emptyCaptureSquare);
            int emptyCaptureCol = board.getCol(emptyCaptureSquare);

            external:
            for (int i = 0; i < 2; i++) {
                for (int j = -1; j < 2; j += 2) {
                    Bitboard rest = new Bitboard(Board.DIM);
                    rest.setPos(emptyCaptureRow, emptyCaptureCol, true);

                    Bitboard ray = Bitboard.diagonalRay(3 * j, piceRow, pieceCol, i == 0,
                                                        rest.getBoardDim());

                    // make sure the ray captures the empty square
                    if (rest.and(ray).cardinality() != 1) {
                        continue;
                    }

                    rest.xor(oppPieces);

                    if (board.getDistance(piceRow, pieceCol, emptyCaptureRow, emptyCaptureCol) >=
                            2 && rest.and(ray).cardinality() == 2) {
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
}

// check one around
// check jumps