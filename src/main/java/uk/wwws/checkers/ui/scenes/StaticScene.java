package uk.wwws.checkers.ui.scenes;

import java.io.IOException;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.UI;

abstract public class StaticScene extends Scene {
    private static final Logger logger = LogManager.getRootLogger();

    protected GUI gui;

    protected StaticScene(@NotNull GUI gui) {
        super(null);
        this.gui = gui;
    }

    protected void initialize() {
        gui.getCurrentStage().setScene(this);
        Scene scene;

        gui.getCurrentStage().setTitle("Checkers ");
        gui.getCurrentStage().show();
    }
}