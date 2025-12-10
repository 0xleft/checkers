package uk.wwws.checkers.apps;

import java.util.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.ErrorType;
import uk.wwws.checkers.game.Game;
import uk.wwws.checkers.ui.CommandAction;
import uk.wwws.checkers.ui.DataParser;

public interface App extends DataParser {
    void run();
    @Nullable Game getGameState();
}
