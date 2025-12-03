package uk.wwws.apps;

import java.io.IOException;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.App;
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

public class ServerApp extends App
        implements ConnectionReceiver, ConnectionDataHandler, NewConnectionHandler {
    HashSet<ConnectedClientThread> connections = new HashSet<>();
    Queue<ConnectedPlayer> queue = new LinkedList<>();

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
    protected void handleAction(@Nullable CommandAction action) {
        switch (action) {
            case START_SERVER -> handleStartServer();
            case STOP_SERVER -> stopServer();
            case STATE -> displayState();
            case null, default -> System.out.println(
                    "Invalid command or wrong argument usage. Type help to get command list");
        }
    }

    private void displayState() {
        System.out.println("Size of connections: " + connections.size());
        System.out.println("Queue size: " + queue.size());
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
        System.out.println("Spawned new server");
        return newServerThread;
    }

    @Override
    public void handleNewConnection(@NotNull Socket socket) {
        System.out.println("New client connected");
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
    public boolean handleData(@Nullable String data, @NotNull Connection c) {
        if (data == null) {
            handleDisconnect(c);
            return false;
        }

        Scanner input = new Scanner(data);

        System.out.println("New data: " + data);

        try {
            switch (PacketAction.valueOf(input.next().toUpperCase())) {
                case QUEUE -> handleQueue(c);
                case MOVE -> handleMove(input, c);
                case BYE, ERROR -> {
                    handleDisconnect(c);
                    return false;
                }
                default -> {
                    c.write(PacketAction.ERROR);
                    return false;
                }
            }
        } catch (Exception e) {
            c.write(PacketAction.ERROR);
        }

        return true;
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
        System.out.println("Client trying to disconnect");
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
        System.out.println("Client disconnected");
    }

    private void handleMove(@NotNull Scanner input, @NotNull Connection c) {
        int fromIndex;
        int toIndex;

        try {
            fromIndex = input.nextInt();
            toIndex = input.nextInt();
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("Invalid move packet: " + Arrays.toString(input.tokens().toArray()));
            c.write(PacketAction.ERROR);
            return;
        }

        ConnectedPlayer player = getConnectedPlayer(c).getPlayer();
        CheckersGame game = player.getGame();
        if (game == null) {
            c.write(PacketAction.ERROR);
            System.out.println("Player of no game tried to make a move");
            return;
        }


        if (game.getTurn() != player) {
            c.write(PacketAction.ERROR);
            System.out.println("Player tried to move when its not its turn");
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

        System.out.println("Stopped server");
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
