package uk.wwws.checkers.apps.entrypoints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.ai.DummyAIPlayer;
import uk.wwws.checkers.apps.ClientLikeApp;
import uk.wwws.checkers.eventframework.Listener;
import uk.wwws.checkers.eventframework.annotations.EventHandler;
import uk.wwws.checkers.eventframework.annotations.Priority;
import uk.wwws.checkers.events.net.YourMoveConnectionEvent;
import uk.wwws.checkers.game.moves.CheckersMove;
import uk.wwws.checkers.ui.TUI;

public class AIApp extends ClientLikeApp implements Listener {
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

    public static void main(String[] args) {
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

    @EventHandler(priority = Priority.LOWEST)
    public void handleAIToMove(YourMoveConnectionEvent event) {
        sendBestMove();
    }

    @Override
    public void run() {
        this.ui = new TUI();
        ui.setApp(this);
        ui.run();
    }
}
