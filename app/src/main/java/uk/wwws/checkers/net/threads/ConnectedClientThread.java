package uk.wwws.checkers.net.threads;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.ErrorType;
import uk.wwws.checkers.game.players.ConnectedPlayer;

public class ConnectedClientThread extends Thread {
    private static final Logger logger = LogManager.getRootLogger();

    private final @NotNull ConnectedPlayer player;
    private final ConnectionDataHandler handler;

    public ConnectedClientThread(@NotNull ConnectedPlayer player, ConnectionDataHandler handler) {
        this.player = player;
        this.handler = handler;
    }

    public @NotNull ConnectedPlayer getPlayer() {
        return player;
    }

    @Override
    public void interrupt() {
        logger.debug("Interrupting client thread");
        super.interrupt();
        try {
            this.player.getConnection().disconnect();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void run() {
        logger.debug("Created new connected client thread");
        super.run();

        String inputLine;

        while (true) {
            try {
                inputLine = this.player.getConnection().read();
            } catch (IOException e) {
                break;
            }

            if (handler.handleData(inputLine, this.player.getConnection()) == ErrorType.FATAL) {
                break;
            }
        }

        // send null so client disconnects
        handler.handleData(null, this.player.getConnection());
    }
}
