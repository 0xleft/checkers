package uk.wwws.checkers.ui.scenes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.eventframework.annotations.EventHandlerContainer;
import uk.wwws.checkers.events.ui.ConnectedUIEvent;
import uk.wwws.checkers.events.ui.FailedToConnectUIEvent;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.controllers.LobbyController;

@EventHandlerContainer
public class LobbyScene extends StaticScene {
    private static final Logger logger = LogManager.getRootLogger();

    public LobbyScene(@NotNull GUI gui) {
        super("Lobby.fxml", gui);
    }

    @EventHandler(isPlatform = true)
    public void handleFailedToConnect(FailedToConnectUIEvent event) {
        LobbyController controller = gui.getLoader().getController();
        controller.statusLabel.setText("Failed to connect... try again");
    }

    @Override
    public void initialize() {
        super.initialize();
    }
}
