package uk.wwws.checkers.ui.scenes;

import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.eventframework.Listener;
import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.events.ui.ConnectedUIEvent;
import uk.wwws.checkers.events.ui.DisconnectedUIEvent;
import uk.wwws.checkers.ui.GUI;

public class SceneManager implements Listener {
    private static final Logger logger = LogManager.getRootLogger();

    private static SceneManager instance;

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    private StaticScene currentScene = null;
    private GUI gui;

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public <T> void loadScene(@NotNull Class<T> sceneClass, @NotNull GUI gui) {
        try {
            currentScene =
                    (StaticScene) sceneClass.getDeclaredConstructor(GUI.class).newInstance(gui);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                 NoSuchMethodException e) {
            logger.error("Error while loading scene: {}, error: {}", sceneClass, e.getMessage());
            e.printStackTrace();
            return;
        }

        currentScene.initialize();
    }

    @EventHandler(isPlatform = true)
    public void handleConnected(ConnectedUIEvent event) {
        SceneManager.getInstance().loadScene(GameScene.class, gui);
    }

    @EventHandler(isPlatform = true)
    public void handleDisconnect(DisconnectedUIEvent event) {
        SceneManager.getInstance().loadScene(LobbyScene.class, gui);
    }
}