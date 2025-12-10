package uk.wwws.checkers.apps;

import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.eventframework.annotations.EventHandlerContainer;
import uk.wwws.checkers.eventframework.annotations.Priority;
import uk.wwws.checkers.events.commands.QuitCommandEvent;
import uk.wwws.checkers.game.Game;
import uk.wwws.checkers.utils.DataParser;

public abstract class App implements DataParser {
    protected String helpText = "";

    public abstract void run();
    public @Nullable Game getGameState() {
        return null;
    }
}
