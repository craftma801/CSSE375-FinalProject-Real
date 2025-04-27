package main.actions;

import main.BoardStatusController;

public class TakeKnowledgeAction implements ActionHandler {
    private final BoardStatusController controller;

    public TakeKnowledgeAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleTakeKnowledge();
    }
}
