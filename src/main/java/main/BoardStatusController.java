package main;

import main.roles.*;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BoardStatusController {
    public static final int NUM_PLAYERS = 4;

    public ArrayList<City> cityMap;

    public Stack<PlayerCard> playerDeck;
    public Stack<PlayerCard> playerDiscardPile;
    public Stack<InfectionCard> infectionDeck;
    public Stack<InfectionCard> infectionDiscardPile;

    public Player[] players;
    public int currentPlayerTurn;
    public int currentPlayerRemainingActions;

    private final int[] infectionRateValues;
    public int infectionRateIndex;
    public DiseaseCubeBank cubeBank;

    private DiseaseStatus yellowDiseaseStatus;
    private DiseaseStatus redDiseaseStatus;
    private DiseaseStatus blueDiseaseStatus;
    private DiseaseStatus blackDiseaseStatus;

    public GameWindowInterface gameWindow;
    public boolean isQuietNight;
    private boolean gameOver;

    public OutbreakManager outbreakManager;

    private final ResourceBundle bundle;

    public BoardStatusController(GameWindowInterface gameWindow, ArrayList<City> cityMap) {
        if (Pandemic.bundle != null) {
            this.bundle = Pandemic.bundle;
        } else {
            Locale locale = new Locale("en", "US");
            this.bundle = ResourceBundle.getBundle("messages", locale);
        }

        this.players = new Player[4];
        this.infectionRateValues = new int[]{2, 2, 2, 3, 3, 4, 4};
        this.infectionRateIndex = 0;

        this.playerDeck = new Stack<>();
        this.playerDiscardPile = new Stack<>();
        this.infectionDeck = new Stack<>();
        this.infectionDiscardPile = new Stack<>();
        this.cubeBank = new DiseaseCubeBank();
        this.currentPlayerTurn = NUM_PLAYERS;
        this.currentPlayerRemainingActions = 0;
        this.outbreakManager = new OutbreakManager(gameWindow);

        this.yellowDiseaseStatus = DiseaseStatus.ACTIVE;
        this.redDiseaseStatus = DiseaseStatus.ACTIVE;
        this.blueDiseaseStatus = DiseaseStatus.ACTIVE;
        this.blackDiseaseStatus = DiseaseStatus.ACTIVE;

        this.gameWindow = gameWindow;

        this.isQuietNight = false;
        this.gameOver = false;

        this.cityMap = cityMap;
    }

    public void handleAction(PlayerAction playerAction) {
        boolean actionWasPerformed;
        try {
            actionWasPerformed = switch (playerAction) {
                case BUILD_RESEARCH_STATION -> handleBuildResearchStation();
                case TREAT_DISEASE -> handleTreatDisease();
                case GIVE_KNOWLEDGE -> handleGiveKnowledge();
                case TAKE_KNOWLEDGE -> handleTakeKnowledge();
                case DISCOVER_CURE -> handleDiscoverCure(players[currentPlayerTurn]);
                case DRIVE_FERRY -> handleDriveFerry();
                case DIRECT_FLIGHT -> handleDirectFlight();
                case CHARTER_FLIGHT -> handleCharterFlight();
                case SHUTTLE_FLIGHT -> handleShuttleFlight();
                case VIEW_CARDS -> handleViewCards(players[currentPlayerTurn]);
                case PLAY_EVENT_CARD -> handlePlayEventCard();
                case ROLE_ACTION -> handleRoleAction(players[currentPlayerTurn]);
                case SKIP_ACTION -> true;
            };
        } catch (ActionFailedException e) {
            handleActionFailure(e);
            return;
        }
        if (actionWasPerformed) {
            currentPlayerRemainingActions--;
            gameWindow.updateRemainingActions(currentPlayerRemainingActions);
            if (cubeBank.remainingCubes(CityColor.YELLOW) == 24 && this.yellowDiseaseStatus.equals(DiseaseStatus.CURED)) {
                this.yellowDiseaseStatus = DiseaseStatus.ERADICATED;
            }
            if (cubeBank.remainingCubes(CityColor.BLUE) == 24 && this.blueDiseaseStatus.equals(DiseaseStatus.CURED)) {
                this.blueDiseaseStatus = DiseaseStatus.ERADICATED;
            }
            if (cubeBank.remainingCubes(CityColor.BLACK) == 24 && this.blackDiseaseStatus.equals(DiseaseStatus.CURED)) {
                this.blackDiseaseStatus = DiseaseStatus.ERADICATED;
            }
            if (cubeBank.remainingCubes(CityColor.RED) == 24 && this.redDiseaseStatus.equals(DiseaseStatus.CURED)) {
                this.redDiseaseStatus = DiseaseStatus.ERADICATED;
            }
            gameWindow.repaintGameBoard();
        }
        if (currentPlayerRemainingActions == 0) {
            drawTwoPlayerCards();
            if (!this.gameOver) {
                nextPlayerTurn();
            }
        }
    }

    private boolean handleBuildResearchStation() {
        players[currentPlayerTurn].buildResearchStation();
        return true;
    }

    public boolean handleDriveFerry() {
        CompletableFuture<City> userSelection = gameWindow.selectCity();
        userSelection.thenAccept((city) -> {
            players[currentPlayerTurn].move(city);
        });
        return true;
    }

    public boolean handleDirectFlight() {
        CompletableFuture<City> userSelection = gameWindow.selectCity();
        userSelection.thenAccept((city) -> {
            players[currentPlayerTurn].directFlight(city);
        });
        return true;
    }

    public boolean handleCharterFlight() {
        CompletableFuture<City> userSelection = gameWindow.selectCity();
        userSelection.thenAccept((city) -> {
            players[currentPlayerTurn].charterFlight(city);
        });
        return true;
    }

    public boolean handleShuttleFlight() {
        CompletableFuture<City> userSelection = gameWindow.selectCity();
        userSelection.thenAccept((city) -> {
            players[currentPlayerTurn].shuttleFlight(city);
        });
        return true;
    }

    public boolean handleViewCards(Player currentPlayer) {
        gameWindow.displayPlayerCards(players, currentPlayer);
        return false;
    }

    private boolean handleTreatDisease() {
        CityColor[] colors = new CityColor[]{CityColor.YELLOW,
                CityColor.RED,
                CityColor.BLUE,
                CityColor.BLACK};
        CityColor colorToTreat = gameWindow.promptColorToCure(colors);
        if (colorToTreat == null) {
            throw new ActionFailedException("Failed to treat disease!");
        }
        DiseaseStatus status = switch (colorToTreat) {
            case YELLOW -> yellowDiseaseStatus;
            case RED -> redDiseaseStatus;
            case BLUE -> blueDiseaseStatus;
            case BLACK -> blackDiseaseStatus;
            default -> throw new ActionFailedException("Invalid color to treat!");
        };
        if (status.equals(DiseaseStatus.CURED)) {
            players[currentPlayerTurn].getCity().medicTreatDisease(colorToTreat, cubeBank);
        } else {
            players[currentPlayerTurn].treatDisease(colorToTreat, cubeBank);
        }
        return true;
    }

    public boolean handleTakeKnowledge() {
        Player takingFrom = gameWindow.promptSelectPlayer(players, bundle.getString("knowledge.take"), bundle.getString("selectThePlayerYouWouldLikeToTakeFrom"));
        if (takingFrom == null) {
            throw new ActionFailedException("Failed to take knowledge!");
        }
        PlayerCard taking = gameWindow.promptSelectPlayerCard(takingFrom.getCardsInHand().toArray(new PlayerCard[0]),
                bundle.getString("knowledge.share"), bundle.getString("selectCardToShare"));
        players[currentPlayerTurn].shareKnowledgeTake(takingFrom, taking);
        return true;
    }

    private boolean handleGiveKnowledge() {
        Player givingTo = gameWindow.promptSelectPlayer(players, bundle.getString("knowledge.give"), bundle.getString("selectThePlayerYouWouldLikeToGiveTo"));
        PlayerCard giving = gameWindow.promptSelectPlayerCard(players[currentPlayerTurn].getCardsInHand().toArray(new PlayerCard[0]),
                bundle.getString("knowledge.share"), bundle.getString("selectCardToShare"));
        players[currentPlayerTurn].shareKnowledgeGive(givingTo, giving);
        return true;
    }

    public boolean handleDiscoverCure(Player cureDiscoverer) {
        int cardsToCure = 5;
        if (cureDiscoverer.getClass().equals(Scientist.class)) {
            cardsToCure = 4;
        }
        if (cureDiscoverer.handSize() < cardsToCure ||
                !cureDiscoverer.getCity().hasResearchStation()) {
            return false;
        }
        String[] selectedCards = new String[cardsToCure];
        CityColor color = selectCureDiscardedCards(
                cureDiscoverer, selectedCards, cardsToCure);
        cureDisease(color);
        return true;
    }

    private void handleActionFailure(ActionFailedException e) {
        gameWindow.displayMessage(bundle.getString("actionFailed"), e.getMessage(), JOptionPane.ERROR_MESSAGE);
    }

    public void setup() {
        placeResearchStationInAtlanta();
        initializeInfectionDeck();
        initializePlayerDeck();
    }

    public void startGame() {
        infectNineCities();
        initializePlayers();
        shuffleEpidemicCardsIntoPlayerDeck();
        shuffleEventCardsIntoPlayerDeck();
        transferPlayToNextPlayer();
    }

    public City getCityByName(String cityName) {
        for (City city : cityMap) {
            if (city.name.equals(cityName)) {
                return city;
            }
        }
        throw new RuntimeException(cityName + " does not exist!");
    }

    public String[] getCityNames() {
        ArrayList<String> cityNames = new ArrayList<>();
        for (City city : cityMap) {
            cityNames.add(city.name);
        }
        return cityNames.toArray(new String[0]);
    }

    private void undoCureSelection(Player currentPlayer, int cardsDrawn) {
        for (int i = 0; i < cardsDrawn; i++) {
            currentPlayer.drawCard(playerDiscardPile.pop());
        }
    }

    private CityColor selectCureDiscardedCards(Player currentPlayer, String[] selectedCards, int numCards) {
        CityColor color = CityColor.EVENT_COLOR;
        for (int i = 0; i < numCards; i++) {
            ArrayList<String> cardNames = currentPlayer.getCardNames();
            selectedCards[i] = gameWindow.promptCureCards(cardNames.toArray(new String[0]));
            playerDiscardPile.push(currentPlayer.discardCardAtIndex(cardNames.indexOf(selectedCards[i])));
            color = getCityByName(selectedCards[0]).color;
            if (color != getCityByName(selectedCards[i]).color) {
                undoCureSelection(currentPlayer, i + 1);
                return CityColor.EVENT_COLOR;
            }
        }
        return color;
    }

    public void cureDisease(CityColor color) {
        String cured = bundle.getString("cured");
        switch (color) {
            case YELLOW -> {
                yellowDiseaseStatus = DiseaseStatus.CURED;
                gameWindow.updateTreatmentIndicator(CityColor.YELLOW, cured);
            }
            case RED -> {
                redDiseaseStatus = DiseaseStatus.CURED;
                gameWindow.updateTreatmentIndicator(CityColor.RED, cured);
            }
            case BLUE -> {
                blueDiseaseStatus = DiseaseStatus.CURED;
                gameWindow.updateTreatmentIndicator(CityColor.BLUE, cured);
            }
            case BLACK -> {
                blackDiseaseStatus = DiseaseStatus.CURED;
                gameWindow.updateTreatmentIndicator(CityColor.BLACK, cured);
            }
        }
        if (yellowDiseaseStatus == DiseaseStatus.CURED
                && redDiseaseStatus == DiseaseStatus.CURED
                && blueDiseaseStatus == DiseaseStatus.CURED
                && blackDiseaseStatus == DiseaseStatus.CURED) {
            gameEnd(GameEndCondition.WIN_ALL_FOUR_CURES_DISCOVERED);
        }
    }

    private boolean handleRoleAction(Player currentPlayer) {
        Class<? extends Player> role = currentPlayer.getClass();
        if (!role.equals(OperationsExpert.class)) {
            if (role.equals(Dispatcher.class)) {
                dispatcherRoleAction();
                return true;
            } else if (role.equals(ContingencyPlanner.class)) {
                return contingencyPlannerRoleAction(currentPlayer);
            }
        } else {
            return operationsExpertRoleAction(currentPlayer);
        }
        return false;
    }

    private boolean contingencyPlannerRoleAction(Player currentPlayer) {
        return ((ContingencyPlanner) currentPlayer).takeEventCardFromDiscardPile();
    }

    private boolean operationsExpertRoleAction(Player currentPlayer) {
        String[] possibleDestinations = getCityNames();
        String destinationName = gameWindow.promptSelectOption(possibleDestinations,
                bundle.getString("selectALocation"), bundle.getString("whereWouldYouLikeToGo"));

        String[] possibleCardsToDiscard = currentPlayer.getCardNames().toArray(new String[0]);
        String cardNameToDiscard = gameWindow.promptSelectOption(possibleCardsToDiscard,
                bundle.getString("selectACard"), bundle.getString("whichCardWouldYouLikeToDiscard"));

        return ((OperationsExpert) currentPlayer).operationsExpertAction(getCityByName(destinationName), cardNameToDiscard);
    }

    private void dispatcherRoleAction() {
        Player selectedPlayer = gameWindow.promptSelectPlayer(players, bundle.getString("moveAPlayer"), bundle.getString("selectThePlayerYouWouldLikeToMove"));
        ArrayList<String> possibleLocations = new ArrayList<>();
        for (Player player : players) {
            possibleLocations.add(player.getCity().name);
        }
        for (City city : cityMap) {
            if (selectedPlayer.getCity().connectedCities.contains(city)) {
                possibleLocations.add(city.name);
            }
        }

        String selectedLocation = gameWindow.promptSelectOption(possibleLocations.toArray(new String[0]),
                bundle.getString("selectALocation"), bundle.getString("whereWouldYouLikeToGo"));

        selectedPlayer.forceRelocatePlayer(getCityByName(selectedLocation));
    }

    private boolean handlePlayEventCard() {
        Player currentPlayer = this.players[currentPlayerTurn];
        ArrayList<PlayerCard> eventCardsInHand = getEventCardsInHand(currentPlayer);

        if (eventCardsInHand.isEmpty()) {
            return false;
        }

        EventCard eventCardToPlay = (EventCard) this.gameWindow.promptSelectPlayerCard(eventCardsInHand.toArray(new PlayerCard[0]),
                bundle.getString("selectAnEventCard"), bundle.getString("whichEventCardWouldYouLikeToPlay"));
        if (eventCardToPlay == null) {
            return false;
        }
        eventCardToPlay.use();
        currentPlayer.cardsInHand.remove(eventCardToPlay);
        return false;
    }

    private ArrayList<PlayerCard> getEventCardsInHand(Player player) {
        ArrayList<PlayerCard> eventCardsInHand = new ArrayList<>();
        for (PlayerCard playerCard : player.getCardsInHand()) {
            if (playerCard.isEvent()) {
                eventCardsInHand.add(playerCard);
            }
        }
        return eventCardsInHand;
    }

    private void gameEnd(GameEndCondition endCondition) {
        this.gameOver = true;
        String gameOverMessage = switch (endCondition) {
            case WIN_ALL_FOUR_CURES_DISCOVERED -> bundle.getString("congratsYouCuredTheDiseases");
            case LOSE_OUTBREAK_MARKER_REACHED_LAST_SPACE -> bundle.getString("gameOver.youLostAfterEightOutbreaks");
            case LOSE_RAN_OUT_OF_DISEASE_CUBES ->
                    bundle.getString("gameOver.YouLostAfterTooManyDiseaseCubesWerePlaced");
            case LOSE_CANNOT_DRAW_PLAYER_CARD -> bundle.getString("gameOver.youLostAfterThePlayerDeckRanOutOfCards");
        };
        gameWindow.showGameOverMessage(gameOverMessage);
    }

    private void infectNineCities() {
        for (int diseaseLevel = 3; diseaseLevel > 0; diseaseLevel--) {
            infectThreeCities(diseaseLevel);
        }
    }

    private void infectThreeCities(int diseaseCubes) {
        for (int i = 0; i < 3; i++) {
            InfectionCard currentCard = this.infectionDeck.pop();
            currentCard.infectDuringSetup(diseaseCubes, this.cubeBank, outbreakManager);
            this.infectionDiscardPile.push(currentCard);
        }
    }

    private void placeResearchStationInAtlanta() {
        City atlanta = getCityByName(bundle.getString("atlanta"));
        atlanta.buildResearchStation();
    }

    private void initializeInfectionDeck() {
        for (City currentCity : cityMap) {
            InfectionCard cardToAdd = new InfectionCard(currentCity);
            this.infectionDeck.add(cardToAdd);
        }
        Collections.shuffle(this.infectionDeck);
    }

    public void addToInfectionDeck(City city) {
        InfectionCard cardToAdd = new InfectionCard(city);
        this.infectionDeck.add(cardToAdd);
    }

    public void addToInfectionDiscardPile(InfectionCard cardToAdd) {
        this.infectionDiscardPile.add(cardToAdd);
    }

    private void initializePlayerDeck() {
        for (City currentCity : cityMap) {
            PlayerCard cardToAdd = new PlayerCard(currentCity);
            this.playerDeck.add(cardToAdd);
        }
        Collections.shuffle(this.playerDeck);
    }

    public void initializePlayers() {
        ArrayList<Player> allPossiblePlayers = new ArrayList<>();
        City atlanta = getCityByName(bundle.getString("atlanta"));
        allPossiblePlayers.add(new Researcher(atlanta));
        allPossiblePlayers.add(new Dispatcher(atlanta));
        allPossiblePlayers.add(new ContingencyPlanner(atlanta, this));
        allPossiblePlayers.add(new Medic(atlanta));
        allPossiblePlayers.add(new OperationsExpert(atlanta));
        allPossiblePlayers.add(new Scientist(atlanta));
        allPossiblePlayers.add(new QuarantineSpecialist(atlanta));

        for (int i = 0; i < 4; i++) {
            int randomIndex = (int) (Math.random() * allPossiblePlayers.size());
            Player newPlayer = allPossiblePlayers.remove(randomIndex);
            newPlayer.name = generatePlayerName(i + 1, newPlayer);
            players[i] = newPlayer;
            atlanta.players.add(newPlayer);
        }
        playersDrawStartingHands();
    }

    private void shuffleEpidemicCardsIntoPlayerDeck() {
        Stack<PlayerCard> playerDeck1 = new Stack<>();
        Stack<PlayerCard> playerDeck2 = new Stack<>();
        Stack<PlayerCard> playerDeck3 = new Stack<>();
        Stack<PlayerCard> playerDeck4 = new Stack<>();
        for (int i = 0; i < 10; i++) {
            playerDeck1.push(playerDeck.pop());
        }
        for (int i = 0; i < 10; i++) {
            playerDeck2.push(playerDeck.pop());
        }
        for (int i = 0; i < 10; i++) {
            playerDeck3.push(playerDeck.pop());
        }
        for (int i = 0; i < 10; i++) {
            playerDeck4.push(playerDeck.pop());
        }
        PlayerCard epidemicCard1 = new PlayerCard(true);
        PlayerCard epidemicCard2 = new PlayerCard(true);
        PlayerCard epidemicCard3 = new PlayerCard(true);
        PlayerCard epidemicCard4 = new PlayerCard(true);
        playerDeck1.push(epidemicCard1);
        playerDeck2.push(epidemicCard2);
        playerDeck3.push(epidemicCard3);
        playerDeck4.push(epidemicCard4);
        Collections.shuffle(playerDeck1);
        Collections.shuffle(playerDeck2);
        Collections.shuffle(playerDeck3);
        Collections.shuffle(playerDeck4);
        for (int i = 0; i < 11; i++) {
            playerDeck.push(playerDeck1.pop());
        }
        for (int i = 0; i < 11; i++) {
            playerDeck.push(playerDeck2.pop());
        }
        for (int i = 0; i < 11; i++) {
            playerDeck.push(playerDeck3.pop());
        }
        for (int i = 0; i < 11; i++) {
            playerDeck.push(playerDeck4.pop());
        }
    }

    private void shuffleEventCardsIntoPlayerDeck() {
        EventCard[] eventCards = createArrayWithAllEventCards();
        for (EventCard eventCard : eventCards) {
            insertEventCardIntoPlayerDeckAtRandomIndex(eventCard);
        }
    }

    private EventCard[] createArrayWithAllEventCards() {
        return new EventCard[]{
                new EventCard(EventName.AIRLIFT, this),
                new EventCard(EventName.ONE_QUIET_NIGHT, this),
                new EventCard(EventName.GOVERNMENT_GRANT, this),
                new EventCard(EventName.FORECAST, this),
                new EventCard(EventName.RESILIENT_POPULATION, this),
        };
    }

    private void insertEventCardIntoPlayerDeckAtRandomIndex(EventCard eventCard) {
        Random rand = new Random();
        int indexToInsertEventCard = rand.nextInt(this.playerDeck.size());
        this.playerDeck.insertElementAt(eventCard, indexToInsertEventCard);
    }

    public String generatePlayerName(int playerNumber, Player player) {
        String playerClassName = player.getClass().getName();
        String[] roleNameParts = playerClassName.split("\\.");
        String roleName = roleNameParts[roleNameParts.length - 1];
        roleName = switch (roleName) {
            case "ContingencyPlanner" -> bundle.getString("contingencyPlanner");
            case "Dispatcher" -> bundle.getString("dispatcher");
            case "Medic" -> bundle.getString("medic");
            case "OperationsExpert" -> bundle.getString("operationsExpert");
            case "QuarantineSpecialist" -> bundle.getString("quarantineSpecialist");
            case "Researcher" -> bundle.getString("researcher");
            case "Scientist" -> bundle.getString("scientist");
            default -> bundle.getString("player");
        };
        return MessageFormat.format(bundle.getString("playerNumberAndRole"), playerNumber, roleName);
    }

    private void playersDrawStartingHands() {
        for (int i = 0; i < 2; i++) {
            for (Player player : this.players) {
                PlayerCard drawnCard = this.playerDeck.pop();
                player.drawCard(drawnCard);
            }
        }
    }

    public void displayGame() {
        gameWindow.showWindow();
    }

    public void nextPlayerTurn() {
        infectCitiesBasedOnInfectionRate();
        if (cubeBank.remainingCubes(CityColor.BLUE) < 0 || cubeBank.remainingCubes(CityColor.YELLOW) < 0 ||
                cubeBank.remainingCubes(CityColor.RED) < 0 || cubeBank.remainingCubes(CityColor.BLACK) < 0) {
            gameEnd(GameEndCondition.LOSE_RAN_OUT_OF_DISEASE_CUBES);
        }
        resetOutbreakStatus();
        if (outbreakManager.getOutbreaks() >= 8) {
            gameEnd(GameEndCondition.LOSE_OUTBREAK_MARKER_REACHED_LAST_SPACE);
        }
        transferPlayToNextPlayer();
    }

    public void transferPlayToNextPlayer() {
        currentPlayerTurn++;
        if (currentPlayerTurn >= NUM_PLAYERS) {
            currentPlayerTurn = 0;
        }
        this.currentPlayerRemainingActions = 4;
        String nextPlayerName = players[currentPlayerTurn].name;
        gameWindow.displayNextPlayerInfo(nextPlayerName, this.currentPlayerRemainingActions);
    }

    public void resetOutbreakStatus() {
        for (City city : cityMap) {
            city.outbreakIsHappening = false;
        }
    }

    public void infectCitiesBasedOnInfectionRate() {
        if (this.isQuietNight) {
            this.isQuietNight = false;
            return;
        }
        int currentInfectionRate = this.infectionRateValues[this.infectionRateIndex];
        for (int i = 0; i < currentInfectionRate; i++) {
            InfectionCard currentCard = this.infectionDeck.pop();
            DiseaseStatus status = selectColorOfStatus(currentCard);
            currentCard.cardDrawn(status, cubeBank, outbreakManager);
            this.infectionDiscardPile.push(currentCard);
        }
    }

    private DiseaseStatus selectColorOfStatus(InfectionCard currentCard) {
        CityColor color = currentCard.getCityColor();
        return switch (color) {
            case YELLOW -> yellowDiseaseStatus;
            case RED -> redDiseaseStatus;
            case BLUE -> blueDiseaseStatus;
            case BLACK -> blackDiseaseStatus;
            case EVENT_COLOR -> null;
        };
    }

    public DiseaseStatus getStatus(CityColor color) {
        return switch (color) {
            case YELLOW -> yellowDiseaseStatus;
            case RED -> redDiseaseStatus;
            case BLUE -> blueDiseaseStatus;
            case BLACK -> blackDiseaseStatus;
            default -> DiseaseStatus.ACTIVE;
        };
    }

    public int getInfectionRate() {
        return this.infectionRateValues[infectionRateIndex];
    }

    public void increaseInfectionRate() {
        this.infectionRateIndex = Math.min(this.infectionRateIndex + 1, 6);
        gameWindow.updateInfectionRate(infectionRateValues[infectionRateIndex]);
    }

    public void airLift() {
        Player selectedPlayer = gameWindow.promptSelectPlayer(players, bundle.getString("moveAPlayer"), bundle.getString("selectThePlayerYouWouldLikeToMove"));
        CompletableFuture<City> userSelection = gameWindow.selectCity();
        userSelection.thenAccept(selectedPlayer::forceRelocatePlayer); //Method reference
    }

    public void forecast() {
        int numCardsToRearrange = Math.min(6, this.infectionDeck.size());
        InfectionCard[] cardsToRearrange = new InfectionCard[numCardsToRearrange];
        for (int i = 0; i < numCardsToRearrange; i++) {
            cardsToRearrange[i] = this.infectionDeck.pop();
        }

        playerRearrangeCards(cardsToRearrange);

        for (int i = cardsToRearrange.length - 1; i >= 0; i--) {
            this.infectionDeck.push(cardsToRearrange[i]);
        }
    }

    private void playerRearrangeCards(InfectionCard[] cardsToRearrange) {
        for (; ; ) {
            gameWindow.displayInfectionCards(cardsToRearrange, bundle.getString("topOfInfectionDeck"));

            String[] options = new String[]{bundle.getString("continueRearranging"), bundle.getString("putCardsBackOnDeck")};
            String nextAction = gameWindow.promptSelectOption(options,
                    bundle.getString("selectAnOption"), bundle.getString("wouldYouLikeToContinueRearrangingTheTopOfTheInfectionDeck"));
            if (nextAction == null || !nextAction.equals(bundle.getString("continueRearranging"))) {
                gameWindow.destroyCurrentInfectionCardsDialog();
                break;
            }

            InfectionCard firstCardToSwap = gameWindow.promptInfectionCard(cardsToRearrange,
                    bundle.getString("selectACard"), bundle.getString("selectTheFirstCardToSwap"));
            InfectionCard secondCardToSwap = gameWindow.promptInfectionCard(cardsToRearrange,
                    bundle.getString("selectACard"), bundle.getString("selectTheSecondCardToSwap"));
            swapCards(cardsToRearrange, firstCardToSwap, secondCardToSwap);
            gameWindow.destroyCurrentInfectionCardsDialog();
        }
    }

    private void swapCards(InfectionCard[] cardsToRearrange, InfectionCard firstCardToSwap, InfectionCard secondCardToSwap) {
        if (firstCardToSwap.equals(secondCardToSwap)) {
            return;
        }
        int firstIndex = -1;
        int secondIndex = -1;
        for (int i = 0; i < cardsToRearrange.length; i++) {
            if (cardsToRearrange[i].equals(firstCardToSwap)) {
                firstIndex = i;
            } else if (cardsToRearrange[i].equals(secondCardToSwap)) {
                secondIndex = i;
            }
        }
        if (firstIndex != -1 && secondIndex != -1) {
            cardsToRearrange[firstIndex] = cardsToRearrange[secondIndex];
            cardsToRearrange[secondIndex] = firstCardToSwap;
        }
    }

    public void governmentGrant() {
        CompletableFuture<City> userSelection = gameWindow.selectCity();
        userSelection.thenAccept((city) -> {
            city.buildResearchStation();
        });
    }

    public void oneQuietNight() {
        this.isQuietNight = true;
    }

    public void resilientPopulation() {
        InfectionCard cardToRemove = gameWindow.promptInfectionCard(infectionDiscardPile.toArray(new InfectionCard[0]),
                bundle.getString("removeAnInfectionCard"), bundle.getString("selectAnInfectionCardToRemoveFromTheGame"));

        infectionDiscardPile.remove(cardToRemove);
    }

    public int infectionDeckSize() {
        return this.infectionDeck.size();
    }

    public int playerDeckSize() {
        return this.playerDeck.size();
    }

    public int infectionDiscardPileSize() {
        return this.infectionDiscardPile.size();
    }

    public boolean drawTwoPlayerCards() {
        Player currentPlayer = players[currentPlayerTurn];
        try {
            PlayerCard card1 = this.playerDeck.pop();
            PlayerCard card2 = this.playerDeck.pop();
            currentPlayer.drawCard(card1);
            currentPlayer.drawCard(card2);
            playerHandFull(currentPlayer);
            if (card1.isEpidemic || card2.isEpidemic) {
                this.epidemic();
            }
            if (card1.isEpidemic && card2.isEpidemic) {
                this.epidemic();
            }
        } catch (EmptyStackException e) {
            gameEnd(GameEndCondition.LOSE_CANNOT_DRAW_PLAYER_CARD);
            return false;
        }
        return true;
    }

    public void playerHandFull(Player currentPlayer) {
        while (currentPlayer.handSize() > 7) {
            if (handContainsEventCard(currentPlayer)) {
                boolean playedEventCard = givePlayerOptionToPlayEventCard(currentPlayer);
                if (playedEventCard) {
                    return;
                }
            }

            String[] cardNamesToSelectFrom = new String[currentPlayer.cardsInHand.size()];
            for (int i = 0; i < currentPlayer.cardsInHand.size(); i++) {
                cardNamesToSelectFrom[i] = currentPlayer.cardsInHand.get(i).name;
            }

            String cardToDiscardName = gameWindow.promptSelectOption(cardNamesToSelectFrom,
                    bundle.getString("yourHandIsFull"), bundle.getString("selectACardToDiscard"));
            if (cardToDiscardName != null) {
                currentPlayer.discardCardWithName(cardToDiscardName);
            }
        }
    }

    private boolean givePlayerOptionToPlayEventCard(Player currentPlayer) {
        ArrayList<String> cardsToSelectFrom = new ArrayList<>();

        String[] possibleActions = new String[]{bundle.getString("playAnEventCard"), bundle.getString("discardACard")};
        String handOverflowingAction = gameWindow.promptSelectOption(possibleActions,
                bundle.getString("yourHandIsFull"), bundle.getString("youreHoldingAnEventCardWouldYouLikeToPlayItOrDiscardACard"));
        if (!handOverflowingAction.equals(bundle.getString("playAnEventCard"))) {
            return false;
        }

        getPossibleEventCardsToPlay(currentPlayer, cardsToSelectFrom);
        String eventCardNameToPlay = gameWindow.promptSelectOption(cardsToSelectFrom.toArray(new String[0]),
                bundle.getString("playAnEventCard"), bundle.getString("selectAnEventCardToPlay"));

        for (PlayerCard card : currentPlayer.cardsInHand) {
            if (card.name.equals(eventCardNameToPlay)) {
                playEventCard(currentPlayer, (EventCard) card);
                break;
            }
        }
        return true;
    }

    private void getPossibleEventCardsToPlay(Player currentPlayer, ArrayList<String> cardsToSelectFrom) {
        for (PlayerCard card : currentPlayer.cardsInHand) {
            if (card.isEvent()) {
                cardsToSelectFrom.add(card.name);
            }
        }
    }

    public void playEventCard(Player currentPlayer, EventCard eventCardToPlay) {
        eventCardToPlay.use();
        currentPlayer.discardCardWithName(eventCardToPlay.name);
    }

    private boolean handContainsEventCard(Player currentPlayer) {
        for (PlayerCard card : currentPlayer.cardsInHand) {
            if (card.isEvent()) {
                return true;
            }
        }
        return false;
    }

    public int[] diseaseCubesLeft() {
        int[] toReturn = new int[4];
        toReturn[0] = cubeBank.remainingCubes(CityColor.YELLOW);
        toReturn[1] = cubeBank.remainingCubes(CityColor.RED);
        toReturn[2] = cubeBank.remainingCubes(CityColor.BLUE);
        toReturn[3] = cubeBank.remainingCubes(CityColor.BLACK);
        return toReturn;
    }

    public void epidemic() {
        gameWindow.displayMessage(bundle.getString("epidemic"), bundle.getString("anEpidemicOccurred"), JOptionPane.INFORMATION_MESSAGE);
        this.increaseInfectionRate();
        Stack<InfectionCard> placeHolderStack = new Stack<>();
        for (int i = 0; i < infectionDeck.size() - 1; i++) {
            placeHolderStack.push(infectionDeck.pop());
        }
        InfectionCard cardToInfect = infectionDeck.pop();
        selectColorOfStatus(cardToInfect);
        cardToInfect.cardDrawnDuringEpidemic(this.cubeBank, outbreakManager);
        for (int i = 0; i < placeHolderStack.size(); i++) {
            infectionDeck.push(placeHolderStack.pop());
        }
        infectionDiscardPile.push(cardToInfect);
        Collections.shuffle(infectionDiscardPile);
        for (int i = 0; i < this.infectionDiscardPile.size(); i++) {
            infectionDeck.push(infectionDiscardPile.pop());
        }
    }

    public int getCityInfectionLevel(String cityName, CityColor cityColor) {
        City city = getCityByName(cityName);
        return city.getInfectionLevel(cityColor);
    }

    public Stack<PlayerCard> getPlayerDiscardPile() {
        return this.playerDiscardPile;
    }

    public void removeEventCardFromPlayerDiscardPile(EventCard card) {
        this.playerDiscardPile.remove(card);
    }

    public void addPlayerCardToDiscardPile(PlayerCard card) {
        this.playerDiscardPile.push(card);
    }
}
