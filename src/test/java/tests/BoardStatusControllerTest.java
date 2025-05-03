package tests;

//import main.Point;
import main.*;
import main.roles.*;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class BoardStatusControllerTest {

    private BoardStatusController bsc;

    public void initFourGenericPlayers(BoardStatusController bsc, City city) {
        for (int i = 0; i < 4; i++) {
            Player newPlayer = new Player(Color.BLACK, city);
            newPlayer.name = "Test Player " + i;
            bsc.players[i] = newPlayer;
            city.players.add(newPlayer);
        }
        bsc.playersDrawStartingHands();
    }

    public CompletableFuture<City> generateTestFuture(City city) {
        CompletableFuture<City> cityFuture = new CompletableFuture<City>();
        cityFuture.complete(city);
        return cityFuture;
    }

    @Test
    public void testInitialInfectionRate() {
        this.bsc = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        assertEquals(2, this.bsc.getInfectionRate());
    }

    @Test
    public void testAdditionalInfectionRates() {
        this.bsc = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
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
        this.bsc = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withMap(Pandemic.createMap())
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        assertEquals(0, this.bsc.infectionDeck.size());
        this.bsc.setup();
        assertEquals(48, this.bsc.infectionDeck.size());
        assertEquals(0, this.bsc.infectionDiscardPile.size());
    }

    @Test
    public void testInfectionDeckAfterGameStart() {
        Pandemic.bundle = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        this.bsc = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withMap(Pandemic.createMap())
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        this.bsc.setup();
        this.bsc.startGame();
        assertEquals(39, this.bsc.infectionDeck.size());
        assertEquals(9, this.bsc.infectionDiscardPile.size());
    }

    @Test
    public void testAirlift() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        EventCard airlift = new EventCard(EventName.AIRLIFT,bsc);

        EasyMock.expect(gw.promptSelectPlayerCard(anyObject())).andReturn(airlift);
        EasyMock.expect(gw.promptSelectPlayer(anyObject())).andReturn(bsc.players[1]);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(testBSC.london));
        EasyMock.replay(gw);

        bsc.players[0].drawCard(airlift);
        assertTrue(bsc.players[0].getCardsInHand().contains(airlift));
        assertEquals(testBSC.atlanta, bsc.players[1].getCity());
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);

        assertEquals(testBSC.london, bsc.players[1].getCity());
        assertFalse(bsc.players[0].getCardsInHand().contains(airlift));
    }

    @Test
    public void testGovernmentGrant() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        EventCard governmentGrant = new EventCard(EventName.GOVERNMENT_GRANT,bsc);

        EasyMock.expect(gw.promptSelectPlayerCard(anyObject())).andReturn(governmentGrant);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(testBSC.chicago));
        EasyMock.replay(gw);

        assertEquals(4,bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);
        assertEquals(4,bsc.currentPlayerRemainingActions);
        assertFalse(testBSC.chicago.hasResearchStation());

        bsc.players[0].drawCard(governmentGrant);
        assertTrue(bsc.players[0].getCardsInHand().contains(governmentGrant));
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);
        assertTrue(testBSC.chicago.hasResearchStation());
        assertFalse(bsc.players[0].getCardsInHand().contains(governmentGrant));
    }

    @Test
    public void testEventCardToPlayNull() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        EventCard governmentGrant = new EventCard(EventName.GOVERNMENT_GRANT,bsc);

        EasyMock.expect(gw.promptSelectPlayerCard(anyObject())).andReturn(null);
        EasyMock.replay(gw);

        assertEquals(4,bsc.currentPlayerRemainingActions);
        assertFalse(testBSC.chicago.hasResearchStation());
        bsc.players[0].drawCard(governmentGrant);
        assertTrue(bsc.players[0].getCardsInHand().contains(governmentGrant));
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);
        assertEquals(4,bsc.currentPlayerRemainingActions);
        assertFalse(testBSC.chicago.hasResearchStation());
        assertTrue(bsc.players[0].getCardsInHand().contains(governmentGrant));
    }

    @Test
    public void testForecastNoRearranging() {
        GameWindow mockedGameWindow = EasyMock.niceMock(GameWindow.class);
        this.bsc = new TestBSC()
                .withGameWindow(mockedGameWindow)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        bsc.setup();

        initializeInfectionDeck();
        expectMockWindowCalls(mockedGameWindow);

        Player player = new Medic(new City("Atlanta", 0, 0, CityColor.BLUE));
        EventCard forecastCard = new EventCard(EventName.FORECAST, bsc);
        player.drawCard(forecastCard);
        assertTrue(player.getCardsInHand().contains(forecastCard));
        bsc.playEventCard(player, forecastCard);

        verifyForecastResults(player, forecastCard, mockedGameWindow);
    }

    private void initializeInfectionDeck() {
        Stack<InfectionCard> testInfectionDeck = new Stack<>();
        String[] cityNames = new String[]{"Washington", "Essen", "Delhi", "Chicago", "Baghdad", "Atlanta"};
        for (String cityName : cityNames) {
            City currentCity = new City(cityName, 0, 0, CityColor.BLUE);
            InfectionCard cardToAdd = new InfectionCard(currentCity);
            testInfectionDeck.push(cardToAdd);
        }
        bsc.infectionDeck = testInfectionDeck;
    }

    private void expectMockWindowCalls(GameWindow mockedGameWindow) {
        mockedGameWindow.displayInfectionCards(anyObject(), anyObject());
        EasyMock.expectLastCall();
        EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject())).andReturn("Put cards back on deck");
        mockedGameWindow.destroyCurrentInfectionCardsDialog();
        EasyMock.expectLastCall();

        EasyMock.replay(mockedGameWindow);
    }

    private void verifyForecastResults(Player player, EventCard forecastCard, GameWindowInterface mockedGameWindow) {
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
        this.bsc = new TestBSC()
                .withGameWindow(mockedGameWindow)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        bsc.setup();

        String[] cityNames = new String[]{"Atlanta", "Baghdad", "Chicago", "Delhi", "Essen", "Washington"};
        InfectionCard[] listOfInfectionCards = initializeInfectionDeckMultipleCards(cityNames);
        expectMockWindowCallsMultipleCards(cityNames, mockedGameWindow, listOfInfectionCards);

        Player player = new Medic(new City("Atlanta", 0, 0, CityColor.BLUE));
        EventCard forecastCard = new EventCard(EventName.FORECAST, bsc);
        player.drawCard(forecastCard);
        assertTrue(player.getCardsInHand().contains(forecastCard));
        bsc.playEventCard(player, forecastCard);

        verifyForecastResultsMultipleCalls(player, cityNames, forecastCard, mockedGameWindow);
    }

    private InfectionCard[] initializeInfectionDeckMultipleCards(String[] cityNames) {
        Stack<InfectionCard> testInfectionDeck = new Stack<>();
        Stack<InfectionCard> expectedInfectionDeck = new Stack<>();
        InfectionCard[] listOfInfectionCards = new InfectionCard[6];
        for (int i = 0; i < cityNames.length; i++) {
            City currentCity = new City(cityNames[i], 0, 0, CityColor.BLUE);
            InfectionCard cardToAdd = new InfectionCard(currentCity);
            testInfectionDeck.push(cardToAdd);
            expectedInfectionDeck.push(cardToAdd);
            listOfInfectionCards[i] = cardToAdd;
        }
        bsc.infectionDeck = testInfectionDeck;
        Collections.reverse(expectedInfectionDeck);
        return listOfInfectionCards;
    }

    private void expectMockWindowCallsMultipleCards(String[] cityNames, GameWindow mockedGameWindow, InfectionCard[] listOfInfectionCards) {
        for (int i = 0; i < 3; i++) {
            int highIndex = cityNames.length - 1 - i;
            EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject())).andReturn("Continue Rearranging");
            EasyMock.expect(mockedGameWindow.promptInfectionCard(anyObject())).andReturn(listOfInfectionCards[i]);
            EasyMock.expect(mockedGameWindow.promptInfectionCard(anyObject())).andReturn(listOfInfectionCards[highIndex]);
        }

        EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject())).andReturn("Put cards back on deck");

        for (int i = 0; i < cityNames.length / 2 + 1; i++) {
            mockedGameWindow.displayInfectionCards(anyObject(), anyObject());
            EasyMock.expectLastCall();
            mockedGameWindow.destroyCurrentInfectionCardsDialog();
            EasyMock.expectLastCall();
        }

        EasyMock.replay(mockedGameWindow);
    }

    private void verifyForecastResultsMultipleCalls(Player player, String[] cityNames, EventCard forecastCard, GameWindow mockedGameWindow) {
        assertFalse(player.getCardsInHand().contains(forecastCard));
        for (String cityName : cityNames) {
            assertEquals(cityName, bsc.infectionDeck.pop().city.name);
        }
        EasyMock.verify(mockedGameWindow);
    }

    @Test
    public void testForecastFiveCardsInDeck() {
        GameWindow mockedGameWindow = EasyMock.niceMock(GameWindow.class);
        this.bsc = new TestBSC()
                .withGameWindow(mockedGameWindow)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        bsc.setup();

        String[] testCityNames = new String[]{"Atlanta", "Baghdad", "Chicago", "Delhi", "Washington"};
        InfectionCard[] listOfInfectionCards = initializeInfectionDeckMultipleCards(testCityNames);

        EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject())).andReturn("Continue Rearranging");
        EasyMock.expect(mockedGameWindow.promptInfectionCard(anyObject())).andReturn(listOfInfectionCards[1]);
        EasyMock.expect(mockedGameWindow.promptInfectionCard(anyObject())).andReturn(listOfInfectionCards[4]);
        EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject())).andReturn("Put cards back on deck");

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

        String[] expectedCityNames = new String[]{"Baghdad", "Delhi", "Chicago", "Washington", "Atlanta"};
        verifyForecastResultsMultipleCalls(player, expectedCityNames, forecastCard, mockedGameWindow);
    }

    @Test
    public void testOneQuietNight() {
        TestBSC testBSC = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        InfectionCard cardToAdd = new InfectionCard(testBSC.atlanta);
        bsc.infectionDeck.add(cardToAdd);
        bsc.isQuietNight = true;
        bsc.infectCitiesBasedOnInfectionRate();
        assertEquals(0, testBSC.atlanta.getInfectionLevel(CityColor.BLUE));
    }

    @Test
    public void testResilientPopulation() {
        GameWindow mockedGameWindow = EasyMock.niceMock(GameWindow.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(mockedGameWindow)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        InfectionCard atlantaCard = new InfectionCard(testBSC.atlanta);
        InfectionCard chicagoCard = new InfectionCard(testBSC.chicago);
        InfectionCard washingtonCard = new InfectionCard(testBSC.washington);
        bsc.infectionDiscardPile.add(atlantaCard);
        bsc.infectionDiscardPile.add(chicagoCard);
        bsc.infectionDiscardPile.add(washingtonCard);

        EasyMock.expect(mockedGameWindow.promptInfectionCard(anyObject())).andReturn(atlantaCard);
        EasyMock.replay(mockedGameWindow);

        assertEquals(3, bsc.infectionDiscardPile.size());
        assertEquals(0, bsc.playerDiscardPile.size());
        assertTrue(bsc.infectionDiscardPile.contains(atlantaCard));

        EventCard resilientPopulationCard = new EventCard(EventName.RESILIENT_POPULATION, bsc);
        resilientPopulationCard.use();

        assertEquals(2, bsc.infectionDiscardPile.size());
        assertFalse(bsc.infectionDiscardPile.contains(atlantaCard));
        assertTrue(bsc.playerDiscardPile.contains(resilientPopulationCard));
        EasyMock.verify(mockedGameWindow);
    }

    @Test
    public void testDriveFerry() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(testBSC.chicago));
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(testBSC.montreal));
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(testBSC.newYork));
        EasyMock.replay(gw);

        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();

        this.bsc.handleAction(PlayerAction.DRIVE_FERRY);
        this.bsc.handleAction(PlayerAction.DRIVE_FERRY);
        this.bsc.handleAction(PlayerAction.DRIVE_FERRY);

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

        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        bsc.setup();

        Player player = new Medic(testBSC.atlanta);

        PlayerCard chicagoCard = new PlayerCard(testBSC.chicago);
        PlayerCard nyCard = new PlayerCard(testBSC.newYork);
        PlayerCard montrealCard = new PlayerCard(testBSC.montreal);
        PlayerCard sfCard = new PlayerCard(testBSC.sanFrancisco);
        PlayerCard washingtonCard = new PlayerCard(testBSC.washington);

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

        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        bsc.setup();

        Player player = new Scientist(testBSC.atlanta);

        PlayerCard chicagoCard = new PlayerCard(testBSC.chicago);
        PlayerCard nyCard = new PlayerCard(testBSC.newYork);
        PlayerCard montrealCard = new PlayerCard(testBSC.montreal);
        PlayerCard sfCard = new PlayerCard(testBSC.sanFrancisco);

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

        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        this.bsc.setup();
        this.bsc.initializePlayers();
        this.bsc.nextPlayerTurn();

        while (bsc.playerDeck.size() > 1) {
            assertTrue(bsc.drawTwoPlayerCards());
        }
        assertFalse(bsc.drawTwoPlayerCards());
        EasyMock.verify(gw);
    }

    @Test
    public void testEventCardsAddedToPlayerDeck() {
        TestBSC testBSC = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withMap(Pandemic.createMap())
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();

        this.bsc.setup();
        assertEquals(48, this.bsc.playerDeck.size());

        this.bsc.startGame();
        assertEquals(49, this.bsc.playerDeck.size());

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
        GameWindow mockedGameWindow = EasyMock.createMock(GameWindow.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(mockedGameWindow)
                .withMap(Pandemic.createMap())
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();

        Medic medic = new Medic(testBSC.atlanta);
        this.bsc.players = new Player[]{medic};
        this.bsc.currentPlayerTurn = 0;
        this.bsc.currentPlayerRemainingActions = 4;
        EventCard oneQuietNightCard = new EventCard(EventName.ONE_QUIET_NIGHT, this.bsc);
        medic.drawCard(oneQuietNightCard);

        EasyMock.expect(mockedGameWindow.promptSelectPlayerCard(anyObject())).andReturn(oneQuietNightCard);
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
        TestBSC testBSC = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        this.bsc.setup();
        bsc.infectionDeck = new Stack<>();
        City blackCity = new City("BlackCity",0, 0,CityColor.BLACK);
        bsc.infectionDeck.push(new InfectionCard(blackCity));
        bsc.initializePlayers();
        bsc.playerDeck = new Stack<>();
        bsc.playerDeck.push(new PlayerCard(true));
        bsc.playerDeck.push(new PlayerCard(testBSC.atlanta));
        bsc.transferPlayToNextPlayer();
        bsc.drawTwoPlayerCards();
        assertEquals(1,bsc.infectionRateIndex);
    }

    @Test
    public void testEpidemicIncrementsInfectionRate() {
        this.bsc = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
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
        this.bsc = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withMap(Pandemic.createMap())
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        bsc.setup();
        bsc.startGame();
        assertEquals(49, bsc.playerDeck.size());
    }

    @Test
    public void testDiseaseCubeBankInitialize() {
        this.bsc = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
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
        this.bsc = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        bsc.cureDisease(CityColor.YELLOW);
        bsc.handleAction(PlayerAction.SKIP_ACTION);
        assertEquals(DiseaseStatus.ERADICATED, bsc.getStatus(CityColor.YELLOW));
    }

    @Test
    public void twoColorsEradicated() {
        GameWindowInterface gw = EasyMock.mock(GameWindowInterface.class);
        this.bsc = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
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
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        bsc.cureDisease(CityColor.BLUE);
        bsc.handleAction(PlayerAction.SKIP_ACTION);
        assertEquals(DiseaseStatus.ERADICATED, bsc.getStatus(CityColor.BLUE));
        InfectionCard testCard = new InfectionCard(testBSC.atlanta);
        testCard.cardDrawn(bsc.getStatus(CityColor.BLUE), dcb, obm);
        assertEquals(0, testBSC.atlanta.getInfectionLevel(CityColor.BLUE));
    }

    @Test
    public void researchStationBuilt() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(testBSC.chicago));
        EasyMock.replay(gw);
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        assertDoesNotThrow(() -> this.bsc.handleAction(PlayerAction.DRIVE_FERRY));
        assertEquals(3,this.bsc.currentPlayerRemainingActions);
        PlayerCard chicagoCard = new PlayerCard(testBSC.chicago);
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

        this.bsc = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        bsc.handleAction(PlayerAction.VIEW_CARDS);
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleTreatDiseaseYellow() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        DiseaseCubeBank dcb = new DiseaseCubeBank();
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.YELLOW);
        EasyMock.replay(gw);

        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        this.bsc.setup();
        initFourGenericPlayers(bsc, testBSC.atlanta);
        bsc.currentPlayerTurn = 0;
        dcb.infectCity(testBSC.atlanta, CityColor.YELLOW,om);
        assertEquals(1, testBSC.atlanta.getInfectionLevel(CityColor.YELLOW));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);

        EasyMock.verify(gw);
        assertEquals(0, testBSC.atlanta.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testHandleTreatDiseaseBlue() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.BLUE);
        EasyMock.replay(gw);

        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        this.bsc.setup();
        initFourGenericPlayers(bsc, testBSC.atlanta);

        bsc.currentPlayerTurn = 0;
        dcb.infectCity(testBSC.atlanta, CityColor.BLUE, om);
        assertEquals(1, testBSC.atlanta.getInfectionLevel(CityColor.BLUE));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);

        EasyMock.verify(gw);
        assertEquals(0, testBSC.atlanta.getInfectionLevel(CityColor.BLUE));
    }

    @Test
    public void testHandleTreatDiseaseRed() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        DiseaseCubeBank dcb = new DiseaseCubeBank();
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.RED);
        EasyMock.replay(gw);

        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        this.bsc.setup();
        initFourGenericPlayers(bsc, testBSC.atlanta);
        bsc.currentPlayerTurn = 0;
        dcb.infectCity(testBSC.atlanta, CityColor.RED,om);
        assertEquals(1, testBSC.atlanta.getInfectionLevel(CityColor.RED));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);

        EasyMock.verify(gw);
        assertEquals(0, testBSC.atlanta.getInfectionLevel(CityColor.RED));
    }

    @Test
    public void testHandleTreatDiseaseBlack() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        DiseaseCubeBank dcb = new DiseaseCubeBank();
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.BLACK);
        EasyMock.replay(gw);

        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        this.bsc.setup();
        initFourGenericPlayers(bsc, testBSC.atlanta);
        bsc.currentPlayerTurn = 0;
        dcb.infectCity(testBSC.atlanta, CityColor.BLACK,om);
        assertEquals(1, testBSC.atlanta.getInfectionLevel(CityColor.BLACK));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);

        EasyMock.verify(gw);
        assertEquals(0, testBSC.atlanta.getInfectionLevel(CityColor.BLACK));
    }

    @Test
    public void testGiveKnowledge() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        Player firstTestPlayer = new Medic(testBSC.atlanta);
        Player secondTestPlayer = new Researcher(testBSC.atlanta);
        PlayerCard atlantaCard = new PlayerCard(testBSC.atlanta);


        EasyMock.expect(gw.promptSelectPlayer(anyObject())).andReturn(secondTestPlayer);
        EasyMock.expect(gw.promptSelectPlayerCard(anyObject())).andReturn(atlantaCard);
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
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        Player firstTestPlayer = new Medic(testBSC.atlanta);
        Player secondTestPlayer = new Researcher(testBSC.atlanta);
        PlayerCard atlantaCard = new PlayerCard(testBSC.atlanta);


        EasyMock.expect(gw.promptSelectPlayer(anyObject())).andReturn(secondTestPlayer);
        EasyMock.expect(gw.promptSelectPlayerCard(anyObject())).andReturn(atlantaCard);
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
        this.bsc = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        this.bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();

        EasyMock.expect(gw.promptSelectPlayer(anyObject())).andReturn(null);
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

        this.bsc = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
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
        this.bsc = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
        assertThrows(RuntimeException.class, () -> bsc.getCityByName("bruh"));
    }

    @Test
    public void testCureDisease() {
        this.bsc = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
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
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(testBSC.miami));
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        PlayerCard atlantaCard = new PlayerCard(testBSC.atlanta);
        bsc.players[0].drawCard(atlantaCard);
        assertTrue(bsc.players[0].getCardsInHand().contains(atlantaCard));
        assertDoesNotThrow(() -> bsc.handleAction(PlayerAction.CHARTER_FLIGHT));
        assertEquals(testBSC.miami,bsc.players[0].getCity());
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleDirectFlight() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(testBSC.miami));
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        assertEquals(testBSC.atlanta,bsc.players[0].getCity());
        PlayerCard miamiCard = new PlayerCard(testBSC.miami);
        bsc.players[0].drawCard(miamiCard);
        assertTrue(bsc.players[0].getCardsInHand().contains(miamiCard));
        assertDoesNotThrow(()->bsc.handleAction(PlayerAction.DIRECT_FLIGHT));
        assertEquals(testBSC.miami, bsc.players[0].getCity());
        EasyMock.verify(gw);
    }

    @Test
    public void testhandleShuttleFlight() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(testBSC.miami));
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        assertEquals(testBSC.atlanta,bsc.players[0].getCity());
        testBSC.miami.buildResearchStation();
        bsc.handleAction(PlayerAction.SHUTTLE_FLIGHT);
        assertEquals(testBSC.miami,bsc.players[0].getCity());
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleContingencyPlannerRoleAction(){
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        EventCard oqn = new EventCard(EventName.ONE_QUIET_NIGHT,bsc);
        EasyMock.expect(gw.promptSelectOption(anyObject())).andReturn(oqn.getEventName());
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        bsc.playerDiscardPile = new Stack<>();
        bsc.playerDiscardPile.push(oqn);
        ContingencyPlanner contingencyPlanner = new ContingencyPlanner(testBSC.atlanta, bsc);
        bsc.players[0] = contingencyPlanner;
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertTrue(contingencyPlanner.isHoldingEventCard);
        EasyMock.verify(gw);
    }

    @Test
    public void testDispatcherRoleAction() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        bsc.setup();
        bsc.initializePlayers();
        EasyMock.expect(gw.promptSelectPlayer(anyObject())).andReturn(bsc.players[1]);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(testBSC.chicago));
        EasyMock.replay(gw);
        bsc.transferPlayToNextPlayer();
        Dispatcher dispatcher = new Dispatcher(testBSC.atlanta);
        bsc.players[0] = dispatcher;
        assertEquals(testBSC.atlanta,bsc.players[1].getCity());
        assertEquals(4,bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertEquals(testBSC.chicago,bsc.players[1].getCity());
        assertEquals(3,bsc.currentPlayerRemainingActions);
    }

    @Test
    public void testOperationsExpertRoleAction() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        bsc.setup();
        bsc.initializePlayers();
        PlayerCard chicagoCard = new PlayerCard(testBSC.chicago);
        EasyMock.expect(gw.promptSelectOption(anyObject())).andReturn(testBSC.miami.name);
        EasyMock.expect(gw.promptSelectOption(anyObject())).andReturn(chicagoCard.getName());
        EasyMock.replay(gw);
        bsc.transferPlayToNextPlayer();
        OperationsExpert operationsExpert = new OperationsExpert(testBSC.atlanta);
        bsc.players[0] = operationsExpert;
        operationsExpert.drawCard(chicagoCard);
        assertEquals(testBSC.atlanta,bsc.players[0].getCity());
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertEquals(testBSC.miami,bsc.players[0].getCity());
    }

    @Test
    public void testNoRoleAction() {
        TestBSC testBSC = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        Medic medic = new Medic(testBSC.atlanta);
        bsc.players[0] = medic;
        assertEquals(4,bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertEquals(4,bsc.currentPlayerRemainingActions);
    }

    @Test
    public void handleCureDiseaseFail() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        bsc.setup();
        bsc.initializePlayers();
        PlayerCard chicagoCard = new PlayerCard(testBSC.chicago);
        PlayerCard miamiCard = new PlayerCard(testBSC.miami);
        PlayerCard atlantaCard = new PlayerCard(testBSC.atlanta);
        PlayerCard montrealCard = new PlayerCard(testBSC.montreal);
        PlayerCard washingtonCard = new PlayerCard(testBSC.washington);
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn(testBSC.miami.name);
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn(testBSC.washington.name);
        EasyMock.replay(gw);
        bsc.transferPlayToNextPlayer();
        Medic medic = new Medic(testBSC.atlanta);
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
        this.bsc = new TestBSC()
                .withGameWindow(gw)
                .withMap(Pandemic.createMap())
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
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

        this.bsc = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
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

        this.bsc = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4)
                .build();
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
        DiseaseCubeBank dcb = new DiseaseCubeBank();
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.YELLOW);
        EasyMock.replay(gw);

        TestBSC testBSC = new TestBSC()
                .withGameWindow(gw)
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        this.bsc.setup();
        initFourGenericPlayers(bsc, testBSC.atlanta);
        bsc.nextPlayerTurn();
        dcb.infectCity(testBSC.atlanta, CityColor.YELLOW,om);
        dcb.infectCity(testBSC.atlanta, CityColor.YELLOW,om);
        bsc.cureDisease(CityColor.YELLOW);
        assertEquals(2, testBSC.atlanta.getInfectionLevel(CityColor.YELLOW));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);
        EasyMock.verify(gw);
        assertEquals(0, testBSC.atlanta.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testDoubleEpidemic() {
        GameWindowInterface gw = EasyMock.niceMock(GameWindowInterface.class);
        EasyMock.replay(gw);
        TestBSC testBSC = new TestBSC()
                .withGameWindow(new DummyGameWindow())
                .withDefaultMap()
                .withPlayers(4)
                .withEpidemicCards(4);
        this.bsc = testBSC.build();
        this.bsc.setup();
        bsc.infectionDeck = new Stack<>();
        City redCity = new City("RedCity", 0, 0,CityColor.RED);
        bsc.infectionDeck.push(new InfectionCard(testBSC.miami));
        bsc.infectionDeck.push(new InfectionCard(testBSC.atlanta));
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
