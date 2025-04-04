package tests;

//import main.Point;
import main.*;
import main.roles.*;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class BoardStatusControllerTest {
    private BoardStatusController bsc;

    private City chicago;
    private City atlanta;
    private City london;
    private City newYork;
    private City sanFrancisco;
    private City montreal;
    private City washington;
    private City madrid;
    private City miami;

    private final int NUM_PLAYERS = 4;

    public void createNewBSCWithTestMap(GameWindowInterface gameWindow) {
        chicago = new City("Chicago", 315, 405, CityColor.BLUE);
        atlanta = new City("Atlanta", 380, 515, CityColor.BLUE);
        london = new City("London", 0, 0, CityColor.BLUE);
        sanFrancisco = new City("San Francisco", 105, 460, CityColor.BLUE);
        montreal = new City("Montreal", 485, 400, CityColor.BLUE);
        washington = new City("Washington", 555, 510, CityColor.BLUE);
        newYork = new City("New York", 610, 420, CityColor.BLUE);
        madrid = new City("Madrid", 890, 475, CityColor.BLUE);
        miami = new City("Miami", 485, 635, CityColor.YELLOW);

        chicago.addConnection(atlanta);
        chicago.addConnection(montreal);
        montreal.addConnection(newYork);
        montreal.addConnection(chicago);
        newYork.addConnection(montreal);
        atlanta.addConnection(chicago);

        ArrayList<City> basicMap = new ArrayList<>();
        basicMap.add(chicago);
        basicMap.add(atlanta);
        basicMap.add(london);
        basicMap.add(newYork);
        basicMap.add(sanFrancisco);
        basicMap.add(montreal);
        basicMap.add(washington);
        basicMap.add(madrid);
        basicMap.add(miami);

        this.bsc = new BoardStatusController(gameWindow, basicMap, NUM_PLAYERS);
    }

    public CompletableFuture<City> generateTestFuture(City city) {
        CompletableFuture<City> cityFuture = new CompletableFuture<City>();
        cityFuture.complete(city);
        return cityFuture;
    }

    @Test
    public void testInitialInfectionRate() {
        createNewBSCWithTestMap(new DummyGameWindow());
        assertEquals(2, this.bsc.getInfectionRate());
    }

    @Test
    public void testAdditionalInfectionRates() {
        createNewBSCWithTestMap(new DummyGameWindow());
        assertEquals(2, this.bsc.getInfectionRate());
        this.bsc.increaseInfectionRate();
        this.bsc.increaseInfectionRate();
        assertEquals(2, this.bsc.getInfectionRate());
        this.bsc.increaseInfectionRate();
        assertEquals(3, this.bsc.getInfectionRate());
        this.bsc.increaseInfectionRate();
        assertEquals(3, this.bsc.getInfectionRate());
        this.bsc.increaseInfectionRate();
        assertEquals(4, this.bsc.getInfectionRate());
        this.bsc.increaseInfectionRate();
        assertEquals(4, this.bsc.getInfectionRate());
        this.bsc.increaseInfectionRate();
        assertEquals(4, this.bsc.getInfectionRate());
    }

    @Test
    public void testInitializeInfectionDeck() {
        Pandemic.bundle = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        bsc = new BoardStatusController(new DummyGameWindow(), Pandemic.createMap(), NUM_PLAYERS);
        assertEquals(0, this.bsc.infectionDeckSize());
        this.bsc.setup();
        assertEquals(48, this.bsc.infectionDeckSize());
        assertEquals(0, this.bsc.infectionDiscardPileSize());
    }

    @Test
    public void testInfectionDeckAfterGameStart() {
        Pandemic.bundle = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        bsc = new BoardStatusController(new DummyGameWindow(), Pandemic.createMap(), NUM_PLAYERS);
        this.bsc.setup();
        this.bsc.startGame();
        assertEquals(39, this.bsc.infectionDeckSize());
        assertEquals(9, this.bsc.infectionDiscardPileSize());
    }

    @Test
    public void testAirlift() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        EventCard airlift = new EventCard(EventName.AIRLIFT,bsc);

        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(),anyObject(),anyObject())).andReturn(airlift);
        EasyMock.expect(gw.promptSelectPlayer(anyObject(),anyObject(),anyObject())).andReturn(bsc.players[1]);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(london));
        EasyMock.replay(gw);

        bsc.players[0].drawCard(airlift);
        assertTrue(bsc.players[0].getCardsInHand().contains(airlift));
        assertEquals(atlanta, bsc.players[1].getCity());
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);

        assertEquals(london, bsc.players[1].getCity());
        assertFalse(bsc.players[0].getCardsInHand().contains(airlift));
    }

    @Test
    public void testGovernmentGrant() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        EventCard governmentGrant = new EventCard(EventName.GOVERNMENT_GRANT,bsc);

        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(),anyObject(),anyObject())).andReturn(governmentGrant);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(chicago));
        EasyMock.replay(gw);

        assertEquals(4,bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);
        assertEquals(4,bsc.currentPlayerRemainingActions);
        assertFalse(chicago.hasResearchStation());

        bsc.players[0].drawCard(governmentGrant);
        assertTrue(bsc.players[0].getCardsInHand().contains(governmentGrant));
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);
        assertTrue(chicago.hasResearchStation());
        assertFalse(bsc.players[0].getCardsInHand().contains(governmentGrant));
    }

    @Test
    public void testEventCardToPlayNull() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        EventCard governmentGrant = new EventCard(EventName.GOVERNMENT_GRANT,bsc);

        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(),anyObject(),anyObject())).andReturn(null);
        EasyMock.replay(gw);

        assertEquals(4,bsc.currentPlayerRemainingActions);
        assertFalse(chicago.hasResearchStation());
        bsc.players[0].drawCard(governmentGrant);
        assertTrue(bsc.players[0].getCardsInHand().contains(governmentGrant));
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);
        assertEquals(4,bsc.currentPlayerRemainingActions);
        assertFalse(chicago.hasResearchStation());
        assertTrue(bsc.players[0].getCardsInHand().contains(governmentGrant));
    }

    @Test
    public void testForecastNoRearranging() {
        GameWindow mockedGameWindow = EasyMock.niceMock(GameWindow.class);
        createNewBSCWithTestMap(mockedGameWindow);
        bsc.setup();
        Stack<InfectionCard> testInfectionDeck = new Stack<>();
        String[] cityNames = new String[]{"Washington", "Essen", "Delhi", "Chicago", "Baghdad", "Atlanta"};
        for (String cityName : cityNames) {
            City currentCity = new City(cityName, 0, 0, CityColor.BLUE);
            InfectionCard cardToAdd = new InfectionCard(currentCity);
            testInfectionDeck.push(cardToAdd);
        }
        bsc.infectionDeck = testInfectionDeck;

        mockedGameWindow.displayInfectionCards(anyObject(), anyObject());
        EasyMock.expectLastCall();
        EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Put cards back on deck");
        mockedGameWindow.destroyCurrentInfectionCardsDialog();
        EasyMock.expectLastCall();

        EasyMock.replay(mockedGameWindow);

        Player player = new Medic(new City("Atlanta", 0, 0, CityColor.BLUE));
        EventCard forecastCard = new EventCard(EventName.FORECAST, bsc);

        player.drawCard(forecastCard);
        assertTrue(player.getCardsInHand().contains(forecastCard));

        bsc.playEventCard(player, forecastCard);

        assertFalse(player.getCardsInHand().contains(forecastCard));
        String[] expectedCityNames = new String[]{"Atlanta", "Baghdad", "Chicago", "Delhi", "Essen", "Washington"};
        for (int i = 0; i < 6; i++) {
            assertEquals(expectedCityNames[i], bsc.infectionDeck.pop().city.name);
        }
        EasyMock.verify(mockedGameWindow);
    }

    @Test
    public void testForecastReverseTopSixCards() {
        GameWindow mockedGameWindow = EasyMock.niceMock(GameWindow.class);
        createNewBSCWithTestMap(mockedGameWindow);
        bsc.setup();
        Stack<InfectionCard> testInfectionDeck = new Stack<>();
        Stack<InfectionCard> expectedInfectionDeck = new Stack<>();
        InfectionCard[] listOfInfectionCards = new InfectionCard[6];
        String[] cityNames = new String[]{"Atlanta", "Baghdad", "Chicago", "Delhi", "Essen", "Washington"};
        for (int i = 0; i < cityNames.length; i++) {
            City currentCity = new City(cityNames[i], 0, 0, CityColor.BLUE);
            InfectionCard cardToAdd = new InfectionCard(currentCity);
            testInfectionDeck.push(cardToAdd);
            expectedInfectionDeck.push(cardToAdd);
            listOfInfectionCards[i] = cardToAdd;
        }
        bsc.infectionDeck = testInfectionDeck;
        Collections.reverse(expectedInfectionDeck);

        for (int i = 0; i < 3; i++) {
            int highIndex = cityNames.length - 1 - i;
            EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Continue Rearranging");
            EasyMock.expect(mockedGameWindow.promptInfectionCard(anyObject(), anyObject(), anyObject())).andReturn(listOfInfectionCards[i]);
            EasyMock.expect(mockedGameWindow.promptInfectionCard(anyObject(), anyObject(), anyObject())).andReturn(listOfInfectionCards[highIndex]);
        }

        EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Put cards back on deck");

        for (int i = 0; i < cityNames.length / 2 + 1; i++) {
            mockedGameWindow.displayInfectionCards(anyObject(), anyObject());
            EasyMock.expectLastCall();
            mockedGameWindow.destroyCurrentInfectionCardsDialog();
            EasyMock.expectLastCall();
        }

        EasyMock.replay(mockedGameWindow);

        Player player = new Medic(new City("Atlanta", 0, 0, CityColor.BLUE));
        EventCard forecastCard = new EventCard(EventName.FORECAST, bsc);

        player.drawCard(forecastCard);
        assertTrue(player.getCardsInHand().contains(forecastCard));

        bsc.playEventCard(player, forecastCard);

        assertFalse(player.getCardsInHand().contains(forecastCard));
        for (String cityName : cityNames) {
            assertEquals(cityName, bsc.infectionDeck.pop().city.name);
        }
        EasyMock.verify(mockedGameWindow);
    }

    @Test
    public void testForecastFiveCardsInDeck() {
        GameWindow mockedGameWindow = EasyMock.niceMock(GameWindow.class);
        createNewBSCWithTestMap(mockedGameWindow);
        bsc.setup();

        String[] testCityNames = new String[]{"Atlanta", "Baghdad", "Chicago", "Delhi", "Washington"};
        Stack<InfectionCard> testInfectionDeck = new Stack<>();
        InfectionCard[] listOfInfectionCards = new InfectionCard[5];

        for (int i = 0; i < testCityNames.length; i++) {
            City currentTestCity = new City(testCityNames[i], 0, 0, CityColor.BLUE);
            InfectionCard cardToAdd = new InfectionCard(currentTestCity);
            testInfectionDeck.push(cardToAdd);
            listOfInfectionCards[i] = cardToAdd;
        }

        bsc.infectionDeck = testInfectionDeck;

        EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Continue Rearranging");
        EasyMock.expect(mockedGameWindow.promptInfectionCard(anyObject(), anyObject(), anyObject())).andReturn(listOfInfectionCards[1]);
        EasyMock.expect(mockedGameWindow.promptInfectionCard(anyObject(), anyObject(), anyObject())).andReturn(listOfInfectionCards[4]);

        EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Put cards back on deck");

        mockedGameWindow.displayInfectionCards(anyObject(), anyObject());
        EasyMock.expectLastCall();
        mockedGameWindow.destroyCurrentInfectionCardsDialog();
        EasyMock.expectLastCall();
        mockedGameWindow.displayInfectionCards(anyObject(), anyObject());
        EasyMock.expectLastCall();
        mockedGameWindow.destroyCurrentInfectionCardsDialog();
        EasyMock.expectLastCall();

        EasyMock.replay(mockedGameWindow);

        Player player = new Medic(new City("Atlanta", 0, 0, CityColor.BLUE));
        EventCard forecastCard = new EventCard(EventName.FORECAST, bsc);

        player.drawCard(forecastCard);
        assertTrue(player.getCardsInHand().contains(forecastCard));

        bsc.playEventCard(player, forecastCard);

        assertFalse(player.getCardsInHand().contains(forecastCard));
        String[] expectedCityNames = new String[]{"Baghdad", "Delhi", "Chicago", "Washington", "Atlanta"};
        for (String expectedCityName : expectedCityNames) {
            assertEquals(expectedCityName, bsc.infectionDeck.pop().city.name);
        }
        EasyMock.verify(mockedGameWindow);
    }

    @Test
    public void testOneQuietNight() {
        createNewBSCWithTestMap(new DummyGameWindow());
        bsc.addToInfectionDeck(atlanta);
        bsc.oneQuietNight();
        bsc.infectCitiesBasedOnInfectionRate();
        assertEquals(0, bsc.getCityInfectionLevel("Atlanta", CityColor.BLUE));
    }

    @Test
    public void testResilientPopulation() {
        GameWindow mockedGameWindow = EasyMock.niceMock(GameWindow.class);
        createNewBSCWithTestMap(mockedGameWindow);
        InfectionCard atlantaCard = new InfectionCard(atlanta);
        InfectionCard chicagoCard = new InfectionCard(chicago);
        InfectionCard washingtonCard = new InfectionCard(washington);
        bsc.addToInfectionDiscardPile(atlantaCard);
        bsc.addToInfectionDiscardPile(chicagoCard);
        bsc.addToInfectionDiscardPile(washingtonCard);

        EasyMock.expect(mockedGameWindow.promptInfectionCard(anyObject(), anyObject(), anyObject())).andReturn(atlantaCard);
        EasyMock.replay(mockedGameWindow);

        assertEquals(3, bsc.infectionDiscardPileSize());
        assertEquals(0, bsc.playerDiscardPile.size());
        assertTrue(bsc.infectionDiscardPile.contains(atlantaCard));

        EventCard resilientPopulationCard = new EventCard(EventName.RESILIENT_POPULATION, bsc);
        resilientPopulationCard.use();

        assertEquals(2, bsc.infectionDiscardPileSize());
        assertFalse(bsc.infectionDiscardPile.contains(atlantaCard));
        assertTrue(bsc.playerDiscardPile.contains(resilientPopulationCard));
        EasyMock.verify(mockedGameWindow);
    }

    @Test
    public void testDriveFerry() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        Pandemic.bundle = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(chicago));
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(montreal));
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(newYork));
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(miami));
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(montreal));
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();

        assertDoesNotThrow(() -> this.bsc.handleAction(PlayerAction.DRIVE_FERRY));
        assertEquals(3,this.bsc.currentPlayerRemainingActions);
        assertDoesNotThrow(() -> this.bsc.handleAction(PlayerAction.DRIVE_FERRY));
        assertEquals(2,this.bsc.currentPlayerRemainingActions);
        assertDoesNotThrow(() -> this.bsc.handleAction(PlayerAction.DRIVE_FERRY));
        assertEquals(1,this.bsc.currentPlayerRemainingActions);
        this.bsc.handleAction(PlayerAction.DRIVE_FERRY);
        assertEquals(1, this.bsc.currentPlayerRemainingActions);
        assertEquals(0,bsc.currentPlayerTurn);
        assertDoesNotThrow(() -> this.bsc.handleAction(PlayerAction.DRIVE_FERRY));
        assertEquals(1,bsc.currentPlayerTurn);
        assertEquals(4,this.bsc.currentPlayerRemainingActions);
        EasyMock.verify(gw);
    }

    @Test
    public void testDiscoverCure() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("Chicago");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("New York");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("San Francisco");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("Montreal");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("Washington");
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        bsc.setup();

        Player player = new Medic(atlanta);

        PlayerCard chicagoCard = new PlayerCard(chicago);
        PlayerCard nyCard = new PlayerCard(newYork);
        PlayerCard montrealCard = new PlayerCard(montreal);
        PlayerCard sfCard = new PlayerCard(sanFrancisco);
        PlayerCard washingtonCard = new PlayerCard(washington);

        player.drawCard(chicagoCard);
        player.drawCard(nyCard);
        player.drawCard(montrealCard);
        player.drawCard(sfCard);
        player.drawCard(washingtonCard);

        assertTrue(bsc.handleDiscoverCure(player));
        assertFalse(bsc.handleDiscoverCure(player));
        EasyMock.verify(gw);
    }

    @Test
    public void testScientistCureDisease() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("Chicago");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("New York");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("San Francisco");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("Montreal");
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        bsc.setup();

        Player player = new Scientist(atlanta);

        PlayerCard chicagoCard = new PlayerCard(chicago);
        PlayerCard nyCard = new PlayerCard(newYork);
        PlayerCard montrealCard = new PlayerCard(montreal);
        PlayerCard sfCard = new PlayerCard(sanFrancisco);

        player.drawCard(chicagoCard);
        player.drawCard(nyCard);
        player.drawCard(montrealCard);
        player.drawCard(sfCard);

        assertTrue(bsc.handleDiscoverCure(player));
        assertFalse(bsc.handleDiscoverCure(player));
        EasyMock.verify(gw);
    }

    @Test
    public void testGameLostWhenPlayerDeckEmpty() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        gw.showGameOverMessage("Game Over! You lost after the player deck ran out of cards");
        EasyMock.expectLastCall();
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        this.bsc.initializePlayers();
        this.bsc.nextPlayerTurn();

        while (bsc.playerDeckSize() > 1) {
            assertTrue(bsc.drawTwoPlayerCards());
        }
        assertFalse(bsc.drawTwoPlayerCards());
        EasyMock.verify(gw);
    }

    @Test
    public void testEventCardsAddedToPlayerDeck() {
        Pandemic.bundle = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        ArrayList<City> cityMap = Pandemic.createMap();
        this.bsc = new BoardStatusController(new DummyGameWindow(), cityMap, NUM_PLAYERS);

        this.bsc.setup();
        assertEquals(48, this.bsc.playerDeckSize());

        this.bsc.startGame();
        assertEquals(49, this.bsc.playerDeckSize());

        ArrayList<String> playerDeckCardNames = new ArrayList<>();
        for (PlayerCard playerCard : this.bsc.playerDeck) {
            playerDeckCardNames.add(playerCard.getName());
        }
        for (EventName eventName : EventName.values()) {
            assertTrue(playerDeckCardNames.contains(eventName.name()));
        }
    }

    @Test
    public void testPlayEventCard() {
        ArrayList<City> cityMap = Pandemic.createMap();
        GameWindow mockedGameWindow = EasyMock.createMock(GameWindow.class);
        this.bsc = new BoardStatusController(mockedGameWindow, cityMap, NUM_PLAYERS);

        Medic medic = new Medic(atlanta);
        this.bsc.players = new Player[]{medic};
        this.bsc.currentPlayerTurn = 0;
        this.bsc.currentPlayerRemainingActions = 4;
        EventCard oneQuietNightCard = new EventCard(EventName.ONE_QUIET_NIGHT, this.bsc);
        medic.drawCard(oneQuietNightCard);

        EasyMock.expect(mockedGameWindow.promptSelectPlayerCard(anyObject(), anyObject(), anyObject())).andReturn(oneQuietNightCard);
        mockedGameWindow.repaintGameBoard();
        EasyMock.expectLastCall();
        EasyMock.replay(mockedGameWindow);

        assertEquals(1, medic.getCardsInHand().size());
        assertFalse(this.bsc.isQuietNight);

        this.bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);

        assertEquals(0, medic.getCardsInHand().size());
        assertTrue(this.bsc.isQuietNight);
        assertEquals(4, this.bsc.currentPlayerRemainingActions);
    }

    @Test
    public void testEpidemicIncreasesIndex(){
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        EasyMock.replay(gw);
        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.infectionDeck = new Stack<>();
//        Point blackPoint = new Point(0,0);
        City blackCity = new City("BlackCity",0, 0,CityColor.BLACK);
        bsc.infectionDeck.push(new InfectionCard(blackCity));
        bsc.initializePlayers();
        bsc.playerDeck = new Stack<>();
        bsc.playerDeck.push(new PlayerCard(true));
        bsc.playerDeck.push(new PlayerCard(atlanta));
        bsc.transferPlayToNextPlayer();
        bsc.drawTwoPlayerCards();
        assertEquals(1,bsc.infectionRateIndex);
    }

    @Test
    public void testEpidemicIncrementsInfectionRate() {
        createNewBSCWithTestMap(new DummyGameWindow());
        bsc.setup();
        bsc.epidemic();
        bsc.epidemic();
        bsc.epidemic();
        bsc.epidemic();
        assertEquals(3, bsc.getInfectionRate());
    }

    @Test
    public void testEpidemicAndEventCardsShuffledInAtGameStart() {
        Pandemic.bundle = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        bsc = new BoardStatusController(new DummyGameWindow(), Pandemic.createMap(), NUM_PLAYERS);
        bsc.setup();
        bsc.startGame();
        assertEquals(49, bsc.playerDeckSize());
    }

    @Test
    public void testDiseaseCubeBankInitialize() {
        createNewBSCWithTestMap(new DummyGameWindow());
        bsc.setup();
        int[] toCheck = bsc.diseaseCubesLeft();
        assertEquals(24, toCheck[0]);
        assertEquals(24, toCheck[1]);
        assertEquals(24, toCheck[2]);
        assertEquals(24, toCheck[3]);
    }


    @Test
    public void checkEradicationStatus() {
        GameWindowInterface gw = EasyMock.mock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        bsc.cureDisease(CityColor.YELLOW);
        bsc.handleAction(PlayerAction.SKIP_ACTION);
        assertEquals(DiseaseStatus.ERADICATED, bsc.getStatus(CityColor.YELLOW));
    }

    @Test
    public void twoColorsEradicated() {
        GameWindowInterface gw = EasyMock.mock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        bsc.cureDisease(CityColor.RED);
        bsc.cureDisease(CityColor.BLACK);
        bsc.handleAction(PlayerAction.SKIP_ACTION);
        assertEquals(DiseaseStatus.ERADICATED, bsc.getStatus(CityColor.RED));
        assertEquals(DiseaseStatus.ERADICATED, bsc.getStatus(CityColor.BLACK));
    }

    @Test
    public void infectEradicatedColor() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        OutbreakManager obm = EasyMock.niceMock(OutbreakManager.class);
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        createNewBSCWithTestMap(gw);
        bsc.cureDisease(CityColor.BLUE);
        bsc.handleAction(PlayerAction.SKIP_ACTION);
        assertEquals(DiseaseStatus.ERADICATED, bsc.getStatus(CityColor.BLUE));
        InfectionCard testCard = new InfectionCard(atlanta);
        testCard.cardDrawn(bsc.getStatus(CityColor.BLUE), dcb, obm);
        assertEquals(0, atlanta.getInfectionLevel(CityColor.BLUE));
    }

    @Test
    public void researchStationBuilt() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(chicago));
        EasyMock.replay(gw);
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        assertDoesNotThrow(() -> this.bsc.handleAction(PlayerAction.DRIVE_FERRY));
        assertEquals(3,this.bsc.currentPlayerRemainingActions);
        PlayerCard chicagoCard = new PlayerCard(chicago);
        Player currentPlayer = bsc.players[bsc.currentPlayerTurn];
        currentPlayer.drawCard(chicagoCard);
        bsc.handleAction(PlayerAction.BUILD_RESEARCH_STATION);

        EasyMock.verify(gw);
        assertEquals(2, this.bsc.currentPlayerRemainingActions);
        assertTrue(currentPlayer.getCity().hasResearchStation());
    }

    @Test
    public void testHandleViewCards() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        gw.displayPlayerCards(anyObject(),anyObject());
        EasyMock.expectLastCall();
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        bsc.handleAction(PlayerAction.VIEW_CARDS);
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleTreatDiseaseYellow() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.YELLOW);
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initFourGenericPlayers();
        bsc.nextPlayerTurn();
        dcb.infectCity(atlanta, CityColor.YELLOW,om);
        assertEquals(1,atlanta.getInfectionLevel(CityColor.YELLOW));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);

        EasyMock.verify(gw);
        assertEquals(0,atlanta.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testHandleTreatDiseaseBlue() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.BLUE);
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        dcb.infectCity(atlanta, CityColor.BLUE, om);
        assertEquals(1,atlanta.getInfectionLevel(CityColor.BLUE));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);

        EasyMock.verify(gw);
        assertEquals(0,atlanta.getInfectionLevel(CityColor.BLUE));
    }

    @Test
    public void testHandleTreatDiseaseRed() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.RED);
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initFourGenericPlayers();
        bsc.nextPlayerTurn();
        dcb.infectCity(atlanta, CityColor.RED,om);
        assertEquals(1,atlanta.getInfectionLevel(CityColor.RED));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);

        EasyMock.verify(gw);
        assertEquals(0,atlanta.getInfectionLevel(CityColor.RED));
    }

    @Test
    public void testHandleTreatDiseaseBlack() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.BLACK);
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initFourGenericPlayers();
        bsc.nextPlayerTurn();
        dcb.infectCity(atlanta, CityColor.BLACK,om);
        assertEquals(1,atlanta.getInfectionLevel(CityColor.BLACK));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);

        EasyMock.verify(gw);
        assertEquals(0,atlanta.getInfectionLevel(CityColor.BLACK));
    }

    @Test
    public void testGiveKnowledge() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        Player firstTestPlayer = new Medic(atlanta);
        Player secondTestPlayer = new Researcher(atlanta);
        PlayerCard atlantaCard = new PlayerCard(atlanta);


        EasyMock.expect(gw.promptSelectPlayer(anyObject(),anyObject(),anyObject())).andReturn(secondTestPlayer);
        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(),anyObject(),anyObject())).andReturn(atlantaCard);
        gw.displayNextPlayerInfo(anyObject(),anyInt());
        EasyMock.expectLastCall();
        EasyMock.replay(gw);


        bsc.players[0] = firstTestPlayer;
        bsc.players[1] = secondTestPlayer;
        firstTestPlayer.drawCard(atlantaCard);

        bsc.handleAction(PlayerAction.GIVE_KNOWLEDGE);
        assertTrue(secondTestPlayer.getCardsInHand().contains(atlantaCard));
        assertFalse(firstTestPlayer.getCardsInHand().contains(atlantaCard));}

    @Test
    public void testTakeKnowledge() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        Player firstTestPlayer = new Medic(atlanta);
        Player secondTestPlayer = new Researcher(atlanta);
        PlayerCard atlantaCard = new PlayerCard(atlanta);


        EasyMock.expect(gw.promptSelectPlayer(anyObject(),anyObject(),anyObject())).andReturn(secondTestPlayer);
        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(),anyObject(),anyObject())).andReturn(atlantaCard);
        EasyMock.replay(gw);


        bsc.players[0] = firstTestPlayer;
        bsc.players[1] = secondTestPlayer;
        secondTestPlayer.drawCard(atlantaCard);

        bsc.handleAction(PlayerAction.TAKE_KNOWLEDGE);
        assertTrue(firstTestPlayer.getCardsInHand().contains(atlantaCard));
        assertFalse(secondTestPlayer.getCardsInHand().contains(atlantaCard));
    }

    @Test
    public void testTakeKnowledgeFails() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();

        EasyMock.expect(gw.promptSelectPlayer(anyObject(),anyObject(),anyObject())).andReturn(null);
        EasyMock.replay(gw);


        assertThrows(ActionFailedException.class,bsc::handleTakeKnowledge);
    }

    @Test
    public void testLoseWhenOutOfCubes() {
        GameWindowInterface gw = EasyMock.strictMock(GameWindowInterface.class);
        gw.showGameOverMessage("Game Over! You lost after too many disease cubes were placed");
        EasyMock.expectLastCall();
        gw.displayNextPlayerInfo(anyObject(),anyInt());
        EasyMock.expectLastCall();
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initializePlayers();
        while(bsc.cubeBank.remainingCubes(CityColor.YELLOW) >= 0){
            bsc.cubeBank.cityInfected(CityColor.YELLOW);
        }
        bsc.nextPlayerTurn();

        EasyMock.verify(gw);

    }

    @Test
    public void testGetCityByName() {
        createNewBSCWithTestMap(new DummyGameWindow());
        assertThrows(RuntimeException.class, () -> bsc.getCityByName("bruh"));
    }

    @Test
    public void testCureDisease() {
        createNewBSCWithTestMap(new DummyGameWindow());
        testCureDiseaseHelper(CityColor.BLUE);
        testCureDiseaseHelper(CityColor.BLACK);
        testCureDiseaseHelper(CityColor.RED);
        testCureDiseaseHelper(CityColor.YELLOW);
    }

    public void testCureDiseaseHelper(CityColor color) {
        bsc.cureDisease(color);
        assertEquals(DiseaseStatus.CURED, bsc.getStatus(color));
    }

    @Test
    public void testHandleCharterFlight() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(miami));
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        PlayerCard atlantaCard = new PlayerCard(atlanta);
        bsc.players[0].drawCard(atlantaCard);
        assertTrue(bsc.players[0].getCardsInHand().contains(atlantaCard));
        assertDoesNotThrow(() -> bsc.handleAction(PlayerAction.CHARTER_FLIGHT));
        assertEquals(miami,bsc.players[0].getCity());
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleDirectFlight() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(miami));
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        assertEquals(atlanta,bsc.players[0].getCity());
        PlayerCard miamiCard = new PlayerCard(miami);
        bsc.players[0].drawCard(miamiCard);
        assertTrue(bsc.players[0].getCardsInHand().contains(miamiCard));
        assertDoesNotThrow(()->bsc.handleAction(PlayerAction.DIRECT_FLIGHT));
        assertEquals(miami,bsc.players[0].getCity());
        EasyMock.verify(gw);
    }

    @Test
    public void testhandleShuttleFlight() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(miami));
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        assertEquals(atlanta,bsc.players[0].getCity());
        miami.buildResearchStation();
        bsc.handleAction(PlayerAction.SHUTTLE_FLIGHT);
        assertEquals(miami,bsc.players[0].getCity());
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleContingencyPlannerRoleAction(){
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        EventCard oqn = new EventCard(EventName.ONE_QUIET_NIGHT,bsc);
        EasyMock.expect(gw.promptSelectOption(anyObject(),anyObject(), anyObject())).andReturn(oqn.getEventName());
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        bsc.playerDiscardPile = new Stack<>();
        bsc.playerDiscardPile.push(oqn);
        ContingencyPlanner contingencyPlanner = new ContingencyPlanner(atlanta,bsc);
        bsc.players[0] = contingencyPlanner;
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertTrue(contingencyPlanner.isHoldingEventCard);
        EasyMock.verify(gw);
    }

    @Test
    public void testDispatcherRoleAction() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        bsc.setup();
        bsc.initializePlayers();
        EasyMock.expect(gw.promptSelectPlayer(anyObject(),anyObject(), anyObject())).andReturn(bsc.players[1]);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(chicago));
        EasyMock.replay(gw);
        bsc.transferPlayToNextPlayer();
        Dispatcher dispatcher = new Dispatcher(atlanta);
        bsc.players[0] = dispatcher;
        assertEquals(atlanta,bsc.players[1].getCity());
        assertEquals(4,bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertEquals(chicago,bsc.players[1].getCity());
        assertEquals(3,bsc.currentPlayerRemainingActions);
    }

    @Test
    public void testOperationsExpertRoleAction() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        bsc.setup();
        bsc.initializePlayers();
        PlayerCard chicagoCard = new PlayerCard(chicago);
        EasyMock.expect(gw.promptSelectOption(anyObject(),anyObject(),anyObject())).andReturn(miami.name);
        EasyMock.expect(gw.promptSelectOption(anyObject(),anyObject(),anyObject())).andReturn(chicagoCard.getName());
        EasyMock.replay(gw);
        bsc.transferPlayToNextPlayer();
        OperationsExpert operationsExpert = new OperationsExpert(atlanta);
        bsc.players[0] = operationsExpert;
        operationsExpert.drawCard(chicagoCard);
        assertEquals(atlanta,bsc.players[0].getCity());
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertEquals(miami,bsc.players[0].getCity());
    }

    @Test
    public void testNoRoleAction() {
        createNewBSCWithTestMap(new DummyGameWindow());
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        Medic medic = new Medic(atlanta);
        bsc.players[0] = medic;
        assertEquals(4,bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertEquals(4,bsc.currentPlayerRemainingActions);
    }

    @Test
    public void handleCureDiseaseFail() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        createNewBSCWithTestMap(gw);
        bsc.setup();
        bsc.initializePlayers();
        PlayerCard chicagoCard = new PlayerCard(chicago);
        PlayerCard miamiCard = new PlayerCard(miami);
        PlayerCard atlantaCard = new PlayerCard(atlanta);
        PlayerCard montrealCard = new PlayerCard(montreal);
        PlayerCard washingtonCard = new PlayerCard(washington);
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn(miami.name);
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn(washington.name);
        EasyMock.replay(gw);
        bsc.transferPlayToNextPlayer();
        Medic medic = new Medic(atlanta);
        this.bsc.playerDiscardPile = new Stack<>();
        bsc.players[0] = medic;
        bsc.players[0].drawCard(chicagoCard);
        bsc.players[0].drawCard(montrealCard);
        bsc.players[0].drawCard(washingtonCard);
        bsc.players[0].drawCard(atlantaCard);
        bsc.players[0].drawCard(miamiCard);
        assertEquals(5,bsc.players[0].handSize());
        bsc.handleAction(PlayerAction.DISCOVER_CURE);
        assertEquals(5,bsc.players[0].handSize());
    }

    @Test
    public void testDisplayGame() {
        GameWindowInterface gw = EasyMock.strictMock(GameWindowInterface.class);
        bsc = new BoardStatusController(gw,Pandemic.createMap(), NUM_PLAYERS);
        gw.showWindow();
        EasyMock.expectLastCall();
        EasyMock.replay(gw);
        bsc.displayGame();
        EasyMock.verify(gw);
    }

    @Test
    public void testGameLossDueToOutbreaks() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        gw.showGameOverMessage("Game Over! You lost after 8 outbreaks");
        EasyMock.expectLastCall();
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        this.bsc.initializePlayers();
        while(bsc.outbreakManager.getOutbreaks() < 8){
            bsc.outbreakManager.incrementOutbreaks();
        }
        this.bsc.nextPlayerTurn();
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleTreatDiseaseNull() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(null);
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        assertEquals(4,bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.TREAT_DISEASE);
        assertEquals(4,bsc.currentPlayerRemainingActions);
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleTreatDiseaseCured() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.YELLOW);
        EasyMock.replay(gw);

        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.initFourGenericPlayers();
        bsc.nextPlayerTurn();
        dcb.infectCity(atlanta, CityColor.YELLOW,om);
        dcb.infectCity(atlanta, CityColor.YELLOW,om);
        bsc.cureDisease(CityColor.YELLOW);
        assertEquals(2,atlanta.getInfectionLevel(CityColor.YELLOW));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);
        EasyMock.verify(gw);
        assertEquals(0,atlanta.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testDoubleEpidemic() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        EasyMock.replay(gw);
        createNewBSCWithTestMap(gw);
        this.bsc.setup();
        bsc.infectionDeck = new Stack<>();
//        Point redPoint = new Point(0,0);
        City redCity = new City("RedCity", 0, 0,CityColor.RED);
        bsc.infectionDeck.push(new InfectionCard(miami));
        bsc.infectionDeck.push(new InfectionCard(atlanta));
        bsc.infectionDeck.push(new InfectionCard(redCity));
        bsc.initializePlayers();
        bsc.playerDeck = new Stack<>();
        bsc.playerDeck.push(new PlayerCard(true));
        bsc.playerDeck.push(new PlayerCard(true));
        bsc.transferPlayToNextPlayer();
        bsc.drawTwoPlayerCards();
        assertEquals(2,bsc.infectionRateIndex);
    }
}
