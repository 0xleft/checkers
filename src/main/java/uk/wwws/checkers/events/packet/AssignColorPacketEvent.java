package uk.wwws.checkers.events.packet;

import uk.wwws.checkers.eventframework.Event;
import uk.wwws.checkers.game.Checker;

public class AssignColorPacketEvent extends PacketEvent {
    private Checker color;

    public Checker getColor() {
        return color;
    }

    public AssignColorPacketEvent setColor(Checker color) {
        this.color = color;
        return this;
    }
}
