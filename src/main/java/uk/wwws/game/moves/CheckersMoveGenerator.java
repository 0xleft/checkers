package uk.wwws.game.moves;

import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import uk.wwws.game.Board;
import uk.wwws.game.Checker;
import uk.wwws.game.bitboards.Bitboard;
import uk.wwws.game.bitboards.CaptureBitboard;
import uk.wwws.game.bitboards.MoveBitboard;
import uk.wwws.game.bitboards.PositionedBitboard;

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
                new MoveBitboard(Board.DIM).reposition(board.getRow(index), board.getCol(index));

        accountForSide(piece, moveBitboard);

        for (Integer onIndex : moveBitboard.and(allPieces.not()).getOnIndexes()) {
            legalMoves.add(new CheckersMove(index, onIndex));
        }
    }

    private void generateCapturesForPiece(@NotNull Board board, int index,
                                         @NotNull HashSet<Move> legalMoves) {
        Checker piece = board.getField(index);

        Bitboard allPieces = new Bitboard(board.getCheckers(), null, Board.DIM);
        Bitboard oppPieces = new Bitboard(board.getCheckers(), piece.other(), Board.DIM);
        CaptureBitboard captures = new CaptureBitboard(Board.DIM, 5).reposition(board.getRow(index),
                                                                                board.getCol(
                                                                                        index));
        accountForSide(piece, captures);

        MoveBitboard move =
                new MoveBitboard(Board.DIM).reposition(board.getRow(index), board.getCol(index));
        accountForSide(piece, move);

        Bitboard capturablePieces = oppPieces.and(move);
    }

    private @NotNull PositionedBitboard accountForSide(@NotNull Checker piece,
                                                    @NotNull PositionedBitboard bitboard) {
        if (!piece.isQueen()) {
            if (piece.sameColor(Checker.WHITE)) {
                bitboard.from(bitboard.forward());
            } else {
                bitboard.from(bitboard.backward());
            }
        }

        return bitboard;
    }

    static void main() {
        Board a = new Board();

        System.out.print(a);

        System.out.println(CheckersMoveGenerator.getInstance().generateMoves(a, Checker.WHITE));
    }
}

// check one around
// check jumps