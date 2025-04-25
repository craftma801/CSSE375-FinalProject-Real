package tests;

//import main.Point;
import main.*;
import main.roles.*;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;

import static org.easymock.EasyMock.anyObject;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    public static GameWindow mockedGameWindow;
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
    ArrayList<City> basicMap;

    private final int NUM_PLAYERS = 4;
    private final int NUM_EPIDEMIC_CARDS = 4;

    //Give each test a fresh map
    @BeforeEach
    public void setupTestMap() {
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

        basicMap = new ArrayList<>();

        basicMap.add(chicago);
        basicMap.add(atlanta);
        basicMap.add(london);
        basicMap.add(newYork);
        basicMap.add(sanFrancisco);
        basicMap.add(montreal);
        basicMap.add(washington);
        basicMap.add(madrid);
        basicMap.add(miami);
    }

    private void setupBSC(GameWindowInterface gameWindow) {
        this.bsc = new BoardStatusController(gameWindow, basicMap, NUM_PLAYERS, NUM_EPIDEMIC_CARDS);
    }

    @Test
    public void testGetCity() {
        Player researcher = new Researcher(miami);
        assertEquals(miami, researcher.getCity());
    }

    @Test
    public void testPlayerNames() {
        Pandemic.bundle = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        bsc = new BoardStatusController(new DummyGameWindow(), Pandemic.createMap(), NUM_PLAYERS, NUM_EPIDEMIC_CARDS);
        Player player = new Researcher(miami);
        player.name = bsc.generatePlayerName(1,player);
        assertEquals("Player 1 (Researcher)", player.name);
    }

    @Test
    public void testNonMedicTreatDisease() {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        OutbreakManager outbreakManager = new OutbreakManager(new DummyGameWindow());
        miami.infect(CityColor.YELLOW, diseaseCubeBank, outbreakManager);
        miami.infect(CityColor.YELLOW, diseaseCubeBank, outbreakManager);

        Player testPlayer = new Researcher(miami);
        testPlayer.treatDisease(CityColor.YELLOW, diseaseCubeBank);

        assertEquals(1, miami.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testMedicTreatDisease() {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        OutbreakManager outbreakManager = new OutbreakManager(new DummyGameWindow());
        miami.infect(CityColor.YELLOW, diseaseCubeBank, outbreakManager);
        miami.infect(CityColor.YELLOW, diseaseCubeBank, outbreakManager);

        Player testPlayer = new Medic(miami);
        testPlayer.treatDisease(CityColor.YELLOW, diseaseCubeBank);

        assertEquals(0, miami.getInfectionLevel(CityColor.YELLOW));

        assertThrows(ActionFailedException.class, () -> testPlayer.treatDisease(CityColor.YELLOW, diseaseCubeBank));
    }

    @Test
    public void testBuildResearchStation() {
        PlayerCard testCard = new PlayerCard(miami);
        Player testPlayer = new Medic(miami);

        assertFalse(miami.hasResearchStation());
        assertThrows(ActionFailedException.class, testPlayer::buildResearchStation);
        assertFalse(miami.hasResearchStation());

        testPlayer.drawCard(testCard);
        testPlayer.buildResearchStation();
        assertTrue(miami.hasResearchStation());
    }

    @Test
    public void testMove() {
        setupBSC(new DummyGameWindow());
        Player testPlayer = new Medic(chicago);
        testPlayer.move(atlanta);
        assertEquals(atlanta, testPlayer.getCity());
        assertThrows(ActionFailedException.class, () -> testPlayer.move(madrid));
        assertEquals(atlanta, testPlayer.getCity());
    }

    @Test
    public void testDirectFlight() {
        PlayerCard testCard = new PlayerCard(chicago);
        Player testPlayer = new Researcher(london);
        testPlayer.drawCard(testCard);
        testPlayer.directFlight(chicago);
        assertEquals(chicago, testPlayer.getCity());
    }

    @Test
    public void testCharterFlight() {
        PlayerCard testCard = new PlayerCard(chicago);
        Player testPlayer = new Researcher(chicago);
        testPlayer.drawCard(testCard);
        testPlayer.charterFlight(london);
        assertEquals(london, testPlayer.getCity());
    }

    @Test
    public void testShuttleFlight() {
        Player testPlayer = new Researcher(london);
        chicago.buildResearchStation();
        testPlayer.shuttleFlight(chicago);
        assertEquals(chicago, testPlayer.getCity());
    }

    @Test
    public void testShareKnowledgeTake() {
        Player testPlayer1 = new Medic(miami);
        Player testPlayer2 = new Researcher(miami);
        PlayerCard testCard = new PlayerCard(miami);
        testPlayer1.drawCard(testCard);
        testPlayer2.shareKnowledgeTake(testPlayer1, testCard);
        assertEquals(1, testPlayer2.handSize());
        assertEquals(0, testPlayer1.handSize());
    }

    @Test
    public void testShareKnowledgeGive() {
        Player testPlayer1 = new Medic(miami);
        Player testPlayer2 = new Researcher(miami);
        PlayerCard testCard = new PlayerCard(miami);
        testPlayer1.drawCard(testCard);
        testPlayer1.shareKnowledgeGive(testPlayer2, testCard);
        assertEquals(0, testPlayer1.handSize());
        assertEquals(1, testPlayer2.handSize());
    }

    @Test
    public void testGetCardNames() {
        Player testPlayer1 = new Medic(miami);
        PlayerCard testCard = new PlayerCard(miami);
        testPlayer1.drawCard(testCard);
        ArrayList<String> cardNames = testPlayer1.getCardNames();
        assertEquals(1, cardNames.size());
    }

    @Test
    public void testBuildResearchStationAsOperationsExpert() {
        Player testPlayer = new OperationsExpert(miami);
        assertEquals(0, testPlayer.handSize());
        testPlayer.buildResearchStation();
        assertTrue(miami.hasResearchStation());
        assertThrows(ActionFailedException.class, testPlayer::buildResearchStation);
    }

    @Test
    public void testOperationsExpertRoleAction() {
        atlanta.buildResearchStation();
        OperationsExpert testPlayer = new OperationsExpert(atlanta);
        PlayerCard testCard = new PlayerCard(chicago);
        testPlayer.drawCard(testCard);
        testPlayer.operationsExpertAction(london, "Chicago");
        assertEquals(0, testPlayer.handSize());
        assertEquals(0, atlanta.players.size());
        assertEquals(london, testPlayer.getCity());
        assertEquals(1, london.players.size());
    }

    @Test
    public void testOperationsExpertRoleActionFail() {
        atlanta.buildResearchStation();
        OperationsExpert testPlayer = new OperationsExpert(chicago);
        PlayerCard testCard = new PlayerCard(chicago);
        testPlayer.drawCard(testCard);
        testPlayer.operationsExpertAction(london, "Chicago");
        assertEquals(chicago, testPlayer.getCity());
        assertEquals(0, london.players.size());
        assertEquals(1, testPlayer.handSize());
    }

    @Test
    public void testMovedByDispatcher() {
        Player testPlayer = new Dispatcher(chicago);
        Player testPlayer2 = new Scientist(london);
        testPlayer.forceRelocatePlayer(chicago);
        testPlayer2.forceRelocatePlayer(london);
        assertEquals(testPlayer.getCity(), chicago);
        assertEquals(testPlayer2.getCity(), london);
        testPlayer.forceRelocatePlayer(london);
        assertEquals(testPlayer.getCity(), london);
    }

    @Test
    public void testDrawCards() {
        Player player = new Medic(atlanta);
        PlayerCard testCard = new PlayerCard(atlanta);

        assertEquals(0, player.handSize());

        player.drawCard(testCard);
        assertEquals(1, player.handSize());

        for (int i = 0; i < 6; i++) {
            player.drawCard(testCard);
        }
        assertEquals(7, player.handSize());
    }

    @Test
    public void testDrawEpidemicCard() {
        Player player = new Medic(atlanta);

        PlayerCard regularPlayerCard = new PlayerCard(atlanta);
        PlayerCard epidemicCard = new PlayerCard(true);

        assertEquals(0, player.handSize());

        player.drawCard(regularPlayerCard);
        assertEquals(1, player.handSize());

        player.drawCard(epidemicCard);
        assertEquals(1, player.handSize());
    }

    @Test
    public void testDrawCardWhenHandFull() {
        mockedGameWindow = EasyMock.createMock(GameWindow.class);
        EasyMock.expect(mockedGameWindow.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Atlanta");
        EasyMock.replay(mockedGameWindow);

        setupBSC(mockedGameWindow);
        PlayerCard atlantaCard = new PlayerCard(atlanta);
        PlayerCard chicagoCard = new PlayerCard(chicago);
        Player player = new Medic(atlanta);

        assertEquals(0, player.handSize());
        for (int i = 0; i < 7; i++) {
            player.drawCard(atlantaCard);
        }
        assertEquals(7, player.handSize());

        player.drawCard(chicagoCard);
        bsc.playerHandFull(player);
        assertEquals(7, player.handSize());
        assertTrue(player.getCardsInHand().contains(chicagoCard));
        EasyMock.verify(mockedGameWindow);
    }

    @Test
    public void testDiscardCardAtIndex() {
        PlayerCard atlantaCard = new PlayerCard(atlanta);
        PlayerCard chicagoCard = new PlayerCard(chicago);
        Player player = new Medic(atlanta);

        for (int i = 0; i < 2; i++) {
            player.drawCard(atlantaCard);
        }

        player.drawCard(chicagoCard);

        for (int i = 0; i < 3; i++) {
            player.drawCard(atlantaCard);
        }

        assertTrue(player.getCardsInHand().contains(chicagoCard));
        player.discardCardAtIndex(2);
        assertFalse(player.getCardsInHand().contains(chicagoCard));
    }

    @Test
    public void testDiscardCardWithName() {
        PlayerCard atlantaCard = new PlayerCard(atlanta);
        PlayerCard chicagoCard = new PlayerCard(chicago);
        Player player = new Medic(atlanta);

        for (int i = 0; i < 2; i++) {
            player.drawCard(atlantaCard);
        }

        player.drawCard(chicagoCard);

        for (int i = 0; i < 3; i++) {
            player.drawCard(atlantaCard);
        }

        assertTrue(player.getCardsInHand().contains(chicagoCard));
        player.discardCardWithName("Chicago");
        assertFalse(player.getCardsInHand().contains(chicagoCard));
    }

    @Test
    public void testDiscardCardInvalidIndex() {
        Player player = new Medic(atlanta);
        assertThrows(IndexOutOfBoundsException.class, () -> player.discardCardAtIndex(-1));
    }

    @Test
    public void testContingencyPlannerTakeCardFromDiscardPile() {
        GameWindow gameWindow = EasyMock.createMock(GameWindow.class);
        setupBSC(gameWindow);

        ContingencyPlanner contingencyPlanner = new ContingencyPlanner(atlanta, bsc);
        EventCard oneQuietNightCard = new EventCard(EventName.ONE_QUIET_NIGHT, bsc);
        EventCard airliftCard = new EventCard(EventName.AIRLIFT, bsc);
        bsc.playerDiscardPile.add(oneQuietNightCard);
        bsc.playerDiscardPile.add(airliftCard);

        EasyMock.expect(gameWindow.promptSelectOption(anyObject(), anyObject(), anyObject()))
                .andReturn("");
        EasyMock.expect(gameWindow.promptSelectOption(anyObject(), anyObject(), anyObject()))
                .andReturn("ONE_QUIET_NIGHT");
        EasyMock.replay(gameWindow);

        assertFalse(contingencyPlanner.isHoldingEventCard);
        contingencyPlanner.takeEventCardFromDiscardPile();
        assertFalse(contingencyPlanner.isHoldingEventCard);

        contingencyPlanner.takeEventCardFromDiscardPile();
        assertTrue(contingencyPlanner.isHoldingEventCard);
        assertEquals("ONE_QUIET_NIGHT", contingencyPlanner.heldEventCard.getEventName());

        contingencyPlanner.takeEventCardFromDiscardPile();
        assertEquals("ONE_QUIET_NIGHT", contingencyPlanner.heldEventCard.getEventName());

        EasyMock.verify(gameWindow);
    }

    @Test
    public void testContingencyPlannerTakeCardFromEmptyDiscardPile() {
        setupBSC(new DummyGameWindow());
        bsc.playerDiscardPile = new Stack<>();
        ContingencyPlanner contingencyPlanner = new ContingencyPlanner(atlanta, bsc);
        assertFalse(contingencyPlanner.takeEventCardFromDiscardPile());
    }

    @Test
    public void testContingencyPlannerPlayEventCardFromRoleCard() {
        GameWindow gameWindow = EasyMock.niceMock(GameWindow.class);
        setupBSC(gameWindow);

        ContingencyPlanner contingencyPlanner = new ContingencyPlanner(atlanta, bsc);
        EasyMock.replay(gameWindow);

        assertFalse(bsc.isQuietNight);
        contingencyPlanner.playEventCardFromRoleCard();
        assertFalse(bsc.isQuietNight);

        contingencyPlanner.heldEventCard = new EventCard(EventName.ONE_QUIET_NIGHT, bsc);
        contingencyPlanner.isHoldingEventCard = true;
        contingencyPlanner.playEventCardFromRoleCard();
        assertTrue(bsc.isQuietNight);
        assertFalse(contingencyPlanner.isHoldingEventCard);
    }

    @Test
    public void testPlayEventCardWhenHandOverflowing() {
        GameWindow gameWindow = EasyMock.niceMock(GameWindow.class);
        setupBSC(gameWindow);

        Player player = new Scientist(atlanta);
        PlayerCard playerCard = new PlayerCard(atlanta);
        EventCard oneQuietNight = new EventCard(EventName.ONE_QUIET_NIGHT, bsc);

        EasyMock.expect(gameWindow.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Play an Event Card");
        EasyMock.expect(gameWindow.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("ONE_QUIET_NIGHT");
        EasyMock.replay(gameWindow);

        player.drawCard(oneQuietNight);
        for (int i = 0; i < 6; i++) {
            player.drawCard(playerCard);
        }

        assertEquals(7, player.handSize());
        assertTrue(player.getCardsInHand().contains(oneQuietNight));
        assertFalse(bsc.isQuietNight);

        player.drawCard(playerCard);
        bsc.playerHandFull(player);

        assertEquals(7, player.handSize());
        assertFalse(player.getCardsInHand().contains(oneQuietNight));
        assertTrue(bsc.isQuietNight);
        EasyMock.verify(gameWindow);
    }

    @Test
    public void testDiscardWhenHaveEventCardAndHandOverflowing() {
        GameWindow gameWindow = EasyMock.createMock(GameWindow.class);
        setupBSC(gameWindow);

        Player player = new Scientist(atlanta);
        PlayerCard atlantaCard = new PlayerCard(atlanta);
        PlayerCard chicagoCard = new PlayerCard(chicago);
        EventCard oneQuietNight = new EventCard(EventName.ONE_QUIET_NIGHT, bsc);

        EasyMock.expect(gameWindow.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Discard a Card");
        EasyMock.expect(gameWindow.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn("Atlanta");
        EasyMock.replay(gameWindow);

        player.drawCard(oneQuietNight);
        for (int i = 0; i < 6; i++) {
            player.drawCard(atlantaCard);
        }

        assertEquals(7, player.handSize());
        assertTrue(player.getCardsInHand().contains(oneQuietNight));
        assertFalse(bsc.isQuietNight);

        player.drawCard(chicagoCard);
        bsc.playerHandFull(player);

        assertEquals(7, player.handSize());
        assertTrue(player.getCardsInHand().contains(oneQuietNight));
        assertTrue(player.getCardsInHand().contains(chicagoCard));
        assertFalse(bsc.isQuietNight);
        EasyMock.verify(gameWindow);
    }

    @Test
    public void testPlayerCardString() {
        PlayerCard card = new PlayerCard(atlanta);
        assertEquals("Atlanta", card.toString());
    }

    @Test
    public void testPlayerToString() {
        Player player = new Scientist(atlanta);
        player.name = "Scientist";
        assertEquals("Scientist", player.toString());
    }

    @Test
    public void testPlayerIcon() {
        Player player = new Scientist(atlanta);
        assertInstanceOf(Image.class, player.getIcon());
    }
}
