package uk.wwws.checkers.ui.controllers;

import java.util.Scanner;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.ErrorType;
import uk.wwws.checkers.ui.CommandAction;

public class LobbyController extends ReferencedController {
    private static final Logger logger = LogManager.getRootLogger();

    @FXML
    public Button joinButton;
    @FXML
    public TextField hostnameField;
    @FXML
    public TextField portField;
    @FXML
    public Label statusLabel;

    @FXML
    protected void onJoinButtonAction() {
        joinButton.setDisable(true);
        statusLabel.setText("Connecting...");
        statusLabel.setVisible(true);

        // todo handle null pointer however at this point if its null here a lot more shit is realdy fucked up
        ErrorType err = gui.getApp().handleAction(CommandAction.CONNECT,
                                   new Scanner(hostnameField.getText() + " " + portField.getText()));

        joinButton.setDisable(false);
        if (err.isError()) {
            statusLabel.setVisible(true);
            statusLabel.setText("Failed to connect...");
        }
    }
}
