package main.actions;

import main.BoardStatusController;

public class DriveFerryAction implements ActionHandler {
    private final BoardStatusController controller;

    public DriveFerryAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleDriveFerry();
    }
}
