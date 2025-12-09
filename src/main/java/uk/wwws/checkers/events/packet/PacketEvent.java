package uk.wwws.checkers.events.packet;

import uk.wwws.checkers.eventframework.Event;
import uk.wwws.checkers.net.Connection;

public class PacketEvent extends Event {
    protected Connection connection;

    public PacketEvent setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public Connection getConnection() {
        return connection;
    }
}
