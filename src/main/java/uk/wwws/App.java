package uk.wwws;

import org.jetbrains.annotations.Nullable;
import uk.wwws.game.CheckersGame;
import uk.wwws.tui.CommandAction;
import uk.wwws.tui.TUI;

abstract public class App extends TUI {
    protected final void run() {
        boolean stop = false;
        while (!stop) {
            CommandAction action = getNextAction();
            if (action == CommandAction.QUIT) stop = true;
            handleAction(action);
        }
    }

    protected abstract void handleAction(@Nullable CommandAction action);
}
