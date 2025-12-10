package uk.wwws.checkers.ui;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.apps.App;
import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.eventframework.annotations.EventHandlerContainer;
import uk.wwws.checkers.eventframework.annotations.Priority;
import uk.wwws.checkers.events.commands.QuitCommandEvent;
import uk.wwws.checkers.events.commands.StopServerCommandEvent;
import uk.wwws.checkers.utils.DataParser;

@EventHandlerContainer
public class TUI implements UI {
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

    public void run() {
        if (app == null) {
            logger.error("App is null");
            return;
        }

        while (true) {
            CommandParser.getInstance().parseAction(scanner.nextLine());
        }
    }

    @EventHandler(priority = Priority.LOWEST)
    public void handleQuit(QuitCommandEvent event) {
        System.exit(0);
    }
}
