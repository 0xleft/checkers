package uk.wwws.checkers.events.commands;

import uk.wwws.checkers.eventframework.Event;

public class MoveCommandEvent extends Event {
    private int fromSquare;
    private int toSquare;

    public int getFromSquare() {
        return fromSquare;
    }

    public MoveCommandEvent setFromSquare(int fromSquare) {
        this.fromSquare = fromSquare;
        return this;
    }

    public int getToSquare() {
        return toSquare;
    }

    public MoveCommandEvent setToSquare(int toSquare) {
        this.toSquare = toSquare;
        return this;
    }
}
