package uk.wwws.checkers.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.UI;

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
}
