package main.actions;

import main.BoardStatusController;

public class CharterFlightAction implements ActionHandler {
    private final BoardStatusController controller;

    public CharterFlightAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleCharterFlight();
    }
}
