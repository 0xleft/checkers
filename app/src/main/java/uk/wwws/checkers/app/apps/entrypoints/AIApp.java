package checkers.apps.entrypoints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import checkers.ai.DummyAIPlayer;
import checkers.apps.ClientLikeApp;
import checkers.game.moves.CheckersMove;
import checkers.net.ConnectionSender;
import checkers.net.threads.ConnectionDataHandler;
import checkers.ui.TUI;

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
