package uk.wwws.net.threads;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.ErrorType;
import uk.wwws.net.Connection;

public interface ConnectionDataHandler {

    /**
     *
     * @param data
     * @param c
     * @return whether the handler wants to continue further handling (false = should exit)
     */
    ErrorType handleData(@Nullable String data, @NotNull Connection c);
}
