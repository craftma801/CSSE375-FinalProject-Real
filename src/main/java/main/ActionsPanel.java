package main;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.tools.Tool;
import java.awt.*;
import java.awt.event.MouseEvent;
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
        this.setLayout(new GridLayout(15, 1, 20, 10));
        this.bundle = Pandemic.bundle;

        Font labelFont = new Font("ActionPanelHeader", Font.ITALIC, 16);
        this.actionsHeader = new JLabel(bundle.getString("actionsUnknownAmountRemaining"), SwingConstants.CENTER);
        this.actionsHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.actionsHeader.setForeground(GameWindow.TEXT_COLOR);
        this.actionsHeader.setFont(labelFont);
        this.playerActionsHeader = new JLabel(bundle.getString("player.actions"), SwingConstants.CENTER);
        this.playerActionsHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.playerActionsHeader.setFont(labelFont);
        this.playerActionsHeader.setForeground(GameWindow.TEXT_COLOR);


        this.buildResearchStationButton = new ActionButton(bundle.getString("buildResearchStation"));
        this.buildResearchStationButton.setToolTipText(this.formatToolTip(
                bundle.getString("buildResearchStationTTL1"),
                bundle.getString("buildResearchStationTTL2"),
                bundle.getString("buildResearchStationTTL3"),
                bundle.getString("buildResearchStationTTL4")));
        this.treatDiseaseButton = new ActionButton(bundle.getString("treatDisease"));
        this.treatDiseaseButton.setToolTipText(this.formatToolTip(
                bundle.getString("treatDiseaseTTL1"),
                bundle.getString("treatDiseaseTTL2"),
                bundle.getString("treatDiseaseTTL3"),
                bundle.getString("treatDiseaseTTL4"),
                bundle.getString("treatDiseaseTTL5"),
                bundle.getString("treatDiseaseTTL6")));
        this.takeKnowledgeButton = new ActionButton(bundle.getString("knowledge.take"));
        this.takeKnowledgeButton.setToolTipText(this.formatToolTip(
                bundle.getString("takeKnowledgeTTL1"),
                bundle.getString("takeKnowledgeTTL2"),
                bundle.getString("takeKnowledgeTTL3"),
                bundle.getString("takeKnowledgeTTL4"),
                bundle.getString("takeKnowledgeTTL5")));
        this.giveKnowledgeButton = new ActionButton(bundle.getString("knowledge.give"));
        this.giveKnowledgeButton.setToolTipText(this.formatToolTip(
                bundle.getString("giveKnowledgeTTL1"),
                bundle.getString("giveKnowledgeTTL2"),
                bundle.getString("giveKnowledgeTTL3"),
                bundle.getString("giveKnowledgeTTL4"),
                bundle.getString("giveKnowledgeTTL5")));
        this.discoverCureButton = new ActionButton(bundle.getString("discoverCure"));
        this.discoverCureButton.setToolTipText(this.formatToolTip(
                bundle.getString("discoverCureTTL1"),
                bundle.getString("discoverCureTTL2"),
                bundle.getString("discoverCureTTL3"),
                bundle.getString("discoverCureTTL4")));

        this.driveFerryButton = new ActionButton(bundle.getString("driveOrFerry"));
        this.driveFerryButton.setToolTipText(this.formatToolTip(
                bundle.getString("driveFerryTTL1")));
        this.directFlightButton = new ActionButton(bundle.getString("flight.direct"));
        this.directFlightButton.setToolTipText(this.formatToolTip(
                bundle.getString("directFlightTTL1")));
        this.charterFlightButton = new ActionButton(bundle.getString("flight.charter"));
        this.charterFlightButton.setToolTipText(this.formatToolTip(
                bundle.getString("charterFlightTTL1")));
        this.shuttleFlightButton = new ActionButton(bundle.getString("flight.shuttle"));
        this.shuttleFlightButton.setToolTipText(this.formatToolTip(
                bundle.getString("shuttleFlightTTL1"),
                bundle.getString("shuttleFlightTTL2")));

        this.viewCardsButton = new ActionButton(bundle.getString("viewCards"));
        this.viewCardsButton.setToolTipText(this.formatToolTip(
                bundle.getString("viewCardsTTL1")));
        this.playEventCardButton = new ActionButton(bundle.getString("playAnEventCard"));
        this.playEventCardButton.setToolTipText(this.formatToolTip(
                bundle.getString("playEventCardTTL1"),
                bundle.getString("playEventCardTTL2"),
                bundle.getString("playEventCardTTL3"),
                bundle.getString("playEventCardTTL4"),
                bundle.getString("playEventCardTTL5")));
        this.performRoleActionButton = new ActionButton(bundle.getString("performRoleAction"));
        this.performRoleActionButton.setToolTipText(this.formatToolTip(
                bundle.getString("performRoleActionTTL1")));
        this.skipActionButton = new ActionButton(bundle.getString("skipAction"));
        this.skipActionButton.setToolTipText(this.formatToolTip(
                bundle.getString("skipActionTTL1")));

        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

        setUpActionListeners();
        addAllElementsToThisPanel();
    }

    private String formatToolTip(String... lines) {
        StringBuilder str = new StringBuilder();
        str.append("<html><div style='font-size:12px;padding-right:24px;'>");
        for (String line : lines) {
            str.append(line);
            str.append("<br>");
        }
        str.append("</div></html>");
        return str.toString();
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

    public void setRemainingActions(int remainingActions) {
        this.actionsHeader.setText(MessageFormat.format(bundle.getString("actions.0.remaining"), remainingActions));
    }
}
