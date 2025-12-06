package uk.wwws.checkers.ui;

import java.io.IOException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.ui.scenes.LobbyScene;
import uk.wwws.checkers.ui.scenes.SceneManager;

abstract public class GUI extends Application implements UI {
    private @Nullable Stage currentStage;

    public @Nullable Stage getCurrentStage() {
        return currentStage;
    }

    @Override
    public void start(@NotNull Stage stage) {
        this.currentStage = stage;
    }

    @Override
    public void run() {
        launch();
    }
}
