package checkers.net.threads;

import java.net.Socket;
import org.jetbrains.annotations.NotNull;

public interface NewConnectionHandler {
    void handleNewConnection(@NotNull Socket socket);
}
