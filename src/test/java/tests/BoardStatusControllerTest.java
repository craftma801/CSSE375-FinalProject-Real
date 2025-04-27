package tests;

import main.*;
import main.roles.*;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.junit.jupiter.api.Assertions.*;

public abstract class BoardStatusControllerTest {
    final int NUM_PLAYERS = 4;
    final int NUM_EPIDEMIC_CARDS = 4;
    final City chicago = new City("Chicago", 0, 0, CityColor.BLUE);
    final City atlanta = new City("Atlanta", 0, 0, CityColor.BLUE);
    final City london = new City("London", 0, 0, CityColor.BLUE);
    final City newYork = new City("New York", 0, 0, CityColor.BLUE);
    final City sanFrancisco = new City("San Francisco", 0, 0, CityColor.BLUE);
    final City montreal = new City("Montreal", 0, 0, CityColor.BLUE);
    final City washington = new City("Washington", 0, 0, CityColor.BLUE);
    final City miami = new City("Miami", 0, 0, CityColor.YELLOW);
    public BoardStatusController bsc;
    public GameWindowInterface gw;

    @BeforeEach
    public void setup() {
        gw = EasyMock.niceMock(GameWindowInterface.class);
        bsc = createTestBoard(gw);
    }

     BoardStatusController createTestBoard(GameWindowInterface gw) {
        ArrayList<City> cities = new ArrayList<>(Arrays.asList(chicago, atlanta, london, newYork, sanFrancisco, montreal, washington, miami));

        connect(chicago, atlanta, montreal);
        connect(montreal, newYork);
        connect(atlanta, washington, miami);
        connect(washington, newYork, miami);
        connect(sanFrancisco, chicago);
        connect(london, newYork);

        return new BoardStatusController(gw, new ArrayList<>(cities), NUM_PLAYERS, NUM_EPIDEMIC_CARDS);
    }

     void connect(City from, City... toCities) {
        for (City to : toCities) {
            from.addConnection(to);
            to.addConnection(from);
        }
    }

     CompletableFuture<City> generateTestFuture(City city) {
        CompletableFuture<City> future = new CompletableFuture<>();
        future.complete(city);
        return future;
    }

    @Test
    public void testInitialInfectionRate() {
        assertEquals(2, bsc.getInfectionRate());
    }

    @Test
    public void testAdditionalInfectionRates() {
        assertEquals(2, bsc.getInfectionRate());
        bsc.increaseInfectionRate();
        bsc.increaseInfectionRate();
        assertEquals(2, bsc.getInfectionRate());
        bsc.increaseInfectionRate();
        assertEquals(3, bsc.getInfectionRate());
        bsc.increaseInfectionRate();
        assertEquals(3, bsc.getInfectionRate());
        bsc.increaseInfectionRate();
        assertEquals(4, bsc.getInfectionRate());
        bsc.increaseInfectionRate();
        assertEquals(4, bsc.getInfectionRate());
        bsc.increaseInfectionRate();
        assertEquals(4, bsc.getInfectionRate());
    }

    @Test
    public void testInitializeInfectionDeck() {
        assertEquals(0, bsc.infectionDeckSize());
        bsc.setup();
        assertEquals(8, bsc.infectionDeckSize());
        assertEquals(0, bsc.infectionDiscardPileSize());
    }

    @Test
    public void testInfectionDeckAfterGameStart() {
        bsc = new BoardStatusController(gw, Pandemic.createMap(), 4, 4);
        bsc.setup();

        bsc.startGame();

        assertEquals(39, bsc.infectionDeckSize());
        assertEquals(9, bsc.infectionDiscardPileSize());
    }


    @Test
    public void testForecastNoRearranging() {
        bsc.setup();
        Stack<InfectionCard> testInfectionDeck = new Stack<>();
        String[] cityNames = new String[]{"Washington", "Essen", "Delhi", "Chicago", "Baghdad", "Atlanta"};
        for (String cityName : cityNames) {
            City currentCity = new City(cityName, 0, 0, CityColor.BLUE);
            InfectionCard cardToAdd = new InfectionCard(currentCity);
            testInfectionDeck.push(cardToAdd);
        }
        bsc.infectionDeck = testInfectionDeck;

        gw.displayInfectionCards(anyObject(), anyObject());
        EasyMock.expectLastCall();
        EasyMock.expect(gw.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Put cards back on deck");
        gw.destroyCurrentInfectionCardsDialog();
        EasyMock.expectLastCall();

        EasyMock.replay(gw);

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
        EasyMock.verify(gw);
    }

    @Test
    public void testForecastReverseTopSixCards() {
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
            EasyMock.expect(gw.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Continue Rearranging");
            EasyMock.expect(gw.promptInfectionCard(anyObject(), anyObject(), anyObject())).andReturn(listOfInfectionCards[i]);
            EasyMock.expect(gw.promptInfectionCard(anyObject(), anyObject(), anyObject())).andReturn(listOfInfectionCards[highIndex]);
        }

        EasyMock.expect(gw.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Put cards back on deck");

        for (int i = 0; i < cityNames.length / 2 + 1; i++) {
            gw.displayInfectionCards(anyObject(), anyObject());
            EasyMock.expectLastCall();
            gw.destroyCurrentInfectionCardsDialog();
            EasyMock.expectLastCall();
        }

        EasyMock.replay(gw);

        Player player = new Medic(new City("Atlanta", 0, 0, CityColor.BLUE));
        EventCard forecastCard = new EventCard(EventName.FORECAST, bsc);

        player.drawCard(forecastCard);
        assertTrue(player.getCardsInHand().contains(forecastCard));

        bsc.playEventCard(player, forecastCard);

        assertFalse(player.getCardsInHand().contains(forecastCard));
        for (String cityName : cityNames) {
            assertEquals(cityName, bsc.infectionDeck.pop().city.name);
        }
        EasyMock.verify(gw);
    }

    @Test
    public void testForecastFiveCardsInDeck() {
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

        EasyMock.expect(gw.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Continue Rearranging");
        EasyMock.expect(gw.promptInfectionCard(anyObject(), anyObject(), anyObject())).andReturn(listOfInfectionCards[1]);
        EasyMock.expect(gw.promptInfectionCard(anyObject(), anyObject(), anyObject())).andReturn(listOfInfectionCards[4]);
        EasyMock.expect(gw.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Put cards back on deck");

        gw.displayInfectionCards(anyObject(), anyObject());
        EasyMock.expectLastCall();
        gw.destroyCurrentInfectionCardsDialog();
        EasyMock.expectLastCall();
        gw.displayInfectionCards(anyObject(), anyObject());
        EasyMock.expectLastCall();
        gw.destroyCurrentInfectionCardsDialog();
        EasyMock.expectLastCall();

        EasyMock.replay(gw);

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
        EasyMock.verify(gw);
    }

    @Test
    public void testOneQuietNight() {

        bsc.addToInfectionDeck(atlanta);
        bsc.oneQuietNight();
        bsc.infectCitiesBasedOnInfectionRate();
        assertEquals(0, bsc.getCityInfectionLevel("Atlanta", CityColor.BLUE));
    }

    @Test
    public void testResilientPopulation() {
        InfectionCard atlantaCard = new InfectionCard(atlanta);
        InfectionCard chicagoCard = new InfectionCard(chicago);
        InfectionCard washingtonCard = new InfectionCard(washington);
        bsc.addToInfectionDiscardPile(atlantaCard);
        bsc.addToInfectionDiscardPile(chicagoCard);
        bsc.addToInfectionDiscardPile(washingtonCard);

        EasyMock.expect(gw.promptInfectionCard(anyObject(), anyObject(), anyObject())).andReturn(atlantaCard);
        EasyMock.replay(gw);

        assertEquals(3, bsc.infectionDiscardPileSize());
        assertEquals(0, bsc.playerDiscardPile.size());
        assertTrue(bsc.infectionDiscardPile.contains(atlantaCard));

        EventCard resilientPopulationCard = new EventCard(EventName.RESILIENT_POPULATION, bsc);
        resilientPopulationCard.use();

        assertEquals(2, bsc.infectionDiscardPileSize());
        assertFalse(bsc.infectionDiscardPile.contains(atlantaCard));
        assertTrue(bsc.playerDiscardPile.contains(resilientPopulationCard));
        EasyMock.verify(gw);
    }

    

    @Test
    public void testDiscoverCure() {
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("Chicago");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("New York");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("San Francisco");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("Montreal");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("Washington");
        EasyMock.replay(gw);
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
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("Chicago");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("New York");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("San Francisco");
        EasyMock.expect(gw.promptCureCards(anyObject())).andReturn("Montreal");
        EasyMock.replay(gw);

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
        gw.showGameOverMessage("Game Over! You lost after the player deck ran out of cards");
        EasyMock.expectLastCall();
        EasyMock.replay(gw);

        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();

        while (bsc.playerDeckSize() > 1) {
            assertTrue(bsc.drawTwoPlayerCards());
        }
        assertFalse(bsc.drawTwoPlayerCards());
        EasyMock.verify(gw);
    }

    @Test
    public void testEventCardsAddedToPlayerDeck() {
        Pandemic.bundle = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        bsc = new BoardStatusController(gw, Pandemic.createMap(), 4, 4);

        bsc.setup();
        bsc.startGame();

        ArrayList<String> playerDeckCardNames = new ArrayList<>();
        for (PlayerCard playerCard : bsc.playerDeck) {
            playerDeckCardNames.add(playerCard.getName());
        }
        for (EventName eventName : EventName.values()) {
            assertTrue(playerDeckCardNames.contains(eventName.name()));
        }
    }



    @Test
    public void testEpidemicIncreasesIndex() {
        bsc.setup();
        bsc.infectionDeck = new Stack<>();
        City blackCity = new City("BlackCity", 0, 0, CityColor.BLACK);
        bsc.infectionDeck.push(new InfectionCard(blackCity));
        bsc.initializePlayers();
        bsc.playerDeck = new Stack<>();
        bsc.playerDeck.push(new PlayerCard(true));
        bsc.playerDeck.push(new PlayerCard(atlanta));
        bsc.transferPlayToNextPlayer();
        bsc.drawTwoPlayerCards();
        assertEquals(1, bsc.infectionRateIndex);
    }

    @Test
    public void testEpidemicIncrementsInfectionRate() {
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
        bsc = new BoardStatusController(new DummyGameWindow(), Pandemic.createMap(), NUM_PLAYERS, NUM_EPIDEMIC_CARDS);
        bsc.setup();
        bsc.startGame();
        assertEquals(49, bsc.playerDeckSize());
    }

    @Test
    public void testDiseaseCubeBankInitialize() {
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
        bsc.cureDisease(CityColor.YELLOW);
        bsc.handleAction(PlayerAction.SKIP_ACTION);
        assertEquals(DiseaseStatus.ERADICATED, bsc.getStatus(CityColor.YELLOW));
    }
    
    @Test
    public void twoColorsEradicated() {
        GameWindowInterface gw = EasyMock.mock(GameWindowInterface.class);
        bsc.cureDisease(CityColor.RED);
        bsc.cureDisease(CityColor.BLACK);
        bsc.handleAction(PlayerAction.SKIP_ACTION);
        assertEquals(DiseaseStatus.ERADICATED, bsc.getStatus(CityColor.RED));
        assertEquals(DiseaseStatus.ERADICATED, bsc.getStatus(CityColor.BLACK));
    }

    @Test
    public void testTakeKnowledgeFails() {
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        EasyMock.expect(gw.promptSelectPlayer(anyObject(), anyObject(), anyObject())).andReturn(null);
        EasyMock.replay(gw);
        assertThrows(ActionFailedException.class, bsc::handleTakeKnowledge);
    }

    @Test
    public void testLoseWhenOutOfCubes() {
        gw.showGameOverMessage("Game Over! You lost after too many disease cubes were placed");
        EasyMock.expectLastCall();
        gw.displayNextPlayerInfo(anyObject(), anyInt());
        EasyMock.expectLastCall();
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        while (bsc.cubeBank.remainingCubes(CityColor.YELLOW) >= 0) {
            bsc.cubeBank.cityInfected(CityColor.YELLOW);
        }
        bsc.nextPlayerTurn();
        EasyMock.verify(gw);
    }

    @Test
    public void testGetCityByName() {
        assertThrows(RuntimeException.class, () -> bsc.getCityByName("bruh"));
    }

    @Test
    public void testCureDisease() {
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
    public void testDisplayGame() {
        bsc = new BoardStatusController(gw, Pandemic.createMap(), NUM_PLAYERS, NUM_EPIDEMIC_CARDS);
        gw.showWindow();
        EasyMock.expectLastCall();
        EasyMock.replay(gw);
        bsc.displayGame();
        EasyMock.verify(gw);
    }

    @Test
    public void testGameLossDueToOutbreaks() {
        gw.showGameOverMessage("Game Over! You lost after 8 outbreaks");
        EasyMock.expectLastCall();
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        while (bsc.outbreakManager.getOutbreaks() < 8) {
            bsc.outbreakManager.incrementOutbreaks();
        }
        bsc.nextPlayerTurn();
        EasyMock.verify(gw);
    }

    @Test
    public void testDoubleEpidemic() {
        bsc.setup();
        bsc.initializePlayers();
        bsc.playerDeck = new Stack<>();
        bsc.playerDeck.push(new PlayerCard(true));
        bsc.playerDeck.push(new PlayerCard(true));
        bsc.transferPlayToNextPlayer();
        bsc.drawTwoPlayerCards();
        assertEquals(2, bsc.infectionRateIndex);
    }
}
