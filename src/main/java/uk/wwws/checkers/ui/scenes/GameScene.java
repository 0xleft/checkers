package uk.wwws.checkers.ui.scenes;

import java.util.Scanner;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
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
    private int selectedId = -1;

    public GameScene(@NotNull GUI gui) {
        super("Game.fxml", gui);
    }

    @Override
    public void handleAction(@NotNull UIAction action, @Nullable Scanner data) {
        switch (action) {
            case BOARD_SYNC -> drawBoard();
            case DISCONNECT -> SceneManager.getInstance().loadScene(LobbyScene.class, gui);
            case GAME_WON -> {
                controller.stateLabel.setText("You won!");
                controller.joinQueueButton.setDisable(false);
                controller.giveUpButton.setDisable(true);
            }
            case GAME_LOST -> {
                controller.stateLabel.setText("You lost!");
                controller.joinQueueButton.setDisable(false);
                controller.giveUpButton.setDisable(true);
            }
            case LEFT_QUEUE -> {
                controller.stateLabel.setText("You left the queue");
                controller.joinQueueButton.setText("Join queue");
            }
            case JOINED_QUEUE -> {
                controller.stateLabel.setText("You joined the queue");
                controller.joinQueueButton.setText("Leave queue");
            }
            case YOUR_MOVE -> controller.stateLabel.setText("It's your turn to move");
            case ASSIGN_COLOR -> {
                // change perspective variable
                perspective = Checker.valueOf(data.next());
                drawBoard();
                controller.stateLabel.setText("You are now playing");
                controller.joinQueueButton.setText("Join queue");
                controller.joinQueueButton.setDisable(true);
                controller.giveUpButton.setDisable(false);
            }
        }
    }

    private void drawBoard() {
        controller.gameBoard.getChildren().clear();

        for (int i = 0; i < controller.gameBoard.getRowCount(); i++) {
            for (int j = 0; j < controller.gameBoard.getColumnCount(); j++) {
                Rectangle rect = new Rectangle(50, 50);

                rect.setFill(Color.LIGHTGRAY);
                if ((j + i) % 2 == 0) {
                    rect.setFill(Color.DIMGRAY);
                }

                containGridPiece(rect, i, j);
                addMoveHandler(rect, i, j);
                controller.gameBoard.getChildren().add(rect);
            }
        }

        addPieces();
    }

    private void addPieces() {
        if (gui.getApp().getGameState() == null) {
            return;
        }

        Bitboard board = new Bitboard(gui.getApp().getGameState().getBoard().getCheckers(), null,
                                      Board.DIM); // non-empty fields

        board.getOnIndexes().forEach(i -> {
            int row = gui.getApp().getGameState().getBoard().getRow(getAdjustedIndex(i));
            int col = gui.getApp().getGameState().getBoard().getCol(getAdjustedIndex(i));

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

    private int getAdjustedIndex(int index) {
        if (perspective.equals(Checker.WHITE)) {
            return index;
        }

        return Board.DIM * Board.DIM - index - 1;
    }

    private void addMoveHandler(@NotNull Node node, int row, int col) {
        node.setOnMouseClicked((MouseEvent _) -> {
            int adjustedIndex = getAdjustedIndex(gui.getApp().getGameState().getBoard().index(row, col));

            if (gui.getApp().getGameState().getBoard().getField(adjustedIndex) !=
                    Checker.EMPTY) {
                selectedId = adjustedIndex;

                System.out.println(perspective + " " + selectedId + " " + gui.getApp().getGameState().getBoard().getField(adjustedIndex));
                return;
            }

            if (selectedId != -1) {
                String coords = selectedId + " " + adjustedIndex;
                selectedId = -1;

                controller.stateLabel.setText("Waiting for opponent to move");
                gui.getApp().handleAction(CommandAction.MOVE, new Scanner(coords));
            }
        });
    }

    private void containGridPiece(@NotNull Node piece, int row, int col) {
        GridPane.setConstraints(piece, col, row);
        GridPane.setFillWidth(piece, true);
        GridPane.setFillHeight(piece, true);
        GridPane.setHgrow(piece, Priority.ALWAYS);
        GridPane.setVgrow(piece, Priority.ALWAYS);
    }

    private Circle createMovablePiece(Paint color, int row, int col) {
        Circle circle = new Circle(
                controller.gameBoard.getRowConstraints().getFirst().getPrefHeight() / 1.5);

        circle.setFill(color);
        containGridPiece(circle, row, col);
        addMoveHandler(circle, row, col);

        return circle;
    }

    private void addQueen(Paint color, int row, int col) {
        Circle piece = createMovablePiece(color, row, col);
        piece.setStroke(Color.GOLD);
        piece.setStrokeWidth(3);
        controller.gameBoard.getChildren().add(piece);
    }

    private void addPawn(Paint color, int row, int col) {
        controller.gameBoard.getChildren().add(createMovablePiece(color, row, col));
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

        drawBoard();
    }
}
