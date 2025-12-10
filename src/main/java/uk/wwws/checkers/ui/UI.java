package uk.wwws.checkers.ui;

import java.util.Scanner;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.wwws.checkers.apps.App;

public interface UI {
    void run();

    void setApp(@NotNull App app);

    @Nullable App getApp();
}
