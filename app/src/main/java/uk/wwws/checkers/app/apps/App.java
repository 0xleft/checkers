package uk.wwws.checkers.app.apps;

import java.util.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.app.ErrorType;
import uk.wwws.checkers.app.game.Game;
import uk.wwws.checkers.app.ui.CommandAction;
import uk.wwws.checkers.app.ui.DataParser;

public interface App extends DataParser {
    void run();
    @NotNull ErrorType handleAction(@Nullable CommandAction action, @NotNull Scanner data);
    @Nullable Game getGameState();
}
