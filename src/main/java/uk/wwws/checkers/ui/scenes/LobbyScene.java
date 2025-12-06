package uk.wwws.checkers.ui.scenes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.controllers.LobbyController;

public class LobbyScene extends StaticScene {
    private static final Logger logger = LogManager.getRootLogger();

    public LobbyScene(GUI gui) {
        super("Lobby.fxml", gui);
    }

    @Override
    public void initialize() {
        super.initialize();

        LobbyController controller = gui.getLoader().getController();
    }
}
