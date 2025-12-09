package checkers.game.players;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import checkers.game.CheckersGame;
import checkers.game.Player;
import checkers.net.Connection;

public class ConnectedPlayer implements Player {
    private @Nullable CheckersGame game = null;
    private final @NotNull Connection connection;

    public ConnectedPlayer(@NotNull Connection connection) {
        this.connection = connection;
    }

    public void setGame(@Nullable CheckersGame game) {
        this.game = game;
    }

    public @Nullable CheckersGame getGame() {
        return game;
    }

    public @NotNull Connection getConnection() {
        return connection;
    }
}
