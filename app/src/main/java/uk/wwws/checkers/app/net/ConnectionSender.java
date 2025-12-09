package uk.wwws.checkers.app.net;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.app.net.exceptions.FailedToConnectException;
import uk.wwws.checkers.app.net.exceptions.FailedToCreateStreamsException;

public interface ConnectionSender {
    default @Nullable Connection connect(@NotNull String host, int port) {
        try {
            return new Connection(host, port);
        } catch (FailedToConnectException | FailedToCreateStreamsException e) {
            LogManager.getRootLogger()
                    .error("Error creating new connection: {}", e.getMessage());
            return null;
        }
    }
}
