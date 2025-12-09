package uk.wwws.checkers.app.net.threads;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.app.ErrorType;
import uk.wwws.checkers.app.net.Connection;

public class ServerConnectionThread extends Thread {
    private static final Logger logger = LogManager.getRootLogger();

    private final ConnectionDataHandler handler;
    private final @NotNull Connection connection;

    public ServerConnectionThread(@NotNull Connection c, ConnectionDataHandler handler) {
        this.connection = c;
        this.handler = handler;
    }

    public @NotNull Connection getConnection() {
        return connection;
    }

    public void run() {
        logger.debug("Started new server connection");

        super.run();

        String inputLine;

        while (true) {
            try {
                inputLine = connection.read();
            } catch (IOException e) {
                break;
            }

            if (handler.handleData(inputLine, connection) == ErrorType.FATAL) {
                break;
            }
        }

        handler.handleData(null, connection);
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
