package main.actions;

import main.BoardStatusController;

public class ViewCardsAction implements ActionHandler {
    private final BoardStatusController controller;

    public ViewCardsAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleViewCards(controller.getCurrentPlayer());
    }
}
