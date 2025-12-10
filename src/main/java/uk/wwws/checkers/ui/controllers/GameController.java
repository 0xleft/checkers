package uk.wwws.checkers.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.ui.CommandParser;

public class GameController extends ReferencedController {
    private static final Logger logger = LogManager.getRootLogger();

    @FXML
    public Button joinQueueButton;
    @FXML
    public Button disconnectButton;
    @FXML
    public Label stateLabel;
    @FXML
    public GridPane gameBoard;
    @FXML
    public Pane backgroundPane;
    @FXML
    public Button giveUpButton;

    @FXML
    protected void onGiveUpButtonAction() {
        CommandParser.getInstance().parseAction("GIVE_UP");
    }

    @FXML
    protected void onJoinQueueButtonAction() {
        CommandParser.getInstance().parseAction("QUEUE");
    }

    @FXML
    protected void onDisconnectButtonAction() {
        CommandParser.getInstance().parseAction("DISCONNECT");
    }
}
