package checkers.apps;

import java.net.Socket;
import java.text.MessageFormat;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import checkers.ErrorType;
import checkers.game.Checker;
import checkers.game.CheckersGame;
import checkers.game.Game;
import checkers.game.Player;
import checkers.game.exceptions.InvalidMoveException;
import checkers.game.moves.CheckersMove;
import checkers.game.players.ConnectedPlayer;
import checkers.net.Connection;
import checkers.net.ConnectionReceiver;
import checkers.net.PacketAction;
import checkers.net.threads.ConnectedClientThread;
import checkers.net.threads.ConnectionDataHandler;
import checkers.net.threads.NewConnectionHandler;
import checkers.net.threads.ServerThread;
import checkers.ui.CommandAction;
import checkers.ui.UI;

public abstract class ServerLikeApp
        implements App, ConnectionReceiver, ConnectionDataHandler, NewConnectionHandler {
    private static final Logger logger = LogManager.getRootLogger();

    private static final @NotNull String HELP_MENU = """
            START_SERVER <port> starts the server on a given port.
            STOP_SERVER         stops the server.
            STATE               prints the state of the server.
            HELP                prints this menu
            QUIT                closes the application""";
    protected UI ui;
    HashSet<ConnectedClientThread> connections = new HashSet<>();
    Queue<ConnectedPlayer> queue = new LinkedList<>();
    private @Nullable ServerThread serverThread;

    public @NotNull ErrorType handleAction(@Nullable CommandAction action, @NotNull Scanner data) {
        switch (action) {
            case START_SERVER -> {
                return handleStartServer(data);
            }
            case STOP_SERVER -> {
                return stopServer();
            }
            case STATE -> {
                return displayState();
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

    private @NotNull ErrorType displayState() {
        System.out.println("Size of connections: " + connections.size());
        System.out.println("Queue size: " + queue.size());
        return ErrorType.NONE;
    }

    private @NotNull ErrorType handleStartServer(@NotNull Scanner data) {
        Integer port = getNextInt(data);

        if (port == null) {
            logger.error("Incorrect usage, should be: start_server <port>");
            return ErrorType.ERROR;
        }

        if (serverThread != null) {
            stopServer();
        }

        this.serverThread = spawnServer(port);

        return ErrorType.NONE;
    }

    @Override
    public ServerThread spawnServer(int port) {
        ServerThread newServerThread = new ServerThread(port, this);
        try {
            newServerThread.start();
        } catch (Exception e) {
            logger.error("Port already in use select a different port");
        }

        logger.info("Spawned new server");
        return newServerThread;
    }

    @Override
    public void handleNewConnection(@NotNull Socket socket) {
        logger.info("New client connected");
        ConnectedClientThread client =
                new ConnectedClientThread(new ConnectedPlayer(new Connection(socket)), this);
        connections.add(client);
        client.start();
    }

    private @Nullable ConnectedClientThread getConnectedPlayer(@NotNull Connection c) {
        for (ConnectedClientThread connection : connections) {
            if (connection.getPlayer().getConnection() == c) {
                return connection;
            }
        }

        return null;
    }

    private boolean connectionIsNotAThread(@NotNull Connection c) {
        return getConnectedPlayer(c) == null;
    }

    @Override
    public ErrorType handleData(@Nullable String data, @NotNull Connection c) {
        if (data == null) {
            handleDisconnect(c);
            return ErrorType.FATAL;
        }

        Scanner input = new Scanner(data);

        logger.debug("New data: {}", data);

        try {
            switch (PacketAction.valueOf(input.next().toUpperCase())) {
                case QUEUE -> handleQueue(c);
                case MOVE -> handleMove(input, c);
                case GIVE_UP -> handleGiveUp(c);
                case BYE, ERROR -> {
                    handleDisconnect(c);
                    return ErrorType.FATAL;
                }
                default -> {
                    c.write(PacketAction.ERROR);
                    return ErrorType.FATAL;
                }
            }
        } catch (Exception e) {
            c.write(PacketAction.ERROR);
            return ErrorType.ERROR;
        }

        return ErrorType.NONE;
    }

    private void handleGiveUp(@NotNull Connection c) {
        if (connectionIsNotAThread(c)) {
            logger.error("Connection is not a thread: {}", c);
            return;
        }

        ConnectedPlayer player = getConnectedPlayer(c).getPlayer();
        if (player.getGame() == null) {
            return;
        }

        player.getGame().setSetLoser(player);
        disconnectPlayers(player.getGame());
    }

    private void handleQueue(@NotNull Connection c) {
        if (connectionIsNotAThread(c)) {
            return;
        }

        ConnectedPlayer player = getConnectedPlayer(c).getPlayer();
        if (queue.contains(player)) {
            player.getConnection().write(PacketAction.LEFT_QUEUE);
            queue.remove(player);
            return;
        }

        if (player.getGame() != null) { //player is already in a game
            player.getConnection().write(PacketAction.ERROR);
            return;
        }

        queue.add(player);
        player.getConnection().write(PacketAction.JOINED_QUEUE);
        checkQueue();
    }

    private void handleGameEnd(
            @NotNull Player player) { // was working on game packets and game end game tie nstuff
        if (player instanceof ConnectedPlayer cp) {
            if (cp.getGame().getWinner() == cp) {
                cp.getConnection().write(PacketAction.GAME_WON);
            } else {
                cp.getConnection().write(PacketAction.GAME_LOST);
            }

            cp.setGame(null);
        }
    }

    private void disconnectPlayers(@NotNull CheckersGame game) {
        for (Player otherPlayer : game.getPlayers()) {
            handleGameEnd(otherPlayer);
        }
    }

    private void handleDisconnect(@NotNull Connection c) {
        logger.info("Client trying to disconnect");
        if (connectionIsNotAThread(c)) {
            return;
        }

        ConnectedClientThread clientThread = getConnectedPlayer(c);
        ConnectedPlayer player = clientThread.getPlayer();
        if (player.getGame() != null) {
            player.getGame().setSetLoser(player);
            disconnectPlayers(player.getGame());
        }

        clientThread.interrupt();
        c.write(PacketAction.BYE);
        connections.remove(clientThread);
        queue.remove(player);
        logger.info("Client disconnected");
    }

    private void handleMove(@NotNull Scanner input, @NotNull Connection c) {
        int fromIndex;
        int toIndex;

        try {
            fromIndex = input.nextInt();
            toIndex = input.nextInt();
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.error("Invalid move packet: {}", Arrays.toString(input.tokens().toArray()));
            c.write(PacketAction.ERROR);
            return;
        }

        ConnectedPlayer player = getConnectedPlayer(c).getPlayer();
        CheckersGame game = player.getGame();
        if (game == null) {
            c.write(PacketAction.ERROR);
            logger.error("Player of no game tried to make a move");
            return;
        }

        if (game.getTurn() != player) {
            c.write(PacketAction.ERROR);
            logger.error("Player tried to move when its not its turn");
            return;
        }

        try {
            player.getGame().doMove(new CheckersMove(fromIndex, toIndex));
        } catch (InvalidMoveException e) {
            logger.error("Player tried to play invalid move: {} to {}", fromIndex, toIndex);
            if (player.getGame().getTurn() == player) {
                c.write(PacketAction.YOUR_MOVE);
            }
            return;
        }

        for (Player otherPlayer : game.getPlayers()) {
            ((ConnectedPlayer) otherPlayer).getConnection()
                    .write(PacketAction.MOVE, MessageFormat.format("{0} {1}", fromIndex, toIndex));
        }

        // send your move to next player
        if (player.getGame().getTurn() instanceof ConnectedPlayer cp) {
            cp.getConnection().write(PacketAction.YOUR_MOVE);
        }

        // handle game over
        if (game.isGameOver()) {
            for (Player otherPlayer : game.getPlayers()) {
                handleGameEnd(otherPlayer);
            }
        }
    }

    private void checkQueue() {
        if (queue.size() > 1) {
            CheckersGame game = new CheckersGame();
            ConnectedPlayer player1 = queue.poll();
            ConnectedPlayer player2 = queue.poll();
            // todo this randomization needs to be moved inside the game
            game.addPlayer(player1, Checker.WHITE);
            game.addPlayer(player2, Checker.BLACK);

            player1.setGame(game);
            player2.setGame(game);

            ((ConnectedPlayer) game.getPlayer(Checker.WHITE)).getConnection()
                    .write(PacketAction.ASSIGN_COLOR, Checker.WHITE.name())
                    .write(PacketAction.YOUR_MOVE);

            ((ConnectedPlayer) game.getPlayer(Checker.BLACK)).getConnection()
                    .write(PacketAction.ASSIGN_COLOR, Checker.BLACK.name());

            checkQueue();
        }
    }

    @Override
    public @NotNull ErrorType stopServer() {
        if (serverThread == null) {
            return ErrorType.ERROR;
        }

        serverThread.interrupt();
        reset();

        logger.info("Stopped server");
        return ErrorType.NONE;
    }

    private void reset() {
        this.serverThread = null;
        this.queue = new LinkedList<>();
        connections.forEach(c -> {
            c.getPlayer().getConnection().write(PacketAction.BYE);
            c.interrupt();
        });
        this.connections = new HashSet<>();
    }

    @Override
    public @Nullable Game getGameState() {
        return null;
    }
}
