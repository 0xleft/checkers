package uk.wwws.checkers.events.net;

import uk.wwws.checkers.eventframework.Event;
import uk.wwws.checkers.net.Connection;

public class ConnectionEvent extends Event {
    protected Connection connection;

    public ConnectionEvent setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public Connection getConnection() {
        return connection;
    }
}
