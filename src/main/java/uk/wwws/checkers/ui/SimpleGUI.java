package uk.wwws.checkers.ui;

import java.io.IOException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.ui.scenes.LobbyScene;
import uk.wwws.checkers.ui.scenes.SceneManager;

abstract public class SimpleGUI extends GUI {
    @Override
    public void start(@NotNull Stage stage) {
        super.start(stage);

        return;

//        SceneManager.getInstance().loadScene(LobbyScene.class, this);
//        stage.setOnCloseRequest((WindowEvent w) -> {
//            handleAction(CommandAction.QUIT, new Scanner(""));
//            System.exit(0);
//        });
    }
}
