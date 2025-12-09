package uk.wwws.checkers;

public enum ErrorType {
    NONE, ERROR, FATAL;

    public boolean isError() {
        return !this.equals(NONE);
    }
}
