package main.actions;

import main.BoardStatusController;

public class DiscoverCureAction implements ActionHandler {
    private final BoardStatusController controller;

    public DiscoverCureAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleDiscoverCure(controller.getCurrentPlayer());
    }
}
