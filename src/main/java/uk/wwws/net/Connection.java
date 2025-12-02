package uk.wwws.net;

import java.io.*;
import java.net.Socket;
import org.jetbrains.annotations.NotNull;
import uk.wwws.net.exceptions.FailedToConnectException;
import uk.wwws.net.exceptions.FailedToCreateStreamsException;

public class Connection {
    private Socket socket;
    private @NotNull String host;
    private int port;
    private PrintWriter out;
    private BufferedReader in;

    // for client to connect to said socket
    public Connection(@NotNull Socket socket) throws FailedToCreateStreamsException {
        this.host = socket.getInetAddress().getHostName();
        this.port = socket.getPort();

        try {
            assignDataStreams();
        } catch (Exception e) {
            throw new FailedToCreateStreamsException("Failed to create data streams");
        }
    }

    // for client to connect to said socket
    public Connection(@NotNull String host, int port) throws FailedToConnectException {
        this.host = host;
        this.port = port;

        try {
            connect();
        } catch (Exception e) {
            throw new FailedToConnectException(host, port);
        }
    }

    private void connect() throws IOException {
        socket = new Socket(this.host, this.port);
        assignDataStreams();
    }

    private void assignDataStreams() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void write(@NotNull String data) {
        out.write(data);
    }

    public @NotNull String read() throws IOException {
        return in.readLine();
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    public Socket getSocket() {
        return socket;
    }
}
