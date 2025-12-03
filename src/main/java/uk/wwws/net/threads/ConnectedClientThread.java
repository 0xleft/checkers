package uk.wwws.net.threads;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.game.CheckersGame;
import uk.wwws.game.players.ConnectedPlayer;

/**
 * Server side thread which handles incomming communication from the client
 */
public class ConnectedClientThread extends Thread {
    private @NotNull ConnectedPlayer player;
    private ConnectionDataHandler handler;

    public ConnectedClientThread(@NotNull ConnectedPlayer player, ConnectionDataHandler handler) {
        this.player = player;
        this.handler = handler;
    }

    public @NotNull ConnectedPlayer getPlayer() {
        return player;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            this.player.getConnection().disconnect();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        System.out.println("test");

        super.run();

        String inputLine;

        while (true) {
            try {
                inputLine = this.player.getConnection().read();
            } catch (IOException e) {
                break;
            }

            if (!handler.handleData(inputLine, this.player.getConnection())) break;
        }

        System.out.println("test");
    }
}
