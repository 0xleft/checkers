package uk.wwws.checkers.ui;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.wwws.checkers.events.commands.*;
import uk.wwws.checkers.utils.DataParser;

public class CommandParser implements DataParser {
    private static final Logger logger = LogManager.getRootLogger();

    private static CommandParser instance;

    public static CommandParser getInstance() {
        if (instance == null) {
            instance = new CommandParser();
        }
        return instance;
    }

    public void parseAction(@NotNull String data) {
        Scanner input = new Scanner(data);
        logger.trace("New command data received: {}", data);

        try {
            switch (CommandAction.valueOf(input.next().toUpperCase())) {
                case CONNECT -> handleConnect(input);
                case DISCONNECT -> handleDisconnect(input);
                case STATE -> handleState(input);
                case MOVE -> handleMove(input);
                case QUEUE -> handleQueue(input);
                case GIVE_UP -> handleGiveUp(input);
                case HELP -> handleHelpMenu(input);
                case START_SERVER -> handleStartServer(input);
                case STOP_SERVER -> handleStopServer(input);
                case QUIT -> handleQuit(input);
                default -> logger.error(
                        "Invalid command or wrong argument usage. Type help to get command list");
            }
        } catch (Exception e) {
            logger.error("Error occurred while parsing action: {}", e.getMessage());
        }
    }

    private void handleQueue(@NotNull Scanner input) {
        new QueueCommandEvent().emit();
    }

    private void handleStopServer(@NotNull Scanner input) {
        new StopServerCommandEvent().emit();
    }

    private void handleQuit(@NotNull Scanner input) {
        new QuitCommandEvent().emit();
    }

    private void handleStartServer(@NotNull Scanner input) {
        Integer port = getNextInt(input);

        if (port == null) {
            logger.error("That was not a valid port. Usage: start_server <port:int>");
            return;
        }

        new StartServerCommandEvent().setPort(port).emit();
    }

    private void handleHelpMenu(@NotNull Scanner input) {
        new HelpCommandEvent().emit();
    }

    private void handleGiveUp(@NotNull Scanner input) {
        new GiveUpCommandEvent().emit();
    }

    private void handleMove(@NotNull Scanner input) {
        Integer fromIndex = getNextInt(input);
        Integer toIndex = getNextInt(input);

        if (fromIndex == null || toIndex == null) {
            logger.error("Invalid move: {} to {}", fromIndex, toIndex);
            return;
        }

        new MoveCommandEvent().setFromSquare(fromIndex).setToSquare(toIndex).emit();
    }

    private void handleState(@NotNull Scanner input) {
        new StateCommandEvent().emit();
    }

    private void handleDisconnect(@NotNull Scanner input) {
        new DisconnectCommandEvent().emit();
    }

    private void handleConnect(@NotNull Scanner input) {
        String hostname = getNext(input);
        String port = getNext(input);

        if (hostname == null || port == null) {
            logger.error("Wrong usage. Use: connect <host:string> <port:int>. Got: connect {} {}",
                         hostname, port);
            return;
        }

        new ConnectCommandEvent().setHost(hostname).setPort(port).emit();
    }
}
