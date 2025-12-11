package uk.wwws.checkers.ui.scenes;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.controllers.ReferencedController;

public class StaticScene {
    private static final Logger logger = LogManager.getRootLogger();

    protected final String sceneFileURL;
    protected @NotNull GUI gui;

    protected StaticScene(@NotNull String sceneFileUrl, @NotNull GUI gui) {
        sceneFileURL = sceneFileUrl;
        this.gui = gui;
    }

    protected void initialize() {
        gui.setLoader(new FXMLLoader());
        gui.getLoader().setLocation(getClass().getResource("/" + sceneFileURL));
        Scene scene;
        try {
            scene = new Scene(gui.getLoader().load(), 1200, 600);
        } catch (IOException e) {
            logger.error("Could not load scene: {} {}", sceneFileURL, e.getMessage());
            return;
        }

        ((ReferencedController) gui.getLoader().getController()).setGui(gui);

        if (gui.getCurrentStage() == null) {
            logger.error("Current scene is null while loading url: {}", sceneFileURL);
            return;
        }

        gui.getCurrentStage().setTitle("Checkers " + sceneFileURL);
        gui.getCurrentStage().setScene(scene);
        gui.getCurrentStage().show();
    }
}