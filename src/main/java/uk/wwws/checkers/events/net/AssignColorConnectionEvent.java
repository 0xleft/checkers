package uk.wwws.checkers.events.net;

import uk.wwws.checkers.game.Checker;

public class AssignColorConnectionEvent extends ConnectionEvent {
    private Checker color;

    public Checker getColor() {
        return color;
    }

    public AssignColorConnectionEvent setColor(Checker color) {
        this.color = color;
        return this;
    }
}
