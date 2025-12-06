package uk.wwws.checkers.ui.controllers;

import uk.wwws.checkers.ui.GUI;
import uk.wwws.checkers.ui.UI;

public class ReferencedController {
    protected GUI gui;

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    public ReferencedController(GUI gui) {
        this.gui = gui;
    }

    public ReferencedController() {
    }
}
