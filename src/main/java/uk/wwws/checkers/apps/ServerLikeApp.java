package uk.wwws.checkers.apps;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.eventframework.annotations.EventHandlerContainer;
import uk.wwws.checkers.events.commands.HelpCommandEvent;
import uk.wwws.checkers.events.commands.StartServerCommandEvent;
import uk.wwws.checkers.events.commands.StateCommandEvent;
import uk.wwws.checkers.events.net.*;
import uk.wwws.checkers.game.Checker;
import uk.wwws.checkers.game.CheckersGame;
import uk.wwws.checkers.game.Player;
import uk.wwws.checkers.game.exceptions.InvalidMoveException;
import uk.wwws.checkers.game.moves.CheckersMove;
import uk.wwws.checkers.game.players.ConnectedPlayer;
import uk.wwws.checkers.net.Connection;
import uk.wwws.checkers.net.PacketAction;
import uk.wwws.checkers.net.threads.ConnectedClientThread;
import uk.wwws.checkers.net.threads.ServerThread;
import uk.wwws.checkers.ui.UI;

@EventHandlerContainer
public abstract class ServerLikeApp extends App {
    private static final Logger logger = LogManager.getRootLogger();

    protected UI ui;
    HashSet<ConnectedClientThread> connections = new HashSet<>();
    Queue<ConnectedPlayer> queue = new LinkedList<>();
    private @Nullable ServerThread serverThread;

    public ServerLikeApp() {
        this.helpText = """
                START_SERVER <port> starts the server on a given port.
                STOP_SERVER         stops the server.
                STATE               prints the state of the server.
                HELP                prints this menu
                QUIT                closes the application""";
    }

    @EventHandler
    private void handleHelpMenu(HelpCommandEvent event) {
        logger.info(this.helpText);
    }

    @EventHandler
    private void displayState(StateCommandEvent event) {
        System.out.println("Size of connections: " + connections.size());
        System.out.println("Queue size: " + queue.size());
    }

    @EventHandler
    private void handleStartServer(StartServerCommandEvent event) {
        if (serverThread != null) {
            stopServer();
        }

        this.serverThread = spawnServer(event.getPort());
    }

    public ServerThread spawnServer(int port) {
        ServerThread newServerThread = new ServerThread(port);
        try {
            newServerThread.start();
        } catch (Exception e) {
            logger.error("Port already in use select a different port");
        }

        logger.info("Spawned new server");
        return newServerThread;
    }

    @EventHandler
    public void handleNewConnection(NewConnectionEvent event) {
        logger.info("New client connected");
        ConnectedClientThread client =
                new ConnectedClientThread(new ConnectedPlayer(event.getConnection()));
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

    @EventHandler
    private void handleGiveUp(GiveUpConnectionEvent event) {
        Connection connection = event.getConnection();
        if (connectionIsNotAThread(connection)) {
            logger.error("Connection is not a thread: {}", connection);
            return;
        }

        ConnectedPlayer player = getConnectedPlayer(connection).getPlayer();
        if (player.getGame() == null) {
            return;
        }

        player.getGame().setSetLoser(player);
        disconnectPlayers(player.getGame());
    }

    @EventHandler
    private void handleQueue(QueueConnectionEvent event) {
        Connection connection = event.getConnection();
        if (connectionIsNotAThread(connection)) {
            return;
        }

        ConnectedPlayer player = getConnectedPlayer(connection).getPlayer();
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

    private void handleGameEnd(@NotNull Player player) {
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

    @EventHandler
    private void handleDisconnect(DisconnectClientConnectionEvent event) {
        Connection connection = event.getConnection();
        logger.info("Client trying to disconnect");
        if (connectionIsNotAThread(connection)) {
            return;
        }

        ConnectedClientThread clientThread = getConnectedPlayer(connection);
        ConnectedPlayer player = clientThread.getPlayer();
        if (player.getGame() != null) {
            player.getGame().setSetLoser(player);
            disconnectPlayers(player.getGame());
        }

        clientThread.interrupt();
        connection.write(PacketAction.BYE);
        connections.remove(clientThread);
        queue.remove(player);
        logger.info("Client disconnected");
    }

    @EventHandler
    private void handleMove(MoveConnectionEvent event) {
        Connection connection = event.getConnection();

        ConnectedPlayer player = getConnectedPlayer(connection).getPlayer();
        CheckersGame game = player.getGame();
        if (game == null) {
            connection.write(PacketAction.ERROR);
            logger.error("Player of no game tried to make a move");
            return;
        }

        if (game.getTurn() != player) {
            connection.write(PacketAction.ERROR);
            logger.error("Player tried to move when its not its turn");
            return;
        }

        try {
            player.getGame().doMove(new CheckersMove(event.getFromSquare(), event.getToSquare()));
            logger.debug("Played a move: {} {}", event.getFromSquare(), event.getToSquare());
        } catch (InvalidMoveException e) {
            logger.error("Player tried to play invalid move: {} to {}", event.getFromSquare(),
                         event.getToSquare());
            if (player.getGame().getTurn() == player) {
                connection.write(PacketAction.YOUR_MOVE);
            }
            return;
        }

        for (Player otherPlayer : game.getPlayers()) {
            ((ConnectedPlayer) otherPlayer).getConnection().write(PacketAction.MOVE,
                                                                  MessageFormat.format("{0} {1}",
                                                                                       event.getFromSquare(),
                                                                                       event.getToSquare()));
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

    public void stopServer() {
        if (serverThread == null) {
            logger.warn("Tried to stop server when server is not on");
            return;
        }

        serverThread.interrupt();
        reset();

        logger.info("Stopped server");
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
