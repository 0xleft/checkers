package uk.wwws.apps;

import java.net.Socket;
import java.text.MessageFormat;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.ErrorType;
import uk.wwws.game.Checker;
import uk.wwws.game.CheckersGame;
import uk.wwws.game.Player;
import uk.wwws.game.moves.CheckersMove;
import uk.wwws.game.players.ConnectedPlayer;
import uk.wwws.net.Connection;
import uk.wwws.net.ConnectionReceiver;
import uk.wwws.net.PacketAction;
import uk.wwws.net.threads.ConnectedClientThread;
import uk.wwws.net.threads.ConnectionDataHandler;
import uk.wwws.net.threads.NewConnectionHandler;
import uk.wwws.net.threads.ServerThread;
import uk.wwws.ui.CommandAction;
import uk.wwws.ui.TUI;
import uk.wwws.ui.UI;

public abstract class ServerLikeApp extends TUI
        implements ConnectionReceiver, ConnectionDataHandler, NewConnectionHandler {
    private static final Logger logger = LogManager.getLogger(ServerLikeApp.class);

    HashSet<ConnectedClientThread> connections = new HashSet<>();
    Queue<ConnectedPlayer> queue = new LinkedList<>();
    private @Nullable ServerThread serverThread;

    public void handleAction(@Nullable CommandAction action, @NotNull Scanner data) {
        switch (action) {
            case START_SERVER -> handleStartServer(data);
            case STOP_SERVER -> stopServer();
            case STATE -> displayState();
            case null, default -> logger.error(
                    "Invalid command or wrong argument usage. Type help to get command list");
        }
    }

    private void displayState() {
        System.out.println("Size of connections: " + connections.size());
        System.out.println("Queue size: " + queue.size());
    }

    private void handleStartServer(@NotNull Scanner data) {
        Integer port = getNextInt(data);

        if (port == null) {
            logger.error("Incorrect usage, should be: start_server <port>");
            return;
        }

        this.serverThread = spawnServer(port);
    }

    @Override
    public ServerThread spawnServer(int port) {
        ServerThread newServerThread = new ServerThread(port, this);
        newServerThread.start();
        logger.debug("Spawned new server");
        return newServerThread;
    }

    @Override
    public void handleNewConnection(@NotNull Socket socket) {
        logger.debug("New client connected");
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

    private void handleQueue(@NotNull Connection c) {
        if (connectionIsNotAThread(c)) {
            return;
        }

        ConnectedPlayer player = getConnectedPlayer(c).getPlayer();
        if (queue.contains(player)) {
            player.getConnection().write(PacketAction.ERROR);
            return;
        }

        queue.add(player);
        checkQueue();
    }

    private void handleGameEnd(@NotNull Player player) {
        if (player instanceof ConnectedPlayer cp) {
            cp.setGame(null);
            cp.getConnection().write(PacketAction.GAMEOVER);
        }
    }

    private void handleDisconnect(@NotNull Connection c) {
        logger.debug("Client trying to disconnect");
        if (connectionIsNotAThread(c)) {
            return;
        }

        ConnectedClientThread clientThread = getConnectedPlayer(c);
        ConnectedPlayer player = clientThread.getPlayer();
        if (player.getGame() != null) {
            for (Player otherPlayer : player.getGame().getPlayers()) {
                handleGameEnd(otherPlayer);
            }
        }

        clientThread.interrupt();
        c.write(PacketAction.BYE);
        connections.remove(clientThread);
        queue.remove(player);
        logger.debug("Client disconnected");
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

        player.getGame().doMove(new CheckersMove(fromIndex, toIndex));
        for (Player otherPlayer : game.getPlayers()) {
            ((ConnectedPlayer) otherPlayer).getConnection()
                    .write(PacketAction.MOVE, MessageFormat.format("{0} {1}", fromIndex, toIndex));
        }

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
            game.addPlayer(player1, Checker.WHITE);
            game.addPlayer(player2, Checker.BLACK);
            player1.setGame(game);
            player2.setGame(game);

            player1.getConnection().write(PacketAction.ASSIGN_COLOR, Checker.WHITE.name())
                    .write(PacketAction.GAMESTART);
            player2.getConnection().write(PacketAction.ASSIGN_COLOR, Checker.BLACK.name())
                    .write(PacketAction.GAMESTART);

            checkQueue();
        }
    }

    @Override
    public void stopServer() {
        if (serverThread == null) {
            return;
        }

        serverThread.interrupt();
        reset();

        logger.debug("Stopped server");
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
}
