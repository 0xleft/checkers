package uk.wwws.checkers.net.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.events.net.NewConnectionEvent;
import uk.wwws.checkers.net.Connection;
import uk.wwws.checkers.net.exceptions.FailedToConnectException;
import uk.wwws.checkers.net.exceptions.FailedToCreateStreamsException;

public class ServerThread extends Thread {
    private static final Logger logger = LogManager.getRootLogger();

    private final int port;

    public ServerThread(int port) {
        this.port = port;
    }

    public void run() {
        super.run();

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Failed to bind to port: {}", port);
            return;
        }

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new NewConnectionEvent().setConnection(new Connection(clientSocket)).emit();
            } catch (IOException | FailedToCreateStreamsException e) {
                logger.error("Error in handling new connection: {}", e.getMessage());
            }
        }
    }
}
