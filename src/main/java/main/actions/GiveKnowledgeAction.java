package main.actions;

import main.BoardStatusController;

public class GiveKnowledgeAction implements ActionHandler {
    private final BoardStatusController controller;

    public GiveKnowledgeAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleGiveKnowledge();
    }
}
