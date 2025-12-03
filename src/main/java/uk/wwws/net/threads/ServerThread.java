package uk.wwws.net.threads;

import java.io.IOException;
import java.net.ServerSocket;
import uk.wwws.net.Connection;
import uk.wwws.net.exceptions.FailedToConnectException;

/**
 * Server thread which listens for client connections and spawns a new thread when acceptable
 */
public class ServerThread extends Thread {
    private int port;
    private NewConnectionHandler handler;
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
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                handler.handleNewConnection(socket.accept());
            } catch (IOException e) {
                throw new RuntimeException(
                        e); // i want to test to see what leads to this then fix // todo
            }
        }
    }
}
