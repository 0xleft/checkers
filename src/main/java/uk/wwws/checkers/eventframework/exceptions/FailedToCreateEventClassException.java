package uk.wwws.checkers.eventframework.exceptions;

public class FailedToCreateEventClassException extends RuntimeException {
    public FailedToCreateEventClassException(Class<?> clazz) {
        super("Failed to create instance of class: " + clazz);
    }
}
