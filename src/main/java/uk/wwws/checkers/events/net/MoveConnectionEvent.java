package uk.wwws.checkers.events.net;

public class MoveConnectionEvent extends ConnectionEvent {
    private int fromSquare;
    private int toSquare;

    public int getFromSquare() {
        return fromSquare;
    }

    public MoveConnectionEvent setFromSquare(int fromSquare) {
        this.fromSquare = fromSquare;
        return this;
    }

    public int getToSquare() {
        return toSquare;
    }

    public MoveConnectionEvent setToSquare(int toSquare) {
        this.toSquare = toSquare;
        return this;
    }
}
