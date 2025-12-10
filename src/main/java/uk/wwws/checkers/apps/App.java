package uk.wwws.checkers.apps;

import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.game.Game;
import uk.wwws.checkers.utils.DataParser;

public abstract class App implements DataParser {
    protected String helpText = "";

    public abstract void run();

    public @Nullable Game getGameState() {
        return null;
    }
}
