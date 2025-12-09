package uk.wwws.checkers.net.threads;

import java.io.IOException;
import java.net.ServerSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.net.exceptions.FailedToConnectException;

public class ServerThread extends Thread {
    private static final Logger logger = LogManager.getRootLogger();

    private final int port;
    private final NewConnectionHandler handler;
    private ServerSocket socket;

    public ServerThread(int port, NewConnectionHandler handler) throws FailedToConnectException {
        this.port = port;
        this.handler = handler;
    }

    public void run() {
        super.run();

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Failed to bind to port: {}", port);
            return;
        }

        while (true) {
            try {
                handler.handleNewConnection(socket.accept());
            } catch (IOException e) {
                logger.error("Error in handling new connection: {}", e.getMessage());
            }
        }
    }
}
