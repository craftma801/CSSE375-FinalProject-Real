package main.actions;

import main.BoardStatusController;

public class SkipActionAction implements ActionHandler {
    private final BoardStatusController controller;

    public SkipActionAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.actionAftermath(true);
    }
}
