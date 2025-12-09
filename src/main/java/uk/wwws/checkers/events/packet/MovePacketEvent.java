package uk.wwws.checkers.events.packet;

import uk.wwws.checkers.eventframework.Event;
import uk.wwws.checkers.events.commands.MoveCommandEvent;

class MovePacketEvent extends PacketEvent {
    private int fromSquare;
    private int toSquare;

    public int getFromSquare() {
        return fromSquare;
    }

    public MovePacketEvent setFromSquare(int fromSquare) {
        this.fromSquare = fromSquare;
        return this;
    }

    public int getToSquare() {
        return toSquare;
    }

    public MovePacketEvent setToSquare(int toSquare) {
        this.toSquare = toSquare;
        return this;
    }
}
