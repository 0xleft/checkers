package uk.wwws.checkers.ui;

import java.io.IOException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.apps.App;
import uk.wwws.checkers.ui.scenes.LobbyScene;
import uk.wwws.checkers.ui.scenes.SceneManager;

abstract public class GUI extends Application implements UI {
    private @Nullable Stage currentStage;
    private @NotNull FXMLLoader loader;
    protected @Nullable App app;

    public GUI() {
        loader = new FXMLLoader();
    }

    public void setApp(@NotNull App app) {
        this.app = app;
    }

    public @Nullable App getApp() {
        return app;
    }

    public @NotNull FXMLLoader getLoader() {
        return loader;
    }

    public void setLoader(@NotNull FXMLLoader loader) {
        this.loader = loader;
    }

    public @Nullable Stage getCurrentStage() {
        return currentStage;
    }

    @Override
    public void start(@NotNull Stage stage) {
        this.currentStage = stage;
        this.loader = new FXMLLoader();
    }
}