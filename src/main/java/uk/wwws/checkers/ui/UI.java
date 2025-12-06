package uk.wwws.checkers.ui;

import java.util.Scanner;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.ErrorType;
import uk.wwws.checkers.apps.App;

public interface UI {
    default void handleAction(@NotNull UIAction action, @Nullable Scanner data, boolean defer) {
        if (defer) {
            try {
                Platform.runLater(() -> {
                    handleAction(action, data);
                });
            } catch (IllegalStateException e) {
                // means we are using tui
                handleAction(action, data);
            }
        } else {
            handleAction(action, data);
        }
    }
    void handleAction(@NotNull UIAction action, @Nullable Scanner data);
    void run();

    void setApp(@NotNull App app);
    @Nullable App getApp();
}
