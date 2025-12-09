package uk.wwws.checkers.app.apps.entrypoints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.wwws.checkers.app.ai.DummyAIPlayer;
import uk.wwws.checkers.app.apps.ClientLikeApp;
import uk.wwws.checkers.app.game.moves.CheckersMove;
import uk.wwws.checkers.app.net.ConnectionSender;
import uk.wwws.checkers.app.net.threads.ConnectionDataHandler;
import uk.wwws.checkers.app.ui.TUI;

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

    @Override
    protected void handleYourMove() {
        super.handleYourMove();
        if (game.getTurn() == player) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            sendBestMove();
        }
    }

    @Override
    public void run() {
        this.ui = new TUI();
        ui.setApp(this);
        ui.run();
    }
}
