package uk.wwws.apps.entrypoints;

import java.net.Socket;
import java.text.MessageFormat;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.apps.App;
import uk.wwws.apps.ServerLikeApp;
import uk.wwws.game.Checker;
import uk.wwws.game.CheckersGame;
import uk.wwws.game.CheckersMove;
import uk.wwws.game.Player;
import uk.wwws.game.players.ConnectedPlayer;
import uk.wwws.net.Connection;
import uk.wwws.net.ConnectionReceiver;
import uk.wwws.net.PacketAction;
import uk.wwws.net.threads.*;
import uk.wwws.tui.CommandAction;

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
