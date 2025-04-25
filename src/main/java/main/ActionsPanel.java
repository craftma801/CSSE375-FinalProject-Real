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
                "Discard the City card that matches the city you are in to place a research",
                "station there. Take the research station from the pile next to the board. If all",
                "6 research stations have been built, take a research station from anywhere",
                "on the board."));
        this.treatDiseaseButton = new ActionButton(bundle.getString("treatDisease"));
        this.treatDiseaseButton.setToolTipText(this.formatToolTip(
                "Remove 1 disease cube from the city you are in, placing it in the cube",
                "supply next to the board. If this disease color has been cured (see",
                "Discover a Cure below), remove all cubes of that color from the city you",
                "are in.",
                "If the last cube of a cured disease is removed from the board, this disease",
                "is eradicated."));
        this.takeKnowledgeButton = new ActionButton(bundle.getString("knowledge.take"));
        this.takeKnowledgeButton.setToolTipText(this.formatToolTip(
                "Take the City card that matches the city you are in to another player.",
                "The other player must also be in the city with you. Both of you need to",
                "agree to do this.",
                "If the player who gets the card now has more than 7 cards, that player must",
                "immediately discard a card or play an Event card."));
        this.giveKnowledgeButton = new ActionButton(bundle.getString("knowledge.give"));
        this.giveKnowledgeButton.setToolTipText(this.formatToolTip(
                "Give the City card that matches the city you are in to another player.",
                "The other player must also be in the city with you. Both of you need to",
                "agree to do this.",
                "If the player who gets the card now has more than 7 cards, that player must",
                "immediately discard a card or play an Event card."));
        this.discoverCureButton = new ActionButton(bundle.getString("discoverCure"));
        this.discoverCureButton.setToolTipText(this.formatToolTip(
                "At any research station, discard 5 City cards of the same color from your",
                "hand to cure the disease of that color. Move the disease’s cure marker to its",
                "Cure Indicator.",
                "If no cubes of this color are on the board, this disease is now eradicated."));

        this.driveFerryButton = new ActionButton(bundle.getString("driveOrFerry"));
        this.driveFerryButton.setToolTipText(this.formatToolTip(
                "Move to a city connected by a white line to the one you are in."));
        this.directFlightButton = new ActionButton(bundle.getString("flight.direct"));
        this.directFlightButton.setToolTipText(this.formatToolTip(
                "Discard a City card to move to the city named on the card."));
        this.charterFlightButton = new ActionButton(bundle.getString("flight.charter"));
        this.charterFlightButton.setToolTipText(this.formatToolTip(
                "Discard the City card that matches the city you are in to move to any city."));
        this.shuttleFlightButton = new ActionButton(bundle.getString("flight.shuttle"));
        this.shuttleFlightButton.setToolTipText(this.formatToolTip(
                "Move from a city with a research station to any other city that has a",
                "research station."));

        this.viewCardsButton = new ActionButton(bundle.getString("viewCards"));
        this.viewCardsButton.setToolTipText(this.formatToolTip(
                "Look at the cards in every player's hand."));
        this.playEventCardButton = new ActionButton(bundle.getString("playAnEventCard"));
        this.playEventCardButton.setToolTipText(this.formatToolTip(
                "During a turn, any player may play Event cards. Playing",
                "an Event card is not an action. The player who plays an",
                "Event card decides how it is used.",
                "Event cards can be played at any time, except in between",
                "drawing and resolving a card."));
        this.performRoleActionButton = new ActionButton(bundle.getString("performRoleAction"));
        this.performRoleActionButton.setToolTipText(this.formatToolTip(
                "Perform a role-specific action."));
        this.skipActionButton = new ActionButton(bundle.getString("skipAction"));
        this.skipActionButton.setToolTipText(this.formatToolTip(
                "Skip one of your actions."));

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
