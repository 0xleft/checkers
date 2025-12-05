package uk.wwws.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Scanner;

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
