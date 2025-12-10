package uk.wwws.checkers.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.apps.App;

public interface UI {
    void run();

    void setApp(@NotNull App app);

    @Nullable App getApp();
}
