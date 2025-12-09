package checkers.net;

import org.jetbrains.annotations.NotNull;
import checkers.ErrorType;

public interface ConnectionReceiver {
    Thread spawnServer(int port);

    @NotNull ErrorType stopServer();
}
