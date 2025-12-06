package uk.wwws.checkers.ui.scenes;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.ui.CommandAction;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.controllers.LobbyController;

public class LobbyScene extends StaticScene {
    private static final Logger logger = LogManager.getRootLogger();

    public LobbyScene(GUI gui) {
        super("Lobby.fxml", gui);
    }

    @Override
    public void handleAction(@NotNull CommandAction action, @NotNull Scanner data) {

    }

    @Override
    public void initialize() {
        super.initialize();

        LobbyController controller = gui.getLoader().getController();
    }
}
