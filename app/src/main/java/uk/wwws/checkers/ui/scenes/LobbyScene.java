package uk.wwws.checkers.ui.scenes;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.UIAction;

public class LobbyScene extends StaticScene {
    private static final Logger logger = LogManager.getRootLogger();

    public LobbyScene(@NotNull GUI gui) {
        super("Lobby.fxml", gui);
    }

    @Override
    public void handleAction(@NotNull UIAction action, @Nullable Scanner data) {
        switch (action) {
            case CONNECTED -> {
                SceneManager.getInstance().loadScene(GameScene.class, gui);
            }
        }
    }

    @Override
    public void initialize() {
        super.initialize();
    }
}
