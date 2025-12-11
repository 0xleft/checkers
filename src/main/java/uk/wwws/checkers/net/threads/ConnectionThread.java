package uk.wwws.checkers.net.threads;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.events.net.DisconnectClientConnectionEvent;
import uk.wwws.checkers.net.Connection;
import uk.wwws.checkers.net.PacketParser;

public class ConnectionThread extends Thread {
    private static final Logger logger = LogManager.getRootLogger();

    private final @NotNull Connection connection;

    public ConnectionThread(@NotNull Connection c) {
        this.connection = c;
    }

    public @NotNull Connection getConnection() {
        return connection;
    }

    public void run() {
        logger.debug("Started new connection");

        super.run();

        String inputLine;

        while (true) {
            inputLine = connection.read();
            if (inputLine == null) {
                break;
            }

            PacketParser.getInstance().parsePacket(connection, inputLine);
        }

        new DisconnectClientConnectionEvent().setConnection(connection).emit();
    }

    @Override
    public void interrupt() {
        super.interrupt();

        try {
            connection.disconnect();
        } catch (IOException e) {
            logger.error("Error in interrupting server connection thread: {}", e.getMessage());
        }
    }
}
