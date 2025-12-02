package uk.wwws.apps;

import java.net.Socket;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.App;
import uk.wwws.game.CheckersGame;
import uk.wwws.game.players.ConnectedPlayer;
import uk.wwws.net.Connection;
import uk.wwws.net.ConnectionReceiver;
import uk.wwws.net.ConnectionSender;
import uk.wwws.net.threads.*;
import uk.wwws.tui.Action;

public class ServerApp extends App
        implements ConnectionReceiver, ConnectionDataHandler, NewConnectionHandler {
    HashSet<ConnectedClientThread> connections = new HashSet<>();
    HashSet<CheckersGame> games = new HashSet<>();
    private @Nullable ServerThread serverThread;

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

    @Override
    protected void handleAction(@Nullable Action action) {
        switch (action) {
            case START_SERVER -> handleStartServer();
            case STOP_SERVER -> stopServer();
            case null, default -> System.out.println(
                    "Invalid command or wrong argument usage. Type help to get command list");
        }
    }

    private void handleStartServer() {
        Integer port = getNextIntArg();

        if (port == null) {
            System.out.println("Incorrect usage, should be: start_server <port>");
            return;
        }

        this.serverThread = spawnServer(port);
    }

    @Override
    public ServerThread spawnServer(int port) {
        ServerThread newServerThread = new ServerThread(port, this);
        newServerThread.start();
        return newServerThread;
    }

    @Override
    public void handleNewConnection(@NotNull Socket socket) {
        System.out.println("New client connected");
        connections.add(
                new ConnectedClientThread(new ConnectedPlayer(new Connection(socket)), this));
    }

    @Override
    public boolean handleData(@NotNull String data, @NotNull Connection c) {
        return false;
    }

    @Override
    public void stopServer() {
        if (serverThread == null) {
            return;
        }

        serverThread.interrupt();
    }
}
