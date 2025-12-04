package uk.wwws.apps;

import java.util.NoSuchElementException;
import java.util.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.ErrorType;
import uk.wwws.apps.exceptions.ServerErrorException;
import uk.wwws.game.*;
import uk.wwws.net.Connection;
import uk.wwws.net.ConnectionSender;
import uk.wwws.net.PacketAction;
import uk.wwws.net.exceptions.FailedToConnectException;
import uk.wwws.net.threads.ConnectionDataHandler;
import uk.wwws.net.threads.ServerConnectionThread;
import uk.wwws.ui.CommandAction;

public abstract class ClientLikeApp extends App implements ConnectionSender, ConnectionDataHandler {
    protected ServerConnectionThread connectionThread;
    protected CheckersGame game;

    protected Player player;

    protected ClientLikeApp(Player player) {
        this.player = player;
        game = new CheckersGame();
    }

    @Override
    public ErrorType handleData(@Nullable String data, @NotNull Connection c) {
        if (data == null) {
            reset();
            return ErrorType.FATAL;
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
                    return ErrorType.FATAL;
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred in handling data: " + e.getMessage() + "\n" + data);
            return ErrorType.ERROR;
        }

        return ErrorType.NONE;
    }

    protected void reset() {
        if (this.connectionThread != null) {
            this.connectionThread.interrupt();
        }
        this.connectionThread = null;
        this.game = new CheckersGame();
    }

    protected void handleError() throws ServerErrorException {
        System.out.println("Server sent back an error");
        throw new ServerErrorException("Server sent back an error");
    }

    protected void handleGameStart() {
        System.out.println("Your game has started");
        handleState();
    }

    protected void handleGameOver() {
        System.out.println("Game has ended");
        handleState();
        game = new CheckersGame();
    }

    protected void handleColorAssign(@NotNull Scanner input) {
        String a = input.next();
        Checker color = Checker.valueOf(a.toUpperCase());
        game.addPlayer(this.player, color);
    }

    public void handleReceiveMove(@Nullable Scanner input) {
        int fromIndex;
        int toIndex;

        try {
            fromIndex = input.nextInt();
            toIndex = input.nextInt();
        } catch (NoSuchElementException | IllegalStateException | NullPointerException e) {
            System.out.println("Invalid move packet: " + input);
            return;
        }

        Move move = new CheckersMove(fromIndex, toIndex);

        game.doMove(move);
        handleState();
    }

    protected void handleDisconnect() {
        System.out.println("Disconnecting from server");
        reset();
    }

    protected void handleState() {
        if (connectionThread != null) {
            System.out.println(connectionThread.getConnection());
        }

        if (game == null) {
            System.out.println("Not in game");
            return;
        }

        System.out.println(game);
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

    protected void handleQueue() {
        if (connectionThread == null) {
            System.out.println("You need to be connected to send moves");
            return;
        }

        connectionThread.getConnection().write(PacketAction.QUEUE);
    }

    protected void handleMove() {
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

        sendMove(new CheckersMove(from, to));
    }

    protected void sendMove(@NotNull CheckersMove move) {
        connectionThread.getConnection().write(PacketAction.MOVE, move.startIndex() + " " + move.endIndex());
    }

    protected void handleConnect() {
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
}
