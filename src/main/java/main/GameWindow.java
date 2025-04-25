package main;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GameWindow implements GameWindowInterface {
    public final JFrame windowFrame;
    private final JPanel gamePanel;
    public GameBoard gameBoard;
    public ActionsPanel actionsPanel;
    private final StatusIndicator currentPlayerIndicator;
    private final StatusIndicator infectionRateIndicator;
    private final StatusIndicator outbreaksIndicator;
    private final StatusIndicator redTreatmentIndicator;
    private final StatusIndicator blackTreatmentIndicator;
    private final StatusIndicator blueTreatmentIndicator;
    private final StatusIndicator yellowTreatmentIndicator;
    private JDialog currentInfectionCardsDialog;
    private final ResourceBundle bundle;

    public GameWindow(ArrayList<City> cities) {
        this.bundle = Pandemic.bundle;
        this.windowFrame = new JFrame();
        this.windowFrame.setResizable(true);
        this.windowFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.windowFrame.setTitle("Pandemic");

        this.gamePanel = new JPanel();
        this.gamePanel.setLayout(new BorderLayout());
        this.windowFrame.add(this.gamePanel);

        this.gameBoard = new GameBoard(cities);
        this.gamePanel.add(gameBoard, BorderLayout.CENTER);
        this.gameBoard.setPreferredSize(Pandemic.BOARD_SIZE);

        this.actionsPanel = new ActionsPanel();
        this.gamePanel.add(this.actionsPanel, BorderLayout.EAST);

        this.currentPlayerIndicator = new StatusIndicator(bundle.getString("currentPlayer"), bundle.getString("mr.nobody"));
        this.infectionRateIndicator = new StatusIndicator(bundle.getString("infectionRate"), "2");
        this.outbreaksIndicator = new StatusIndicator(bundle.getString("outbreaks"), "0");
        this.yellowTreatmentIndicator = new StatusIndicator(bundle.getString("yellow"), bundle.getString("untreated"));
        this.redTreatmentIndicator = new StatusIndicator(bundle.getString("red"), bundle.getString("untreated"));
        this.blueTreatmentIndicator = new StatusIndicator(bundle.getString("blue"), bundle.getString("untreated"));
        this.blackTreatmentIndicator = new StatusIndicator(bundle.getString("black"), bundle.getString("untreated"));

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.LINE_AXIS));
        statusPanel.add(this.currentPlayerIndicator);
        statusPanel.add(this.infectionRateIndicator);
        statusPanel.add(this.outbreaksIndicator);
        statusPanel.add(this.yellowTreatmentIndicator);
        statusPanel.add(this.redTreatmentIndicator);
        statusPanel.add(this.blueTreatmentIndicator);
        statusPanel.add(this.blackTreatmentIndicator);
        this.gamePanel.add(statusPanel, BorderLayout.SOUTH);

        this.currentInfectionCardsDialog = null;
    }

    public void showWindow() {
        this.windowFrame.pack();
        this.windowFrame.setVisible(true);
    }

    public void displayNextPlayerInfo(String nextPlayerName, int remainingActions) {
        this.actionsPanel.setRemainingActions(remainingActions);
        currentPlayerIndicator.updateValue(nextPlayerName);
    }

    public String promptSelectOption(PromptWindowInputs inputs) {
        JFrame promptFrame = new JFrame();
        return (String) JOptionPane.showInputDialog(
                promptFrame,
                inputs.message,
                inputs.title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                inputs.selectionValues,
                null);
    }

    public CompletableFuture<City> selectCity(HashSet<City> possibleLocations) {
        return gameBoard.selectCity(possibleLocations);
    }

    public void updateTreatmentIndicator(CityColor color, String message) {
        StatusIndicator toUpdate = switch (color) {
            case YELLOW -> yellowTreatmentIndicator;
            case RED -> redTreatmentIndicator;
            case BLUE -> blueTreatmentIndicator;
            case BLACK -> blackTreatmentIndicator;
            case EVENT_COLOR -> null;
        };
        if (toUpdate != null) {
            toUpdate.updateValue(message);
        }
    }

    public void updateInfectionRate(int infectionRate) {
        infectionRateIndicator.updateValue("" + infectionRate);
    }

    public void updateOutbreaks(int outbreaks) {
        outbreaksIndicator.updateValue("" + outbreaks);
    }

    public void updateRemainingActions(int remainingActions) {
        this.actionsPanel.setRemainingActions(remainingActions);
    }

    public String promptCureCards(String[] possibleValues) {
        JFrame promptFrame = new JFrame();
        return (String) JOptionPane.showInputDialog(
                promptFrame, bundle.getString("discardACityCardOfTheColorYouWantToCure"),
                bundle.getString("cureADisease"),
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibleValues,
                null);
    }

    public void showGameOverMessage(String message) {
        JOptionPane.showMessageDialog(this.actionsPanel, message);
    }

    public void displayMessage(String title, String message, int type) {
        JOptionPane.showMessageDialog(this.gamePanel, message, title, type);
    }

    public Player promptSelectPlayer(PromptWindowInputs inputs) {
        JFrame promptFrame = new JFrame();

        return (Player) JOptionPane.showInputDialog(
                promptFrame,
                inputs.message,
                inputs.title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                inputs.selectionValues,
                null);
    }

    public InfectionCard promptInfectionCard(PromptWindowInputs inputs) {
        JFrame promptFrame = new JFrame();

        return (InfectionCard) JOptionPane.showInputDialog(
                promptFrame,
                inputs.message,
                inputs.title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                inputs.selectionValues,
                null);
    }

    public PlayerCard promptSelectPlayerCard(PromptWindowInputs inputs) {
        JFrame promptFrame = new JFrame();

        return (PlayerCard) JOptionPane.showInputDialog(
                promptFrame,
                inputs.message,
                inputs.title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                inputs.selectionValues,
                null);
    }

    public CityColor promptColorToCure(CityColor[] colors) {
        JFrame promptFrame = new JFrame();

        return (CityColor) JOptionPane.showInputDialog(promptFrame, bundle.getString("selectColorToTreat"),
                bundle.getString("treatDisease"), JOptionPane.PLAIN_MESSAGE, null, colors, null);
    }

    public void displayInfectionCards(InfectionCard[] cardsToDisplay, String title) {
        JPanel viewCardsPanel = new JPanel();
        JDialog dialog = new JDialog(this.windowFrame);
        dialog.add(viewCardsPanel);
        JLabel textLabel = new JLabel();
        StringBuilder cardNames = new StringBuilder();

        cardNames.append("<html>");
        for (InfectionCard card : cardsToDisplay) {
            cardNames.append("<br>");
            cardNames.append(card.city.name);
        }
        cardNames.append("</html>");
        textLabel.setText(cardNames.toString());
        viewCardsPanel.add(textLabel);
        dialog.setTitle(title);

        dialog.setPreferredSize(new Dimension(Pandemic.BOARD_WIDTH / 7, Pandemic.BOARD_HEIGHT / 5));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - dialog.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - dialog.getHeight()) / 5);
        dialog.setLocation(x, y);
        dialog.pack();
        this.currentInfectionCardsDialog = dialog;
        dialog.setVisible(true);
    }

    public void destroyCurrentInfectionCardsDialog() {
        if (this.currentInfectionCardsDialog != null) {
            this.currentInfectionCardsDialog.dispose();
            this.currentInfectionCardsDialog = null;
        }
    }

    @Override
    public void repaintGameBoard() {
        this.gameBoard.repaint();
    }

    @Override
    public JFrame getWindowFrame() {
        return this.windowFrame;
    }

    public void displayPlayerCards(Player[] players, Player currentPlayer) {
        JPanel viewCardsPanel = new JPanel();
        GridLayout viewCardsLayout = new GridLayout(2, 2);
        viewCardsPanel.setLayout(viewCardsLayout);
        JDialog dialog = new JDialog(getWindowFrame());
        dialog.add(viewCardsPanel);
        for (Player player : players) {
            JLabel text = new JLabel();
            StringBuilder playerHand = new StringBuilder();
            playerHand.append("<html>");
            if (player.equals(currentPlayer)) {
                playerHand.append("<big>");
            }
            String s = MessageFormat.format(bundle.getString("playersCards"), player.name);
            playerHand.append(s);
            for (PlayerCard card : player.getCardsInHand()) {
                playerHand.append("<br>");
                playerHand.append(card.getName());
                if (!card.isEvent()) {
                    int spacesToAdd = 20 - card.getName().length();
                    playerHand.append("&nbsp;".repeat(Math.max(0, spacesToAdd)));
                    playerHand.append(card.color);
                }
            }
            if (player.getCardNames().isEmpty()) {
                playerHand.append(bundle.getString("noCardsInHand"));
            }
            if (player.equals(currentPlayer)) {
                playerHand.append("</big>");
            }
            playerHand.append("</html>");
            text.setText(playerHand.toString());
            viewCardsPanel.add(text);
        }
        dialog.setPreferredSize(new Dimension(Pandemic.BOARD_WIDTH / 2, Pandemic.BOARD_HEIGHT / 2));
        dialog.pack();
        dialog.setVisible(true);
    }

    public static Locale selectLocale(String message){
        JFrame promptFrame = new JFrame();
        ArrayList<Locale> locales = new ArrayList<>();
        Scanner scanner;
        try {
            scanner = new Scanner(new File("translations/supportedLocales.csv"));
        } catch (FileNotFoundException e) {
            return new Locale("en", "US");
        }
        scanner.useDelimiter(",");
        while (scanner.hasNext()) {
            String localeString = scanner.next();
            String language = localeString.substring(0, localeString.indexOf('-'));
            String country = localeString.substring(localeString.indexOf('-') + 1);
            Locale locale = new Locale(language, country);
            locales.add(locale);
        }
        scanner.close();

        Locale localeToReturn = (Locale) JOptionPane.showInputDialog(
                promptFrame,
                message,
                null,
                JOptionPane.PLAIN_MESSAGE,
                null,
                locales.toArray(),
                null);
        if (localeToReturn != null) {
            return localeToReturn;
        }
        return new Locale("en", "US");
    }

    public static String selectSetupOption(String[] options, String message, String title) {
        JFrame promptFrame = new JFrame();
        return (String) JOptionPane.showInputDialog(
                promptFrame,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                null);
    }

    public static int calculateNumEpidemicCards(int numPlayers) {
        String[] options = new String[]{"Introductory", "Standard", "Heroic"};
        String message = "Please select difficulty level";
        String title = "Select Difficulty Level";
        String difficulty = selectSetupOption(options, message, title);
        System.out.println(difficulty);
        return switch (difficulty) {
            case "Introductory" -> 4;
            case "Standard" -> 5;
            case "Heroic" -> 6;
            default -> throw new RuntimeException("Invalid Difficulty Level");
        };
    }
}
