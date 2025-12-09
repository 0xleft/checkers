package checkers.ui.controllers;

import checkers.ui.GUI;

public class ReferencedController {
    protected GUI gui;

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    @SuppressWarnings("unused") // used for reflections
    public ReferencedController(GUI gui) {
        this.gui = gui;
    }

    public ReferencedController() {
    }
}
