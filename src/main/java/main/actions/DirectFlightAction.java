package main.actions;

import main.BoardStatusController;

public class DirectFlightAction implements ActionHandler {
    private final BoardStatusController controller;

    public DirectFlightAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleDirectFlight();
    }
}
