package uk.wwws.checkers.apps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.eventframework.annotations.EventHandlerContainer;
import uk.wwws.checkers.eventframework.annotations.Priority;
import uk.wwws.checkers.events.commands.*;
import uk.wwws.checkers.events.net.*;
import uk.wwws.checkers.events.ui.BoardSyncUIEvent;
import uk.wwws.checkers.events.ui.ConnectedUIEvent;
import uk.wwws.checkers.events.ui.DisconnectedUIEvent;
import uk.wwws.checkers.game.CheckersGame;
import uk.wwws.checkers.game.Game;
import uk.wwws.checkers.game.Player;
import uk.wwws.checkers.game.moves.CheckersMove;
import uk.wwws.checkers.game.moves.Move;
import uk.wwws.checkers.net.Connection;
import uk.wwws.checkers.net.PacketAction;
import uk.wwws.checkers.net.exceptions.FailedToConnectException;
import uk.wwws.checkers.net.threads.ServerConnectionThread;
import uk.wwws.checkers.ui.UI;

@EventHandlerContainer
public abstract class ClientLikeApp extends App {
    private static final Logger logger = LogManager.getRootLogger();

    public static final boolean IS_CLIENT = true;
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
        this.helpText = """
                CONNECT <hostname> <port>   connects to a server with given hostname and port.
                DISCONNECT                  disconnects from the server.
                STATE                       prints the state of the application.
                MOVE                        <fromIndex> <toIndex> sends a move to the server with the given indecision.
                QUEUE                       sends queue command to the server. If we are already in the
                                            queue server will remove us from there, otherwise we will be put in a queue.
                GIVE_UP                     sends a give up command to the server.
                HELP                        prints this menu.
                QUIT                        closes the application""";
    }

    protected void reset() {
        if (this.connectionThread != null) {
            this.connectionThread.interrupt();
        }
        this.connectionThread = null;
        this.game = new CheckersGame();
    }

    @EventHandler(priority = Priority.HIGHEST)
    protected void handleJoinedQueue(JoinedQueueConnectionEvent event) {
        logger.info("Server put us in the queue");
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleLeftQueue(LeftQueueConnectionEvent event) {
        logger.info("Server removed us from the queue");
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleError(ErrorConnectionEvent event) {
        logger.error("Server sent back an error");
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleYourMove(YourMoveConnectionEvent event) {
        logger.info("Its your move");
        new StateCommandEvent().emit();
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleGameWon(GameWonConnectionEvent event) {
        logger.info("You won the game");
        game = new CheckersGame();
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleGameLost(GameLostConnectionEvent event) {
        logger.info("You lost the game");
        game = new CheckersGame();
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleColorAssign(AssignColorConnectionEvent event) {
        game.addPlayer(this.player, event.getColor());
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleReceiveMove(MoveConnectionEvent event) {
        Move move = new CheckersMove(event.getFromSquare(), event.getToSquare());

        game.doMove(move);
        new StateCommandEvent().emit();
        new BoardSyncUIEvent().emit();
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleDisconnect(DisconnectCommandEvent event) {
        logger.info("Disconnecting from server");
        reset();
        new DisconnectedUIEvent().emit();
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleState(StateCommandEvent event) {
        if (connectionThread != null) {
            logger.info(connectionThread.getConnection());
        }

        if (game == null) {
            logger.error("Not in game");
        }

        logger.info(game);
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleHelpMenu(HelpCommandEvent event) {
        logger.info(this.helpText);
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleGiveUp(GiveUpCommandEvent event) {
        logger.debug("Sending a give up command to server");
        connectionThread.getConnection().write(PacketAction.GIVE_UP);
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleQueue(QueueCommandEvent event) {
        if (connectionThread == null) {
            logger.error("You need to be connected to send moves");
            return;
        }

        connectionThread.getConnection().write(PacketAction.QUEUE);
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleMove(MoveCommandEvent event) {
        if (connectionThread == null) {
            logger.error("You need to be connected to send moves");
            return;
        }

        if (game == null) {
            logger.error("You need to be in game to send moves");
            return;
        }

        sendMove(new CheckersMove(event.getFromSquare(), event.getToSquare()));
    }

    protected void sendMove(@NotNull CheckersMove move) {
        connectionThread.getConnection()
                .write(PacketAction.MOVE, move.startIndex() + " " + move.endIndex());
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void handleConnect(ConnectCommandEvent event) {
        Connection connection;

        try {
            connection = new Connection(event.getHost(), Integer.parseInt(event.getPort()));
        } catch (FailedToConnectException | NumberFormatException e) {
            logger.error("Invalid usage. Use connect <host:string> <port:int> {}", e.getMessage());
            return;
        }

        connectionThread = new ServerConnectionThread(connection);
        connectionThread.start();

        logger.info("Created new connection");
        new ConnectedUIEvent().emit();
    }

    @Override
    public @Nullable Game getGameState() {
        return game;
    }
}
