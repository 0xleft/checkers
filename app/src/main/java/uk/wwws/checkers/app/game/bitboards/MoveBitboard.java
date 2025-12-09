package uk.wwws.checkers.app.game.bitboards;

import uk.wwws.checkers.app.game.Board;

public class MoveBitboard extends PositionedBitboard {

    public MoveBitboard(int boardDim) {
        super(boardDim, 1);

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

    static void main() {
        MoveBitboard c = new MoveBitboard(8);

        System.out.println(c.backward().reposition(0, 6));
        System.out.println(c.forward());
    }
}
