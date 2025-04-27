package main.actions;

import main.BoardStatusController;

public class ShuttleFlightAction implements ActionHandler {
    private final BoardStatusController controller;

    public ShuttleFlightAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleShuttleFlight();
    }
}
