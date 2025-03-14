package main;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ActionsPanel extends JPanel {

    private final JLabel actionsHeader;
    private final JButton buildResearchStationButton;
    private final JButton treatDiseaseButton;
    private final JButton giveKnowledgeButton;
    private final JButton takeKnowledgeButton;
    private final JButton discoverCureButton;
    private final JButton driveFerryButton;
    private final JButton directFlightButton;
    private final JButton charterFlightButton;
    private final JButton shuttleFlightButton;
    private final JLabel playerActionsHeader;
    private final JButton viewCardsButton;
    private final JButton playEventCardButton;
    private final JButton performRoleActionButton;
    private final JButton skipActionButton;
    private final ResourceBundle bundle;

    public ActionsPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.bundle = Pandemic.bundle;

        this.actionsHeader = new JLabel(bundle.getString("actionsUnknownAmountRemaining"));
        this.buildResearchStationButton = new JButton(bundle.getString("buildResearchStation"));
        this.treatDiseaseButton = new JButton(bundle.getString("treatDisease"));
        this.takeKnowledgeButton = new JButton(bundle.getString("knowledge.take"));
        this.giveKnowledgeButton = new JButton(bundle.getString("knowledge.give"));
        this.discoverCureButton = new JButton(bundle.getString("discoverCure"));
        this.driveFerryButton = new JButton(bundle.getString("driveOrFerry"));
        this.directFlightButton = new JButton(bundle.getString("flight.direct"));
        this.charterFlightButton = new JButton(bundle.getString("flight.charter"));
        this.shuttleFlightButton = new JButton(bundle.getString("flight.shuttle"));

        this.playerActionsHeader = new JLabel(bundle.getString("player.actions"));
        this.viewCardsButton = new JButton(bundle.getString("viewCards"));
        this.playEventCardButton = new JButton(bundle.getString("playAnEventCard"));
        this.performRoleActionButton = new JButton(bundle.getString("performRoleAction"));
        this.skipActionButton = new JButton(bundle.getString("skipAction"));

        setUpActionListeners();
        addAllElementsToThisPanel();
    }

    private void setUpActionListeners() {
        this.buildResearchStationButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.BUILD_RESEARCH_STATION));
        this.treatDiseaseButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.TREAT_DISEASE));
        this.giveKnowledgeButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.GIVE_KNOWLEDGE));
        this.takeKnowledgeButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.TAKE_KNOWLEDGE));
        this.discoverCureButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.DISCOVER_CURE));
        this.driveFerryButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.DRIVE_FERRY));
        this.directFlightButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.DIRECT_FLIGHT));
        this.charterFlightButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.CHARTER_FLIGHT));
        this.shuttleFlightButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.SHUTTLE_FLIGHT));

        this.viewCardsButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.VIEW_CARDS));
        this.playEventCardButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.PLAY_EVENT_CARD));
        this.performRoleActionButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.ROLE_ACTION));
        this.skipActionButton.addActionListener(e -> Pandemic.handleAction(PlayerAction.SKIP_ACTION));
    }

    private void addAllElementsToThisPanel() {
        this.add(actionsHeader);
        this.add(buildResearchStationButton);
        this.add(treatDiseaseButton);
        this.add(giveKnowledgeButton);
        this.add(takeKnowledgeButton);
        this.add(discoverCureButton);
        this.add(driveFerryButton);
        this.add(directFlightButton);
        this.add(charterFlightButton);
        this.add(shuttleFlightButton);

        this.add(playerActionsHeader);
        this.add(viewCardsButton);
        this.add(playEventCardButton);
        this.add(performRoleActionButton);
        this.add(skipActionButton);
    }

    public void setRemainingActions(int remainingActions){
        this.actionsHeader.setText(MessageFormat.format(bundle.getString("actions.0.remaining"), remainingActions));
    }
}
