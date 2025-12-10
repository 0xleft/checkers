package uk.wwws.checkers.events.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.eventframework.Event;
import uk.wwws.checkers.net.Connection;

public class ConnectionEvent extends Event {
    private static final Logger logger = LogManager.getRootLogger();

    protected Connection connection;

    public ConnectionEvent setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    @Override
    public void emit() {
        if (this.connection == null) {
            logger.error(
                    "Failed to emit {} since connection was null and was of ConnectionEvent type",
                    this.getClass());
            return;
        }

        super.emit();
    }

    public Connection getConnection() {
        return connection;
    }
}
