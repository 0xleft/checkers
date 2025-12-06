package uk.wwws.checkers.ui.scenes;

import java.util.Scanner;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.game.Checker;
import uk.wwws.checkers.ui.CommandAction;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.UIAction;
import uk.wwws.checkers.ui.controllers.GameController;

public class GameScene extends StaticScene {
    private static final Logger logger = LogManager.getRootLogger();

    public GameScene(@NotNull GUI gui) {
        super("Game.fxml", gui);
    }

    @Override
    public void handleAction(@NotNull UIAction action, @Nullable Scanner data) {
        switch (action) {
            case BOARD_SYNC -> {
                // sync board
            }
            case DISCONNECT -> {
                SceneManager.getInstance().loadScene(LobbyScene.class, gui);
            }
            case GAMEOVER -> {

            }
            case ASSIGN_COLOR -> {

            }
        }
    }

    @Override
    public void initialize() {
        super.initialize();

        GameController controller = gui.getLoader().getController();

        System.out.print(controller.gameBoard.getChildren());

        controller.gameBoard.getRowConstraints().forEach(c -> {
            c.setValignment(VPos.CENTER);
        });

        controller.gameBoard.getColumnConstraints().forEach(c -> {
            c.setHalignment(HPos.CENTER);
        });

        for (int i = 0; i < controller.gameBoard.getRowCount(); i++) {
            for (int j = 0; j < controller.gameBoard.getColumnCount(); j++) {
                Rectangle rect = new Rectangle(50, 50);

                rect.setFill(Color.WHITESMOKE);
                if ((j + i + 1) % 2 == 0) {
                    rect.setFill(Color.DIMGRAY);
                }

                GridPane.setConstraints(rect, i, j);
                GridPane.setFillWidth(rect, true);
                GridPane.setFillHeight(rect, true);
                GridPane.setHgrow(rect, Priority.ALWAYS);
                GridPane.setVgrow(rect, Priority.ALWAYS);

                controller.gameBoard.getChildren().add(rect);
            }
        }
    }

    private void createBoard(@NotNull Checker color) {

    }
}
