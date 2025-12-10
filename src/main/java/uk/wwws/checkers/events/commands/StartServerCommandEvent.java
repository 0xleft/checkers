package uk.wwws.checkers.events.commands;

import uk.wwws.checkers.eventframework.Event;

public class StartServerCommandEvent extends Event {
    private int port;

    public StartServerCommandEvent setPort(int port) {
        this.port = port;
        return this;
    }

    public int getPort() {
        return port;
    }
}
