package uk.wwws.checkers.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.net.exceptions.FailedToConnectException;
import uk.wwws.checkers.net.exceptions.FailedToCreateStreamsException;

public class Connection {
    private static final Logger logger = LogManager.getRootLogger();

    private Socket socket;
    private final @NotNull String host;
    private final int port;
    private OutputStreamWriter out;
    private BufferedReader in;
    private final UUID id = UUID.randomUUID();

    // for client to connect to said socket
    public Connection(@NotNull Socket socket) throws FailedToCreateStreamsException {
        this.host = socket.getInetAddress().getHostName();
        this.port = socket.getPort();
        this.socket = socket;

        try {
            assignDataStreams();
        } catch (IOException e) {
            throw new FailedToCreateStreamsException("Failed to create data streams");
        }
    }

    // for client to connect to said socket
    public Connection(@NotNull String host, int port) throws FailedToConnectException {
        this.host = host;
        this.port = port;

        try {
            connect();
        } catch (IOException e) {
            throw new FailedToConnectException(host, port);
        }
    }

    private void connect() throws IOException {
        socket = new Socket(this.host, this.port);
        assignDataStreams();
    }

    private void assignDataStreams() throws IOException {
        out = new OutputStreamWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public @NotNull Connection write(@NotNull String data) {
        try {
            out.write(data);
            out.flush();
        } catch (IOException e) {
            logger.error("Error writing to connection: {}", e.getMessage());
        }
        return this;
    }

    public @NotNull Connection write(@NotNull PacketAction action) {
        write(action.toString()).write("\n");
        return this;
    }

    public @NotNull Connection write(@NotNull PacketAction action, @NotNull String data) {
        write(action + " " + data + "\n");
        return this;
    }

    public @NotNull String read() throws IOException {
        return in.readLine();
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Connection c) {
            return c.id == this.id;
        }

        return false;
    }

    @Override
    public String toString() {
        return "Connected to: " + host + ":" + port;
    }
}
