package uk.wwws.net.threads;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
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

    public void run() {
        super.run();

        String inputLine;

        while (true) {
            try {
                inputLine = connection.read();
            } catch (IOException e) {
                break;
            }

            if (!handler.handleData(inputLine, connection)) break;
        }
    }
}
