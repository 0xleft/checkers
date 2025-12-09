package uk.wwws.checkers.app.apps.entrypoints;

import uk.wwws.checkers.app.apps.ClientLikeApp;
import uk.wwws.checkers.app.game.players.HumanPlayer;
import uk.wwws.checkers.app.ui.GUI;

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
        this.ui = new GUI();
        ui.setApp(this);
        ui.run();
    }
}
