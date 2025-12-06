package uk.wwws.checkers.apps.entrypoints;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.ErrorType;
import uk.wwws.checkers.ai.DummyAIPlayer;
import uk.wwws.checkers.apps.ClientLikeApp;
import uk.wwws.checkers.game.moves.CheckersMove;
import uk.wwws.checkers.net.Connection;
import uk.wwws.checkers.net.ConnectionSender;
import uk.wwws.checkers.net.threads.ConnectionDataHandler;

public class AIApp extends ClientLikeApp implements ConnectionSender, ConnectionDataHandler {
    private static final Logger logger = LogManager.getRootLogger();

    private static AIApp instance;

    public static AIApp getInstance() {
        if (instance == null) {
            instance = new AIApp();
        }

        return instance;
    }

    public AIApp() {
        super(new DummyAIPlayer());
    }

    static void main() {
        AIApp.getInstance().run();
    }

    private void sendBestMove() {
        CheckersMove bestMove = (CheckersMove) ((DummyAIPlayer) player).getBestMove(game);
        if (bestMove == null) {
            logger.error("No best move exists");
            return;
        }

        sendMove(bestMove);
    }

    @Override
    public void handleReceiveMove(@NotNull Scanner input) {
        super.handleReceiveMove(input);
        sendBestMove();
    }

    @Override
    public ErrorType handleData(@Nullable String data, @NotNull Connection c) {
        ErrorType error = super.handleData(data, c);

        if (error != ErrorType.NONE) {
            return error;
        }

        if (game.getTurn() == player) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
            sendBestMove();
        }

        return ErrorType.NONE;
    }
}
