package uk.wwws.checkers.app.net.exceptions;

public class FailedToConnectException extends RuntimeException {
    public FailedToConnectException(String host, int port) {
        super("Failed to connect to " + host + ":" + port);
    }
}
