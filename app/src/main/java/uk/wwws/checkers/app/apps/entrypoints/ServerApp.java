package uk.wwws.checkers.app.apps.entrypoints;

import uk.wwws.checkers.app.apps.ServerLikeApp;
import uk.wwws.checkers.app.ui.TUI;

public class ServerApp extends ServerLikeApp {

    private static ServerApp instance;

    public static ServerApp getInstance() {
        if (instance == null) {
            instance = new ServerApp();
        }

        return instance;
    }

    public static void main(String[] args) {
        ServerApp.getInstance().run();
    }

    @Override
    public void run() {
        this.ui = new TUI();
        ui.setApp(this);
        ui.run();
    }
}
