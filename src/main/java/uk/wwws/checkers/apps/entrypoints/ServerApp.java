package uk.wwws.checkers.apps.entrypoints;

import uk.wwws.checkers.apps.ServerLikeApp;

public class ServerApp extends ServerLikeApp {

    private static ServerApp instance;

    public static ServerApp getInstance() {
        if (instance == null) {
            instance = new ServerApp();
        }

        return instance;
    }

    static void main() {
        ServerApp.getInstance().run();
    }
}
