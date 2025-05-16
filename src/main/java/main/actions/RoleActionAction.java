package main.actions;

import main.BoardStatusController;

public class RoleActionAction implements ActionHandler {
    private final BoardStatusController controller;

    public RoleActionAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleRoleAction(controller.getCurrentPlayer());
    }
}
