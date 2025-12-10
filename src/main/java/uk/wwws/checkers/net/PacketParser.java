package uk.wwws.checkers.net;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.events.net.*;
import uk.wwws.checkers.game.Checker;
import uk.wwws.checkers.utils.DataParser;

public class PacketParser implements DataParser {
    private static final Logger logger = LogManager.getRootLogger();

    private static PacketParser instance;

    public static PacketParser getInstance() {
        if (instance == null) {
            instance = new PacketParser();
        }
        return instance;
    }

    public void parsePacket(@NotNull Connection connection, @Nullable String data) {
        if (data == null) {
            logger.debug("Tried parsing a packet when data is null");
            return;
        }

        Scanner input = new Scanner(data);
        logger.trace("New packet data received: {}", data);

        try {
            switch (PacketAction.valueOf(input.next().toUpperCase())) {
                case QUEUE -> handleQueue(connection, input);
                case GIVE_UP -> handleGiveUp(connection, input);
                case MOVE -> handleReceiveMove(connection, input);
                case GAME_WON -> handleGameWon(connection, input);
                case GAME_LOST -> handleGameLost(connection, input);
                case YOUR_MOVE -> handleYourMove(connection, input);
                case ERROR -> handleError(connection, input);
                case JOINED_QUEUE -> handleJoinedQueue(connection, input);
                case LEFT_QUEUE -> handleLeftQueue(connection, input);
                case ASSIGN_COLOR -> handleColorAssign(connection, input);
                case BYE -> {
                    handleDisconnect(connection, input);
                }
            }
        } catch (Exception e) {
            connection.write(PacketAction.ERROR);
        }
    }

    private void handleQueue(@NotNull Connection connection, @NotNull Scanner input) {
        new QueueConnectionEvent().setConnection(connection).emit();
    }

    private void handleGiveUp(@NotNull Connection connection, @NotNull Scanner input) {
        new GiveUpConnectionEvent().setConnection(connection).emit();
    }


    private void handleGameWon(@NotNull Connection connection, @NotNull Scanner input) {
        new GameWonConnectionEvent().setConnection(connection).emit();
    }

    private void handleGameLost(@NotNull Connection connection, @NotNull Scanner input) {
        new GameLostConnectionEvent().setConnection(connection).emit();
    }

    private void handleReceiveMove(@NotNull Connection connection, @NotNull Scanner input) {
        Integer fromIndex = getNextInt(input);
        Integer toIndex = getNextInt(input);

        if (fromIndex == null || toIndex == null) {
            logger.error("Invalid move packet: {}", Arrays.toString(input.tokens().toArray()));
            connection.write(PacketAction.ERROR);
            return;
        }

        new MoveConnectionEvent().setFromSquare(fromIndex).setToSquare(toIndex)
                .setConnection(connection).emit();
    }

    private void handleYourMove(@NotNull Connection connection, @NotNull Scanner input) {
        new YourMoveConnectionEvent().setConnection(connection).emit();
    }

    private void handleError(@NotNull Connection connection, @NotNull Scanner input) {
        new ErrorConnectionEvent().setConnection(connection).emit();
    }

    private void handleJoinedQueue(@NotNull Connection connection, @NotNull Scanner input) {
        new JoinedQueueConnectionEvent().setConnection(connection).emit();
    }

    private void handleLeftQueue(@NotNull Connection connection, @NotNull Scanner input) {
        new LeftQueueConnectionEvent().setConnection(connection).emit();
    }

    private void handleDisconnect(@NotNull Connection connection, @NotNull Scanner input) {
        new DisconnectClientConnectionEvent().setConnection(connection).emit();
    }

    private void handleColorAssign(@NotNull Connection connection, @NotNull Scanner input) {
        String colorString = getNext(input);
        if (colorString == null) {
            logger.error("Received wrong color assign, getNext returned null");
            return;
        }

        Checker color = Checker.valueOf(colorString.toUpperCase());

        new AssignColorConnectionEvent().setColor(color).setConnection(connection).emit();
    }
}
