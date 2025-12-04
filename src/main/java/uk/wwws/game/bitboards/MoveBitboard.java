package uk.wwws.game.bitboards;

import uk.wwws.game.Board;

public class MoveBitboard extends Bitboard {
    public MoveBitboard(int boardDim) {
        super(boardDim);


        //    1 0 1 0 0 0 0 0
        //    0 0 0 0 0 0 0 0
        //    1 0 1 0 0 0 0 0
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i % 2 != 0 || j % 2 != 0) {
                    continue;
                }

                set(i * Board.DIM + j);
            }
        }
    }
}
