package uk.wwws.checkers.ui.scenes;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.UI;
import uk.wwws.checkers.ui.controllers.ReferencedController;

abstract public class StaticScene {
    private static final Logger logger = LogManager.getRootLogger();

    protected final String SCENE_FILE_URL;
    protected GUI gui;

    protected StaticScene(@NotNull String sceneFileUrl, @NotNull GUI gui) {
        SCENE_FILE_URL = sceneFileUrl;
        this.gui = gui;
    }

    protected void initialize() {
        gui.setLoader(new FXMLLoader());
        gui.getLoader().setLocation(getClass().getResource("/" + SCENE_FILE_URL));
        Scene scene;
        try {
            scene = new Scene(gui.getLoader().load(), 1200, 600);
        } catch (IOException e) {
            logger.error("Could not load scene: {} {}", SCENE_FILE_URL, e.getMessage());
            e.printStackTrace();
            return;
        }

        ((ReferencedController)(gui.getLoader().getController())).setGui(gui);

        if (gui.getCurrentStage() == null) {
            logger.error("Current scene is null while loading url: {}", SCENE_FILE_URL);
            return;
        }
        gui.getCurrentStage().setTitle("Checkers " + SCENE_FILE_URL);
        gui.getCurrentStage().setScene(scene);
        gui.getCurrentStage().show();
    }
}