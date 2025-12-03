package uk.wwws.apps.entrypoints;

import java.util.NoSuchElementException;
import java.util.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.apps.App;
import uk.wwws.apps.ClientLikeApp;
import uk.wwws.game.Checker;
import uk.wwws.game.CheckersGame;
import uk.wwws.game.CheckersMove;
import uk.wwws.game.Move;
import uk.wwws.game.players.HumanPlayer;
import uk.wwws.net.Connection;
import uk.wwws.net.ConnectionSender;
import uk.wwws.net.PacketAction;
import uk.wwws.net.exceptions.FailedToConnectException;
import uk.wwws.net.threads.ConnectionDataHandler;
import uk.wwws.net.threads.ServerConnectionThread;
import uk.wwws.tui.CommandAction;

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

    static void main() {
        ClientApp.getInstance().run();
    }
}
