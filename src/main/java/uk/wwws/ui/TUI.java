package uk.wwws.ui;

import java.util.NoSuchElementException;
import java.util.Scanner;
import org.jetbrains.annotations.Nullable;

public class TUI extends UI {
    Scanner scanner;

    protected TUI() {
        scanner = new Scanner(System.in);
    }

    protected @Nullable CommandAction getNextAction() {
        try {
            return CommandAction.valueOf(scanner.next().toUpperCase());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return null;
        }
    }

    protected @Nullable String getNextStringArg() {
        try {
            return scanner.next();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return null;
        }
    }

    protected @Nullable Integer getNextIntArg() {
        try {
            return scanner.nextInt();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return null;
        }
    }
}
