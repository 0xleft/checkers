package uk.wwws.checkers.ui;

import java.io.IOException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jdk.jshell.spi.ExecutionControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.apps.App;
import uk.wwws.checkers.ui.scenes.LobbyScene;
import uk.wwws.checkers.ui.scenes.SceneManager;

public class SimpleGUI extends GUI {
    private static final Logger logger = LogManager.getRootLogger();

    @Override
    public void start(@NotNull Stage stage) {
        super.start(stage);

        SceneManager.getInstance().loadScene(LobbyScene.class, this);
        stage.setOnCloseRequest((WindowEvent _) -> {
            handleAction(CommandAction.QUIT, new Scanner(""));
            System.exit(0);
        });
    }

    @Override
    public void handleAction(@NotNull CommandAction action, @NotNull Scanner data) {
        SceneManager.getInstance().getCurrentScene().handleAction(action, data);
    }

    @Override
    public void run() {
        if (app == null) {
            logger.error("App is null");
            return;
        }
        launch();
    }
}
