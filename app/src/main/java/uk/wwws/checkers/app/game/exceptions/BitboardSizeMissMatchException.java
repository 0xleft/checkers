package uk.wwws.checkers.app.game.exceptions;

public class BitboardSizeMissMatchException extends RuntimeException {
    public BitboardSizeMissMatchException(String message) {
        super(message);
    }
}
