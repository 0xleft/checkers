package uk.wwws.ui;

import java.util.NoSuchElementException;
import java.util.Scanner;
import org.jetbrains.annotations.Nullable;

abstract public class TUI implements UI {
    private final Scanner scanner;

    protected TUI() {
        scanner = new Scanner(System.in);
    }

    public void run() {
        boolean stop = false;
        while (!stop) {
            CommandAction nextAction = getNextCommandAction(scanner);
            if (nextAction == CommandAction.QUIT) {
                stop = true;
                continue;
            }
            handleAction(nextAction, scanner);
        }
    }
}
