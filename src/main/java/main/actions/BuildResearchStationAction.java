package main.actions;

import main.BoardStatusController;

public class BuildResearchStationAction implements ActionHandler {
    private final BoardStatusController controller;

    public BuildResearchStationAction(BoardStatusController controller) {
        this.controller = controller;
    }

    @Override
    public void handle() {
        controller.handleBuildResearchStation();
    }
}
