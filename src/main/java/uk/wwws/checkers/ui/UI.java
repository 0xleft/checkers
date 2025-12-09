package uk.wwws.checkers.ui;

import java.util.Scanner;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.apps.App;

public interface UI {
    default void handleAction(@NotNull UIAction action, @Nullable Scanner data,
                              boolean deferIfPossible) {
        if (deferIfPossible) {
            try {
                Platform.runLater(() -> {
                    handleAction(action, data);
                });
                return;
            } catch (
                    IllegalStateException _) {/* this means we are using TUI, or something that cannot handle Platform.later, but we want to reuse the same methods */}
        }

        handleAction(action, data);
    }

    void handleAction(@NotNull UIAction action, @Nullable Scanner data);

    void run();

    void setApp(@NotNull App app);

    @Nullable App getApp();
}
