package uk.wwws.checkers.ui.scenes;

import java.util.Scanner;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.game.Board;
import uk.wwws.checkers.game.Checker;
import uk.wwws.checkers.game.bitboards.Bitboard;
import uk.wwws.checkers.ui.CommandAction;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.UIAction;
import uk.wwws.checkers.ui.controllers.GameController;

public class GameScene extends StaticScene {
    private static final Logger logger = LogManager.getRootLogger();

    private GameController controller;
    private Checker perspective = Checker.WHITE;

    public GameScene(@NotNull GUI gui) {
        super("Game.fxml", gui);
    }

    @Override
    public void handleAction(@NotNull UIAction action, @Nullable Scanner data) {
        switch (action) {
            case BOARD_SYNC -> {
                drawBoard(perspective);
            }
            case DISCONNECT -> {
                SceneManager.getInstance().loadScene(LobbyScene.class, gui);
            }
            case GAMEOVER -> {
                controller.stateLabel.setText("Your game has ended");
                drawBoard(perspective);
            }
            case ASSIGN_COLOR -> {
                // change perspective variable
                perspective = Checker.valueOf(data.next());
                drawBoard(perspective);
            }
        }
    }

    private void drawBoard(@NotNull Checker perspective) {
        controller.gameBoard.getChildren().clear();

        for (int i = 0; i < controller.gameBoard.getRowCount(); i++) {
            for (int j = 0; j < controller.gameBoard.getColumnCount(); j++) {
                Rectangle rect = new Rectangle(50, 50);

                rect.setFill(Color.LIGHTGRAY);
                if ((j + i + (perspective == Checker.WHITE ? 0 : 1)) % 2 == 0) {
                    rect.setFill(Color.DIMGRAY);
                }

                GridPane.setConstraints(rect, j, i);
                GridPane.setFillWidth(rect, true);
                GridPane.setFillHeight(rect, true);
                GridPane.setHgrow(rect, Priority.ALWAYS);
                GridPane.setVgrow(rect, Priority.ALWAYS);

                controller.gameBoard.getChildren().add(rect);
            }
        }

        addPieces(perspective);
    }

    private void addPieces(@NotNull Checker perspective) {
        if (gui.getApp().getGameState() == null) {
            return;
        }

        Bitboard board = new Bitboard(gui.getApp().getGameState().getBoard().getCheckers(), null,
                                      Board.DIM); // non empty fields

        board.getOnIndexes().forEach(i -> {
            int row = gui.getApp().getGameState().getBoard().getRow(i);
            int col = gui.getApp().getGameState().getBoard().getCol(i);
            if (perspective.sameColor(Checker.BLACK)) {
                row = board.getBoardDim() - row - 1;
            }
            addPiece(gui.getApp().getGameState().getBoard().getField(i), row, col);
        });
    }

    private void addPiece(Checker piece, int row, int col) {
        switch (piece) {
            case BLACK_QUEEN -> addQueen(Color.BLACK, row, col);
            case WHITE_QUEEN -> addQueen(Color.WHITESMOKE, row, col);
            case WHITE -> addPawn(Color.WHITESMOKE, row, col);
            case BLACK -> addPawn(Color.BLACK, row, col);
        }
    }

    private void addPawn(Paint color, int row, int col) {
        Circle circle = new Circle(20);

        circle.setFill(color);

        GridPane.setConstraints(circle, col, row);
        GridPane.setFillWidth(circle, true);
        GridPane.setFillHeight(circle, true);
        GridPane.setHgrow(circle, Priority.ALWAYS);
        GridPane.setVgrow(circle, Priority.ALWAYS);

        

        controller.gameBoard.getChildren().add(circle);
    }

    private void addQueen(Paint color, int row, int col) {
        Circle circle = new Circle(20);

        circle.setFill(color);

        GridPane.setConstraints(circle, col, row);
        GridPane.setFillWidth(circle, true);
        GridPane.setFillHeight(circle, true);
        GridPane.setHgrow(circle, Priority.ALWAYS);
        GridPane.setVgrow(circle, Priority.ALWAYS);

        controller.gameBoard.getChildren().add(circle);
    }

    @Override
    public void initialize() {
        super.initialize();
        controller = gui.getLoader().getController();

        controller.gameBoard.getRowConstraints().forEach(c -> {
            c.setValignment(VPos.CENTER);
        });
        controller.gameBoard.getColumnConstraints().forEach(c -> {
            c.setHalignment(HPos.CENTER);
        });

        drawBoard(Checker.WHITE);
    }
}
