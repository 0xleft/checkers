package checkers.apps;

import java.util.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import checkers.ErrorType;
import checkers.apps.exceptions.ServerErrorException;
import checkers.game.Checker;
import checkers.game.CheckersGame;
import checkers.game.Game;
import checkers.game.Player;
import checkers.game.moves.CheckersMove;
import checkers.game.moves.Move;
import checkers.net.Connection;
import checkers.net.ConnectionSender;
import checkers.net.PacketAction;
import checkers.net.exceptions.FailedToConnectException;
import checkers.net.threads.ConnectionDataHandler;
import checkers.net.threads.ServerConnectionThread;
import checkers.ui.CommandAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import checkers.ui.UI;
import checkers.ui.UIAction;

public abstract class ClientLikeApp
        implements App, ConnectionSender, ConnectionDataHandler {
    private static final Logger logger = LogManager.getRootLogger();

    private static final @NotNull String HELP_MENU = """
           CONNECT <hostname> <port>   connects to a server with given hostname and port.
           DISCONNECT                  disconnects from the server.
           STATE                       prints the state of the application.
           MOVE                        <fromIndex> <toIndex> sends a move to the server with the given indecision.
           QUEUE                       sends queue command to the server. If we are already in the
                                       queue server will remove us from there, otherwise we will be put in a queue.
           GIVE_UP                     sends a give up command to the server.
           HELP                        prints this menu.
           QUIT                        closes the application""";
    protected UI ui;
    protected ServerConnectionThread connectionThread;
    protected CheckersGame game;
    protected Player player;

    public CheckersGame getGame() {
        return game;
    }

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
                case GAME_WON -> handleGameWon();
                case GAME_LOST -> handleGameLost();
                case YOUR_MOVE -> handleYourMove();
                case ERROR -> handleError();
                case JOINED_QUEUE -> handleJoinedQueue();
                case LEFT_QUEUE -> handleLeftQueue();
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

    protected void handleJoinedQueue() {
        logger.info("Server put us in the queue");
        ui.handleAction(UIAction.JOINED_QUEUE, null, true);
    }

    protected void handleLeftQueue() {
        logger.info("Server removed us from the queue");
        ui.handleAction(UIAction.LEFT_QUEUE, null, true);
    }

    protected void handleError() throws ServerErrorException {
        logger.error("Server sent back an error");
        throw new ServerErrorException("Server sent back an error");
    }

    protected void handleYourMove() {
        logger.info("Its your move");
        handleState();
        ui.handleAction(UIAction.YOUR_MOVE, null, true);
    }

    protected void handleGameWon() {
        logger.info("You won the game");
        game = new CheckersGame();
        ui.handleAction(UIAction.GAME_WON, null, true);
    }

    protected void handleGameLost() {
        logger.info("You lost the game");
        game = new CheckersGame();
        ui.handleAction(UIAction.GAME_LOST, null, true);
    }

    protected void handleColorAssign(@NotNull Scanner input) {
        String data = getNext(input);
        if (data == null) {
            logger.error("Received wrong color assign, null");
            return;
        }

        Checker color = Checker.valueOf(data.toUpperCase());
        game.addPlayer(this.player, color);
        ui.handleAction(UIAction.ASSIGN_COLOR, new Scanner(data), true);
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
        ui.handleAction(UIAction.BOARD_SYNC, null, true);
    }

    protected @NotNull ErrorType handleDisconnect() {
        logger.info("Disconnecting from server");
        reset();
        ui.handleAction(UIAction.DISCONNECT, null, true);
        return ErrorType.NONE;
    }

    protected @NotNull ErrorType handleState() {
        if (connectionThread != null) {
            logger.info(connectionThread.getConnection());
        }

        if (game == null) {
            logger.error("Not in game");
            return ErrorType.NONE;
        }

        logger.info(game);
        return ErrorType.NONE;
    }

    public @NotNull ErrorType handleAction(@Nullable CommandAction action, @NotNull Scanner data) {
        switch (action) {
            case CONNECT -> {
                return handleConnect(data);
            }
            case DISCONNECT -> {
                return handleDisconnect();
            }
            case STATE -> {
                return handleState();
            }
            case MOVE -> {
                return handleMove(data);
            }
            case QUEUE -> {
                return handleQueue();
            }
            case GIVE_UP -> {
                return handleGiveUp();
            }
            case HELP -> {
                return handleHelpMenu();
            }
            case null, default -> {
                logger.error(
                        "Invalid command or wrong argument usage. Type help to get command list");
                return ErrorType.ERROR;
            }
        }
    }

    private @NotNull ErrorType handleHelpMenu() {
        logger.info(HELP_MENU);
        return ErrorType.NONE;
    }

    protected @NotNull ErrorType handleGiveUp() {
        logger.debug("Sending a give up command to server");
        connectionThread.getConnection().write(PacketAction.GIVE_UP);
        return ErrorType.NONE;
    }

    protected @NotNull ErrorType handleQueue() {
        if (connectionThread == null) {
            logger.error("You need to be connected to send moves");
            return ErrorType.ERROR;
        }

        connectionThread.getConnection().write(PacketAction.QUEUE);
        return ErrorType.NONE;
    }

    protected @NotNull ErrorType handleMove(@NotNull Scanner data) {
        if (connectionThread == null) {
            logger.error("You need to be connected to send moves");
            return ErrorType.ERROR;
        }

        if (game == null) {
            logger.error("You need to be in game to send moves");
            return ErrorType.ERROR;
        }

        if (game.getTurn() != this.player) {
            logger.error("It's not your turn to move");
            // return;
        }

        Integer from = getNextInt(data);
        Integer to = getNextInt(data);

        if (from == null || to == null) {
            logger.error("Incorrect usage. Use: move <fromindex> <toindex>");
            return ErrorType.ERROR;
        }

        sendMove(new CheckersMove(from, to));

        return ErrorType.NONE;
    }

    protected void sendMove(@NotNull CheckersMove move) {
        connectionThread.getConnection()
                .write(PacketAction.MOVE, move.startIndex() + " " + move.endIndex());
    }

    protected @NotNull ErrorType handleConnect(@NotNull Scanner data) {
        String host = getNext(data);
        Integer port = getNextInt(data);

        logger.info("Connecting to: {}:{}", host, port);

        if (host == null || port == null) {
            logger.error("Invalid usage. Use: connect <host> <port>");
            return ErrorType.ERROR;
        }

        Connection connection;

        try {
            connection = new Connection(host, port);
        } catch (FailedToConnectException e) {
            logger.error(e.getMessage());
            return ErrorType.ERROR;
        }

        connectionThread = new ServerConnectionThread(connection, this);
        connectionThread.start();

        logger.info("Created new connection");
        ui.handleAction(UIAction.CONNECTED, null, true);
        return ErrorType.NONE;
    }

    @Override
    public @Nullable Game getGameState() {
        return game;
    }
}
