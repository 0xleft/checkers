package uk.wwws.checkers.net;

import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.ErrorType;

public interface ConnectionReceiver {
    Thread spawnServer(int port);

    @NotNull ErrorType stopServer();
}
