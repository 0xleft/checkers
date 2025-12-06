package uk.wwws.checkers.apps;

import java.util.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.ErrorType;
import uk.wwws.checkers.apps.exceptions.ServerErrorException;
import uk.wwws.checkers.game.Checker;
import uk.wwws.checkers.game.CheckersGame;
import uk.wwws.checkers.game.Player;
import uk.wwws.checkers.game.moves.CheckersMove;
import uk.wwws.checkers.game.moves.Move;
import uk.wwws.checkers.net.Connection;
import uk.wwws.checkers.net.ConnectionSender;
import uk.wwws.checkers.net.PacketAction;
import uk.wwws.checkers.net.exceptions.FailedToConnectException;
import uk.wwws.checkers.net.threads.ConnectionDataHandler;
import uk.wwws.checkers.net.threads.ServerConnectionThread;
import uk.wwws.checkers.ui.CommandAction;
import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.SimpleGUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.ui.SimpleTUI;

public abstract class ClientLikeApp extends SimpleGUI
        implements ConnectionSender, ConnectionDataHandler {
    private static final Logger logger = LogManager.getRootLogger();

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
            logger.error("Error occurred in handling data: {}\n{}", e.getMessage(), data);
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
        logger.error("Server sent back an error");
        throw new ServerErrorException("Server sent back an error");
    }

    protected void handleGameStart() {
        logger.info("Your game has started");
        handleState();
    }

    protected void handleGameOver() {
        logger.info("Game has ended");
        handleState();
        game = new CheckersGame();
    }

    protected void handleColorAssign(@NotNull Scanner input) {
        String data = getNext(input);
        if (data == null) {
            logger.error("Received wrong color assign, null");
            return;
        }

        Checker color = Checker.valueOf(data.toUpperCase());
        game.addPlayer(this.player, color);
    }

    public void handleReceiveMove(@NotNull Scanner data) {
        Integer fromIndex = getNextInt(data);
        Integer toIndex = getNextInt(data);

        if (fromIndex == null || toIndex == null) {
            logger.error("Invalid move: from: {} to: {}", fromIndex, toIndex);
            return;
        }

        Move move = new CheckersMove(fromIndex, toIndex);

        game.doMove(move);
        handleState();
    }

    protected void handleDisconnect() {
        logger.info("Disconnecting from server");
        reset();
    }

    protected void handleState() {
        if (connectionThread != null) {
            System.out.println(connectionThread.getConnection());
        }

        if (game == null) {
            logger.error("Not in game");
            return;
        }

        System.out.println(game);
    }

    public void handleAction(@Nullable CommandAction action, @NotNull Scanner data) {
        switch (action) {
            case CONNECT -> handleConnect(data);
            case DISCONNECT -> handleDisconnect();
            case STATE -> handleState();
            case MOVE -> handleMove(data);
            case QUEUE -> handleQueue();
            case null, default -> logger.error(
                    "Invalid command or wrong argument usage. Type help to get command list");
        }
    }

    protected void handleQueue() {
        if (connectionThread == null) {
            logger.error("You need to be connected to send moves");
            return;
        }

        connectionThread.getConnection().write(PacketAction.QUEUE);
    }

    protected void handleMove(@NotNull Scanner data) {
        if (connectionThread == null) {
            logger.error("You need to be connected to send moves");
            return;
        }

        if (game == null) {
            logger.error("You need to be in game to send moves");
            return;
        }

        if (game.getTurn() != this.player) {
            logger.error("It's not your turn to move");
            // return;
        }

        Integer from = getNextInt(data);
        Integer to = getNextInt(data);

        if (from == null || to == null) {
            logger.error("Incorrect usage. Use: move <fromindex> <toindex>");
            return;
        }

        sendMove(new CheckersMove(from, to));
    }

    protected void sendMove(@NotNull CheckersMove move) {
        connectionThread.getConnection()
                .write(PacketAction.MOVE, move.startIndex() + " " + move.endIndex());
    }

    protected void handleConnect(@NotNull Scanner data) {
        String host = getNext(data);
        Integer port = getNextInt(data);

        logger.info("Connecting to: {}:{}", host, port);

        if (host == null || port == null) {
            logger.error("Invalid usage. Use: connect <host> <port>");
            return;
        }

        Connection connection;

        try {
            connection = new Connection(host, port);
        } catch (FailedToConnectException e) {
            logger.error(e.getMessage());
            return;
        }

        connectionThread = new ServerConnectionThread(connection, this);
        connectionThread.start();

        logger.info("Created new connection");
    }
}
