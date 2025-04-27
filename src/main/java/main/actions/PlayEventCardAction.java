package main.actions;

import main.BoardStatusController;

public class PlayEventCardAction implements ActionHandler {
    private final BoardStatusController controller;

    public PlayEventCardAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.getEventCardManager().handlePlayEventCard(controller.getCurrentPlayer());
    }
}
