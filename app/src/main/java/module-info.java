module uk.wwws.checkers.app {
    requires org.apache.logging.log4j;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires org.jetbrains.annotations;
    requires jdk.jshell;

    opens uk.wwws.checkers.app;
    exports uk.wwws.checkers.app;
    exports uk.wwws.checkers.app.apps;
    exports uk.wwws.checkers.app.net;
    exports uk.wwws.checkers.app.apps.entrypoints;
    exports uk.wwws.checkers.app.apps.entrypoints.launchers;
    exports uk.wwws.checkers.app.ui;
    exports uk.wwws.checkers.app.ui.controllers;
    exports uk.wwws.checkers.app.ui.scenes;
    exports uk.wwws.checkers.app.net.threads;
    exports uk.wwws.checkers.app.game;
    exports uk.wwws.checkers.app.game.players;
}