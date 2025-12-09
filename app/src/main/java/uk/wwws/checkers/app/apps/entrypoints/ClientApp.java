package checkers.apps.entrypoints;

import checkers.apps.ClientLikeApp;
import checkers.game.players.HumanPlayer;
import checkers.ui.GUI;

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
