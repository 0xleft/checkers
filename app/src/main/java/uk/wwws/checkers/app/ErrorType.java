package uk.wwws.checkers.app;

public enum ErrorType {
    NONE, ERROR, FATAL;

    public boolean isError() {
        return !this.equals(NONE);
    }
}
