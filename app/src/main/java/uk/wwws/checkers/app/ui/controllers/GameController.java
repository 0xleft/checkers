package checkers.ui.controllers;

import java.util.Scanner;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import checkers.ui.CommandAction;

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
        gui.getApp().handleAction(CommandAction.GIVE_UP, new Scanner(""));
    }

    @FXML
    protected void onJoinQueueButtonAction() {
        gui.getApp().handleAction(CommandAction.QUEUE, new Scanner(""));
    }

    @FXML
    protected void onDisconnectButtonAction() {
        gui.getApp().handleAction(CommandAction.DISCONNECT, new Scanner(""));
    }
}
