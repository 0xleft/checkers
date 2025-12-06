package uk.wwws.checkers.ui.scenes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.ui.GUI;

public class GameScene extends StaticScene {
    private static final Logger logger = LogManager.getRootLogger();

    public GameScene(@NotNull GUI gui) {
        super(gui);
    }

    @Override
    public void initialize() {
        super.initialize();
    }
}
