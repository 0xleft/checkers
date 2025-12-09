package checkers.ui;

import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import checkers.apps.App;
import checkers.ui.scenes.LobbyScene;
import checkers.ui.scenes.SceneManager;
import uk.wwws.checkers.eventframework.EventHandlerContainer;

@EventHandlerContainer
public class GUI extends Application implements UI {
    private static final Logger logger = LogManager.getRootLogger();
    private @Nullable Stage currentStage;
    private @NotNull FXMLLoader loader;
    private static App app; // retarded ass bulshit fucking reflection hack

    public GUI() {
        loader = new FXMLLoader();
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

        SceneManager.getInstance().loadScene(LobbyScene.class, this);
        stage.setOnCloseRequest((WindowEvent _) -> {
            app.handleAction(CommandAction.QUIT, new Scanner(""));
            System.exit(0);
        });
    }

    @Override
    public void handleAction(@NotNull UIAction action, @Nullable Scanner data) {
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

    @Override
    public void setApp(@NotNull App newApp) {
        app = newApp;
    }

    @Override
    public @Nullable App getApp() {
        return app;
    }
}