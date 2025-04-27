package main.actions;

import main.BoardStatusController;

public class TreatDiseaseAction implements ActionHandler {
    private final BoardStatusController controller;

    public TreatDiseaseAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleTreatDisease();
    }
}
