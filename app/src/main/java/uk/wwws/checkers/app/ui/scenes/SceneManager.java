package uk.wwws.checkers.app.ui.scenes;

import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.app.ui.GUI;

public class SceneManager {
    private static final Logger logger = LogManager.getRootLogger();

    private static SceneManager instance;

    public static SceneManager getInstance() {
        if  (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    private StaticScene currentScene;

    public StaticScene getCurrentScene() {
        return currentScene;
    }

    public <T> void loadScene(Class<T> sceneClass, @NotNull GUI gui) {
        try {
            currentScene = (StaticScene) sceneClass.getDeclaredConstructor(GUI.class).newInstance(gui);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            logger.error("Error while loading scene: {}, error: {}", sceneClass, e.getMessage());
            return;
        }

        currentScene.initialize();
    }
}