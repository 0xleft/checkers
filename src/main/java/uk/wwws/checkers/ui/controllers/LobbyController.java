package uk.wwws.checkers.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.eventframework.annotations.EventHandlerContainer;
import uk.wwws.checkers.events.commands.ConnectCommandEvent;
import uk.wwws.checkers.events.ui.FailedToConnectUIEvent;

@EventHandlerContainer
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

        new ConnectCommandEvent().setHost(hostnameField.getText()).setPort(portField.getText()).emit();
        joinButton.setDisable(false);
    }

    @EventHandler
    public void handleFailedToConnect(FailedToConnectUIEvent event) {
        statusLabel.setVisible(true);
        statusLabel.setText("Failed to connect...");
    }
}
