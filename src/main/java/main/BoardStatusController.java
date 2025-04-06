package main;

import main.roles.*;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BoardStatusController {
    private int numPlayers = 4;

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

    private HashMap<CityColor, DiseaseStatus> diseaseStatuses;

    public GameWindowInterface gameWindow;
    public boolean isQuietNight;
    private boolean gameOver;
    private int numEpidemicCards;
    public OutbreakManager outbreakManager;

    private final ResourceBundle bundle;
    private final EventCardManager eventCardManager;

    public BoardStatusController(GameWindowInterface gameWindow, ArrayList<City> cityMap, int numPlayers, int numEpidemicCards) {
        if (Pandemic.bundle != null) {
            this.bundle = Pandemic.bundle;
        } else {
            Locale locale = new Locale("en", "US");
            this.bundle = ResourceBundle.getBundle("messages", locale);
        }

        this.eventCardManager = new EventCardManager(gameWindow, this.bundle);

        this.numPlayers = numPlayers;
        this.players = new Player[numPlayers];

        this.numEpidemicCards = numEpidemicCards;

        this.infectionRateValues = new int[]{2, 2, 2, 3, 3, 4, 4};
        this.infectionRateIndex = 0;

        this.playerDeck = new Stack<>();
        this.playerDiscardPile = new Stack<>();
        this.infectionDeck = new Stack<>();
        this.infectionDiscardPile = new Stack<>();
        this.cubeBank = new DiseaseCubeBank();
        this.currentPlayerTurn = getNumPlayers();
        this.currentPlayerRemainingActions = 0;
        this.outbreakManager = new OutbreakManager(gameWindow);

        this.diseaseStatuses = new HashMap<>();
        this.diseaseStatuses.put(CityColor.YELLOW, DiseaseStatus.ACTIVE);
        this.diseaseStatuses.put(CityColor.RED, DiseaseStatus.ACTIVE);
        this.diseaseStatuses.put(CityColor.BLUE, DiseaseStatus.ACTIVE);
        this.diseaseStatuses.put(CityColor.BLACK, DiseaseStatus.ACTIVE);

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
                case PLAY_EVENT_CARD -> eventCardManager.handlePlayEventCard(players[currentPlayerTurn]);
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
            for(CityColor color : diseaseStatuses.keySet()){
                if (cubeBank.remainingCubes(color) == 24 && diseaseStatuses.get(color).equals(DiseaseStatus.CURED)) {
                    diseaseStatuses.put(color, DiseaseStatus.ERADICATED);
                }
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
        CompletableFuture<City> userSelection = gameWindow.selectCity(players[currentPlayerTurn].driveFerryDestinations());
        userSelection.thenAccept((city) -> {
            players[currentPlayerTurn].move(city);
        });
        return true;
    }

    public boolean handleDirectFlight() {
        CompletableFuture<City> userSelection = gameWindow.selectCity(players[currentPlayerTurn].directFlightDestinations());
        userSelection.thenAccept((city) -> {
            players[currentPlayerTurn].directFlight(city);
        });
        return true;
    }

    public boolean handleCharterFlight() {
        CompletableFuture<City> userSelection = gameWindow.selectCity(new HashSet<>(cityMap));
        userSelection.thenAccept((city) -> {
            players[currentPlayerTurn].charterFlight(city);
        });
        return true;
    }

    public boolean handleShuttleFlight() {
        HashSet<City> citiesWithResearchStation = new HashSet();
        for(City city : cityMap) {
            if(city.hasResearchStation()){
                citiesWithResearchStation.add(city);
            }
        }
        CompletableFuture<City> userSelection = gameWindow.selectCity(citiesWithResearchStation);
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
        DiseaseStatus status = diseaseStatuses.get(colorToTreat);
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
//            throw new ActionFailedException("Failed to take knowledge!");
            return false;
        }
        PlayerCard taking = gameWindow.promptSelectPlayerCard(takingFrom.getCardsInHand().toArray(new PlayerCard[0]),
                bundle.getString("knowledge.share"), bundle.getString("selectCardToShare"));
        players[currentPlayerTurn].shareKnowledgeTake(takingFrom, taking);
        return true;
    }

    private boolean handleGiveKnowledge() {
        Player givingTo = gameWindow.promptSelectPlayer(players, bundle.getString("knowledge.give"), bundle.getString("selectThePlayerYouWouldLikeToGiveTo"));
        if (givingTo == null) {
            return false;
        }
        PlayerCard giving = gameWindow.promptSelectPlayerCard(players[currentPlayerTurn].getCardsInHand().toArray(new PlayerCard[0]),
                bundle.getString("knowledge.share"), bundle.getString("selectCardToShare"));
        if (giving == null) {
            return false;
        }
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
        eventCardManager.shuffleEventCardsIntoPlayerDeck(this.playerDeck, this);
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
                throw new ActionFailedException("You do not have the right cards for this operation!");
            }
        }
        return color;
    }

    public void cureDisease(CityColor color) {
        String cured = bundle.getString("cured");
        diseaseStatuses.put(color, DiseaseStatus.CURED);
        gameWindow.updateTreatmentIndicator(color, cured);
        if (allDiseasesCured()) {
            gameEnd(GameEndCondition.WIN_ALL_FOUR_CURES_DISCOVERED);
        }
    }

    private boolean allDiseasesCured() {
        for(DiseaseStatus diseaseStatus : diseaseStatuses.values()){
            if(diseaseStatus != DiseaseStatus.CURED)
                return false;
        }
        return true;
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
        HashSet<City> possibleLocations = new HashSet<>();
        for (Player player : players) {
            possibleLocations.add(player.getCity());
        }
        for (City city : cityMap) {
            if (selectedPlayer.getCity().connectedCities.contains(city)) {
                possibleLocations.add(city);
            }
        }

        CompletableFuture<City> userSelection = gameWindow.selectCity(possibleLocations);
        userSelection.thenAccept(selectedPlayer::forceRelocatePlayer); //Method reference
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

    public void initFourGenericPlayers() {
        City atlanta = getCityByName(bundle.getString("atlanta"));

        for (int i = 0; i < 4; i++) {
            Player newPlayer = new Player(Color.BLACK, atlanta);
            newPlayer.name = generatePlayerName(i + 1, newPlayer);
            players[i] = newPlayer;
            atlanta.players.add(newPlayer);
        }
        playersDrawStartingHands();
    }

    public void playEventCard(Player currentPlayer, EventCard eventCardToPlay) {
        eventCardManager.playEventCard(currentPlayer, eventCardToPlay);
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

        for (int i = 0; i < numPlayers; i++) {
            int randomIndex = (int) (Math.random() * allPossiblePlayers.size());
            Player newPlayer = allPossiblePlayers.remove(randomIndex);
            newPlayer.name = generatePlayerName(i + 1, newPlayer);
            players[i] = newPlayer;
            atlanta.players.add(newPlayer);
        }
        playersDrawStartingHands();
    }

    private void shuffleEpidemicCardsIntoPlayerDeck() {
        for(int j = 0; j < numEpidemicCards; j++) {
            Stack<PlayerCard> singleCardDeck = new Stack<>();
            int numCardsPerStack = 48 / (numPlayers * (6 - numPlayers));
            for (int i = 0; i < numCardsPerStack; i++) {
                singleCardDeck.push(playerDeck.pop());
            }
            PlayerCard epidemicCard = new PlayerCard(true);
            singleCardDeck.push(epidemicCard);
            Collections.shuffle(singleCardDeck);
            for (int i = 0; i < numCardsPerStack + 1; i++) {
                playerDeck.push(singleCardDeck.pop());
            }
        }
    }

    public String generatePlayerName(int playerNumber, Player player) {
        player.playerNum = "P" + playerNumber;
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
        for (int i = 0; i < 6 - numPlayers; i++) {
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
        if (currentPlayerTurn >= getNumPlayers()) {
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
        return diseaseStatuses.get(color);
    }

    public DiseaseStatus getStatus(CityColor color) {
        return diseaseStatuses.get(color);
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
        CompletableFuture<City> userSelection = gameWindow.selectCity(new HashSet<>(cityMap));
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
        CompletableFuture<City> userSelection = gameWindow.selectCity(new HashSet<>(cityMap));
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
            if (eventCardManager.handContainsEventCard(currentPlayer)) {
                boolean playedEventCard = eventCardManager.givePlayerOptionToPlayEventCard(currentPlayer);
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

    public int getNumPlayers() {
        return numPlayers;
    }
}
