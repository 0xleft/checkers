module uk.wwws.checkers.app {
    requires org.apache.logging.log4j;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires org.jetbrains.annotations;
    requires jdk.jshell;
    requires org.aspectj.runtime;

    opens uk.wwws.checkers;
    exports uk.wwws.checkers;
    exports uk.wwws.checkers.apps;
    exports uk.wwws.checkers.net;
    exports uk.wwws.checkers.apps.entrypoints;
    exports uk.wwws.checkers.apps.entrypoints.launchers;
    exports uk.wwws.checkers.ui;
    exports uk.wwws.checkers.ui.controllers;
    exports uk.wwws.checkers.ui.scenes;
    exports uk.wwws.checkers.net.threads;
    exports uk.wwws.checkers.game;
    exports uk.wwws.checkers.game.players;
    exports uk.wwws.checkers.events.ui;
    exports uk.wwws.checkers.events.commands;
    exports uk.wwws.checkers.events.net;
    exports uk.wwws.checkers.utils;
}