package uk.wwws.checkers.net.threads;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.ErrorType;
import uk.wwws.checkers.net.Connection;

public interface ConnectionDataHandler {

    /**
     *
     * @param data
     * @param c
     * @return whether the handler wants to continue further handling (false = should exit)
     */
    ErrorType handleData(@Nullable String data, @NotNull Connection c);
}
