package uk.wwws.checkers.app.net;

import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.app.ErrorType;

public interface ConnectionReceiver {
    Thread spawnServer(int port);

    @NotNull ErrorType stopServer();
}
