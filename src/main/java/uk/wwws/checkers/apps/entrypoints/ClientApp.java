package uk.wwws.checkers.apps.entrypoints;

import uk.wwws.checkers.apps.ClientLikeApp;
import uk.wwws.checkers.game.players.HumanPlayer;
import uk.wwws.checkers.ui.SimpleGUI;

public class ClientApp extends ClientLikeApp {
    private static ClientApp instance;

    public static ClientApp getInstance() {
        if (instance == null) {
            instance = new ClientApp();
        }

        return instance;
    }

    public ClientApp() {
        super(new HumanPlayer());
    }

    public static void main(String[] args) {
        ClientApp.getInstance().run();
    }

    @Override
    public void run() {
        this.ui = new SimpleGUI();
        ui.setApp(this);
        ui.run();
    }
}
