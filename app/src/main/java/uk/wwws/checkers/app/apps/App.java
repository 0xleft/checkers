package checkers.apps;

import java.util.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import checkers.ErrorType;
import checkers.game.Game;
import checkers.ui.CommandAction;
import checkers.ui.DataParser;

public interface App extends DataParser {
    void run();
    @NotNull ErrorType handleAction(@Nullable CommandAction action, @NotNull Scanner data);
    @Nullable Game getGameState();
}
