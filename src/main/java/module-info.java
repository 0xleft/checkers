module uk.wwws.checkers {
    requires org.apache.logging.log4j;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.jetbrains.annotations;

    opens uk.wwws.checkers;
    exports uk.wwws.checkers;
    exports uk.wwws.checkers.apps.entrypoints;
    exports uk.wwws.checkers.ui;
    exports uk.wwws.checkers.ui.scenes;
}