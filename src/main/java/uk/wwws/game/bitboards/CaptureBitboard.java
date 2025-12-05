package uk.wwws.game.bitboards;

import org.jetbrains.annotations.NotNull;
import uk.wwws.game.Board;

public class CaptureBitboard extends PositionedBitboard implements Directional {
    public CaptureBitboard(int boardDim, int captureRange) {
        assert captureRange % 2 == 1;

        super(boardDim, (captureRange - 1) / 2);

        //    1 0 0 0 1 0 0 0
        //    0 1 0 1 0 0 0 0
        //    0 0 0 0 0 0 0 0
        //    0 1 0 1 0 0 0 0
        //    1 0 0 0 1 0 0 0
        for (int i = 0; i < captureRange; i++) {
            for (int j = 0; j < captureRange; j++) {
                if (i != j || ((captureRange - 1) / 2) == i) {
                    continue;
                }

                setPos(i, j, true);
                setPos(i, captureRange - 1 - j, true);
            }
        }
    }

    static void main() {
        System.out.println(new CaptureBitboard(8, 5));
    }
}
