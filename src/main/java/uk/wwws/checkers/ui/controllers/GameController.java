package uk.wwws.checkers.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.events.commands.DisconnectCommandEvent;
import uk.wwws.checkers.events.commands.GiveUpCommandEvent;
import uk.wwws.checkers.events.commands.QueueCommandEvent;

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
        new GiveUpCommandEvent().emit();
    }

    @FXML
    protected void onJoinQueueButtonAction() {
        new QueueCommandEvent().emit();
    }

    @FXML
    protected void onDisconnectButtonAction() {
        new DisconnectCommandEvent().emit();
    }
}
