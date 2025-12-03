package uk.wwws.apps;

import java.util.NoSuchElementException;
import java.util.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.App;
import uk.wwws.game.Checker;
import uk.wwws.game.CheckersGame;
import uk.wwws.game.CheckersMove;
import uk.wwws.game.players.HumanPlayer;
import uk.wwws.net.Connection;
import uk.wwws.net.ConnectionSender;
import uk.wwws.net.PacketAction;
import uk.wwws.net.exceptions.FailedToConnectException;
import uk.wwws.net.threads.ConnectionDataHandler;
import uk.wwws.net.threads.ServerConnectionThread;
import uk.wwws.net.threads.ServerThread;
import uk.wwws.tui.CommandAction;

public class ClientApp extends App implements ConnectionSender, ConnectionDataHandler {
    private HumanPlayer player;
    private ServerConnectionThread connectionThread;
    private CheckersGame game;

    private static ClientApp instance;

    public static ClientApp getInstance() {
        if (instance == null) {
            instance = new ClientApp();
        }

        return instance;
    }

    public ClientApp() {
        player = new HumanPlayer();
        game = new CheckersGame();
    }

    static void main() {
        ClientApp.getInstance().run();
    }

    @Override
    protected void handleAction(@Nullable CommandAction action) {
        switch (action) {
            case CONNECT -> handleConnect();
            case DISCONNECT -> handleDisconnect();
            case STATE -> handleState();
            case MOVE -> handleMove();
            case QUEUE -> handleQueue();
            case null, default -> System.out.println(
                    "Invalid command or wrong argument usage. Type help to get command list");
        }
    }

    private void handleQueue() {
        if (connectionThread == null) {
            System.out.println("You need to be connected to send moves");
            return;
        }

        connectionThread.getConnection().write(PacketAction.QUEUE);
    }

    private void handleState() {
        if (connectionThread != null) {
            System.out.println(connectionThread.getConnection());
        }

        if (game == null) {
            System.out.println("Not in game");
            return;
        }

        System.out.println(game);
    }

    private void handleMove() {
        if (connectionThread == null) {
            System.out.println("You need to be connected to send moves");
            return;
        }

        if (game == null) {
            System.out.println("You need to be in game to send moves");
            return;
        }

        if (game.getTurn() != this.player) {
            System.out.println("It's not your turn to move");
            // return;
        }

        Integer from = getNextIntArg();
        Integer to = getNextIntArg();

        if (from == null || to == null) {
            System.out.println("Incorrect usage. Use: move <fromindex> <toindex>");
            return;
        }

        connectionThread.getConnection().write(PacketAction.MOVE, from.toString() + " " + to.toString());
    }

    private void handleDisconnect() {
        System.out.println("Disconnecting from server");
        reset();
    }

    private void handleConnect() {
        String host = getNextStringArg();
        Integer port = getNextIntArg();

        System.out.println("Connecting to: " + host + ":" + port);

        if (host == null || port == null) {
            System.out.println("Invalid usage. Use: connect <host> <port>");
            return;
        }

        Connection connection;

        try {
            connection = new Connection(host, port);
        } catch (FailedToConnectException e) {
            System.out.println(e.getMessage());
            return;
        }

        connectionThread = new ServerConnectionThread(connection, this);
        connectionThread.start();

        System.out.println("Created new connection");
    }

    @Override
    public boolean handleData(@Nullable String data, @NotNull Connection c) {
        if (data == null) {
            reset();
            return false;
        }

        Scanner input = new Scanner(data);

        try {
            switch (PacketAction.valueOf(input.next().toUpperCase())) {
                case ASSIGN_COLOR -> handleColorAssign(input);
                case MOVE -> handleReceiveMove(input);
                case GAMEOVER -> handleGameOver();
                case GAMESTART -> handleGameStart();
                case ERROR -> handleError();
                case BYE -> {
                    handleDisconnect();
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred in handling data: " + e.getMessage() + "\n" + data);
        }

        return true;
    }

    private void handleError() {
        System.out.println("Server sent back an error");
    }

    private void handleGameStart() {
        System.out.println("Your game has started");
        handleState();
    }

    private void handleGameOver() {
        System.out.println("Game has ended");
        handleState();
        game = new CheckersGame();
    }

    private void handleColorAssign(@NotNull Scanner input) {
        String a = input.next();

        System.out.println("Got oclor assign: " + a);

        Checker color = Checker.valueOf(a.toUpperCase());

        game.addPlayer(this.player, color);
    }

    private void handleReceiveMove(@Nullable Scanner input) {
        int fromIndex;
        int toIndex;

        try {
            fromIndex = input.nextInt();
            toIndex = input.nextInt();
        } catch (NoSuchElementException | IllegalStateException | NullPointerException e) {
            System.out.println("Invalid move packet: " + input);
            return;
        }

        game.doMove(new CheckersMove(fromIndex, toIndex));
        handleState();
    }

    private void reset() {
        if (this.connectionThread != null) {
            this.connectionThread.interrupt();
        }
        this.connectionThread = null;
        this.game = new CheckersGame();
    }
}
