package uk.wwws.net.threads;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import uk.wwws.ErrorType;
import uk.wwws.net.Connection;

/**
 * Client side therad which handles incomming communication from server
 */
public class ServerConnectionThread extends Thread {
    private ConnectionDataHandler handler;
    private @NotNull Connection connection;

    public ServerConnectionThread(@NotNull Connection c, ConnectionDataHandler handler) {
        this.connection = c;
        this.handler = handler;
    }

    public @NotNull Connection getConnection() {
        return connection;
    }

    public void run() {
        System.out.println("Started new server connection");

        super.run();

        String inputLine;

        while (true) {
            try {
                inputLine = connection.read();
            } catch (IOException e) {
                break;
            }

            if (handler.handleData(inputLine, connection) == ErrorType.FATAL) break;
        }

        handler.handleData(null, connection);
    }

    @Override
    public void interrupt() {
        super.interrupt();

        try {
            connection.disconnect();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
