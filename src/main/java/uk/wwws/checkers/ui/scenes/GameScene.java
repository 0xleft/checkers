package uk.wwws.checkers.ui.scenes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.controllers.GameController;

public class GameScene extends StaticScene {
    private static final Logger logger = LogManager.getRootLogger();

    public GameScene(GUI gui) {
        super("Game.fxml", gui);
    }

    @Override
    public void initialize() {
        super.initialize();

        GameController controller = gui.getLoader().getController();
    }
}
