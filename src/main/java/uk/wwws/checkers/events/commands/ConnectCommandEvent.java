package uk.wwws.checkers.events.commands;

import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.eventframework.Event;

public class ConnectCommandEvent extends Event {
    private String host;
    private String port;

    public String getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public ConnectCommandEvent setHost(@NotNull String host) {
        this.host = host;
        return this;
    }

    public ConnectCommandEvent setPort(@NotNull String port) {
        this.port = port;
        return this;
    }
}
