package uk.wwws.checkers.net.exceptions;

public class FailedToConnectException extends RuntimeException {
    public FailedToConnectException(String host, int port) {
        super("Failed to connect to " + host + ":" + port);
    }
}
