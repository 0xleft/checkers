package uk.wwws.checkers.ui;

import java.util.Scanner;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.apps.App;
import uk.wwws.checkers.ui.scenes.SceneManager;

public class TUI implements UI, DataParser {
    private static final Logger logger = LogManager.getRootLogger();

    private final Scanner scanner;
    private @Nullable App app;

    public TUI() {
        scanner = new Scanner(System.in);
    }

    public void setApp(@NotNull App app) {
        this.app = app;
    }

    public @Nullable App getApp() {
        return app;
    }

    @Override
    public void handleAction(@NotNull CommandAction action, @NotNull Scanner data) {
    }

    public void run() {
        if (app == null) {
            logger.error("App is null");
            return;
        }

        boolean stop = false;
        while (!stop) {
            CommandAction nextAction = getNextCommandAction(scanner);
            if (nextAction == CommandAction.QUIT) {
                stop = true;
                continue;
            }
            app.handleAction(nextAction, scanner);
        }
    }
}
