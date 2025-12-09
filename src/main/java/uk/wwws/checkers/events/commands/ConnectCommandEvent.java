package uk.wwws.checkers.events.commands;

import uk.wwws.checkers.eventframework.Event;

public class ConnectCommandEvent extends Event {
    private String host;
    private int port;

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public ConnectCommandEvent setHost(String host) {
        this.host = host;
        return this;
    }

    public ConnectCommandEvent setPort(int port) {
        this.port = port;
        return this;
    }
}
