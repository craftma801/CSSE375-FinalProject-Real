package tests;

import main.Point;
import main.*;
import main.roles.*;
import org.easymock.EasyMock;
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

    private void setupBSC(GameWindowInterface gameWindow) {
        chicago = new City("Chicago", new Point(315, 405), CityColor.BLUE);
        atlanta = new City("Atlanta", new Point(380, 515), CityColor.BLUE);
        london = new City("London", new Point(0, 0), CityColor.BLUE);
        sanFrancisco = new City("San Francisco", new Point(105, 460), CityColor.BLUE);
        montreal = new City("Montreal", new Point(485, 400), CityColor.BLUE);
        washington = new City("Washington", new Point(555, 510), CityColor.BLUE);
        newYork = new City("New York", new Point(610, 420), CityColor.BLUE);
        madrid = new City("Madrid", new Point(890, 475), CityColor.BLUE);
        miami = new City("Miami", new Point(485, 635), CityColor.YELLOW);

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

        this.bsc = new BoardStatusController(gameWindow, basicMap);
    }

    @Test
    public void testGetColor() {
        BoardStatusController bsc = new BoardStatusController(new DummyGameWindow(), new ArrayList<>());
        City testCity = new City("Test Town", new Point(8, 12), CityColor.BLACK);

        Player researcher = new Researcher(testCity);
        assertEquals(Color.decode("0x825b15"), researcher.getColor());

        Player contingencyPlanner = new ContingencyPlanner(testCity, bsc);
        assertEquals(Color.decode("0x35f6ec"), contingencyPlanner.getColor());

        Player operationsExpert = new OperationsExpert(testCity);
        assertEquals(Color.decode("0x5feb3e"), operationsExpert.getColor());

        Player quarantineSpecialist = new QuarantineSpecialist(testCity);
        assertEquals(Color.decode("0x3d8b4b"), quarantineSpecialist.getColor());

        Player medic = new Medic(testCity);
        assertEquals(Color.decode("0xe66eda"), medic.getColor());

        Player scientist = new Scientist(testCity);
        assertEquals(Color.decode("0xFFFFFF"), scientist.getColor());

        Player dispatcher = new Dispatcher(testCity);
        assertEquals(Color.decode("0xf9a711"), dispatcher.getColor());
    }

    @Test
    public void testGetCity() {
        City testCity = new City("Test Towers", new Point(2, 12), CityColor.YELLOW);
        Player researcher = new Researcher(testCity);
        assertEquals(testCity, researcher.getCity());
    }

    @Test
    public void testPlayerNames() {
        Pandemic.bundle = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        bsc = new BoardStatusController(new DummyGameWindow(), Pandemic.createMap());
        City testCity = new City("Test Towers", new Point(2, 12), CityColor.YELLOW);
        Player player = new Researcher(testCity);
        player.name = bsc.generatePlayerName(1,player);
        assertEquals("Player 1 (Researcher)", player.name);
    }

    @Test
    public void testNonMedicTreatDisease() {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        OutbreakManager outbreakManager = new OutbreakManager(new DummyGameWindow());
        City testCity = new City("Terre Haute", new Point(40, 70), CityColor.YELLOW);
        testCity.infect(CityColor.YELLOW, diseaseCubeBank, outbreakManager);
        testCity.infect(CityColor.YELLOW, diseaseCubeBank, outbreakManager);

        Player testPlayer = new Researcher(testCity);
        testPlayer.treatDisease(CityColor.YELLOW, diseaseCubeBank);

        assertEquals(1, testCity.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testMedicTreatDisease() {
        DiseaseCubeBank diseaseCubeBank = new DiseaseCubeBank();
        OutbreakManager outbreakManager = new OutbreakManager(new DummyGameWindow());
        City testCity = new City("Terre Haute", new Point(40, 70), CityColor.YELLOW);
        testCity.infect(CityColor.YELLOW, diseaseCubeBank, outbreakManager);
        testCity.infect(CityColor.YELLOW, diseaseCubeBank, outbreakManager);

        Player testPlayer = new Medic(testCity);
        testPlayer.treatDisease(CityColor.YELLOW, diseaseCubeBank);

        assertEquals(0, testCity.getInfectionLevel(CityColor.YELLOW));

        assertThrows(ActionFailedException.class, () -> testPlayer.treatDisease(CityColor.YELLOW, diseaseCubeBank));
    }

    @Test
    public void testBuildResearchStation() {
        City testCity = new City("Terre Haute", new Point(40, 70), CityColor.YELLOW);
        PlayerCard testCard = new PlayerCard(testCity);
        Player testPlayer = new Medic(testCity);

        assertFalse(testCity.hasResearchStation());
        assertThrows(ActionFailedException.class, testPlayer::buildResearchStation);
        assertFalse(testCity.hasResearchStation());

        testPlayer.drawCard(testCard);
        testPlayer.buildResearchStation();
        assertTrue(testCity.hasResearchStation());
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
        City chicago = new City("Chicago", new Point(0, 0), CityColor.BLUE);
        PlayerCard testCard = new PlayerCard(chicago);
        City testCity = new City("Terre Haute", new Point(40, 70), CityColor.YELLOW);
        Player testPlayer = new Researcher(testCity);
        testPlayer.drawCard(testCard);
        testPlayer.directFlight(chicago);
        assertEquals(chicago, testPlayer.getCity());
    }

    @Test
    public void testCharterFlight() {
        City chicago = new City("Chicago", new Point(0, 0), CityColor.BLUE);
        City baghdad = new City("Baghdad", new Point(0, 0), CityColor.BLUE);
        PlayerCard testCard = new PlayerCard(chicago);
        Player testPlayer = new Researcher(chicago);
        testPlayer.drawCard(testCard);
        testPlayer.charterFlight(baghdad);
        assertEquals(baghdad, testPlayer.getCity());
    }

    @Test
    public void testShuttleFlight() {
        City chicago = new City("Chicago", new Point(0, 0), CityColor.BLUE);
        City testCity = new City("Terre Haute", new Point(40, 70), CityColor.YELLOW);
        Player testPlayer = new Researcher(testCity);
        chicago.buildResearchStation();
        testPlayer.shuttleFlight(chicago);
        assertEquals(chicago, testPlayer.getCity());
    }

    @Test
    public void testShareKnowledgeTake() {
        City testCity = new City("Terre Haute", new Point(40, 70), CityColor.YELLOW);
        Player testPlayer1 = new Medic(testCity);
        Player testPlayer2 = new Researcher(testCity);
        PlayerCard testCard = new PlayerCard(testCity);
        testPlayer1.drawCard(testCard);
        testPlayer2.shareKnowledgeTake(testPlayer1, testCard);
        assertEquals(1, testPlayer2.handSize());
        assertEquals(0, testPlayer1.handSize());
    }

    @Test
    public void testShareKnowledgeGive() {
        City testCity = new City("Terre Haute", new Point(40, 70), CityColor.YELLOW);
        Player testPlayer1 = new Medic(testCity);
        Player testPlayer2 = new Researcher(testCity);
        PlayerCard testCard = new PlayerCard(testCity);
        testPlayer1.drawCard(testCard);
        testPlayer1.shareKnowledgeGive(testPlayer2, testCard);
        assertEquals(0, testPlayer1.handSize());
        assertEquals(1, testPlayer2.handSize());
    }

    @Test
    public void testGetCardNames() {
        City testCity = new City("Terre Haute", new Point(40, 70), CityColor.YELLOW);
        Player testPlayer1 = new Medic(testCity);
        PlayerCard testCard = new PlayerCard(testCity);
        testPlayer1.drawCard(testCard);
        ArrayList<String> cardNames = testPlayer1.getCardNames();
        assertEquals(1, cardNames.size());
    }

    @Test
    public void testBuildResearchStationAsOperationsExpert() {
        City testCity = new City("Terre Haute", new Point(40, 70), CityColor.YELLOW);
        Player testPlayer = new OperationsExpert(testCity);
        assertEquals(0, testPlayer.handSize());
        testPlayer.buildResearchStation();
        assertTrue(testCity.hasResearchStation());
        assertThrows(ActionFailedException.class, () -> testPlayer.buildResearchStation());
    }

    @Test
    public void testOperationsExpertRoleAction() {
        City chicago = new City("Chicago", new Point(0, 0), CityColor.BLUE);
        City baghdad = new City("Baghdad", new Point(0, 0), CityColor.BLUE);
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
        atlanta.buildResearchStation();
        OperationsExpert testPlayer = new OperationsExpert(atlanta);
        PlayerCard testCard = new PlayerCard(chicago);
        testPlayer.drawCard(testCard);
        testPlayer.operationsExpertAction(baghdad, "Chicago");
        assertEquals(0, testPlayer.handSize());
        assertEquals(0, atlanta.players.size());
        assertEquals(baghdad, testPlayer.getCity());
        assertEquals(1, baghdad.players.size());
    }

    @Test
    public void testOperationsExpertRoleActionFail() {
        City chicago = new City("Chicago", new Point(0, 0), CityColor.BLUE);
        City baghdad = new City("Baghdad", new Point(0, 0), CityColor.BLUE);
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
        atlanta.buildResearchStation();
        OperationsExpert testPlayer = new OperationsExpert(chicago);
        PlayerCard testCard = new PlayerCard(chicago);
        testPlayer.drawCard(testCard);
        testPlayer.operationsExpertAction(baghdad, "Chicago");
        assertEquals(chicago, testPlayer.getCity());
        assertEquals(0, baghdad.players.size());
        assertEquals(1, testPlayer.handSize());
    }

    @Test
    public void testMovedByDispatcher() {
        City chicago = new City("Chicago", new Point(0, 0), CityColor.BLUE);
        City baghdad = new City("Baghdad", new Point(0, 0), CityColor.BLUE);
        Player testPlayer = new Dispatcher(chicago);
        Player testPlayer2 = new Scientist(baghdad);
        testPlayer.forceRelocatePlayer(chicago);
        testPlayer2.forceRelocatePlayer(baghdad);
        assertEquals(testPlayer.getCity(), chicago);
        assertEquals(testPlayer2.getCity(), baghdad);
        testPlayer.forceRelocatePlayer(baghdad);
        assertEquals(testPlayer.getCity(), baghdad);
    }

    @Test
    public void testDrawCards() {
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
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
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
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
        City chicago = new City("Chicago", new Point(0, 0), CityColor.BLUE);
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
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
        City chicago = new City("Chicago", new Point(0, 0), CityColor.BLUE);
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
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
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
        Player player = new Medic(atlanta);
        assertThrows(IndexOutOfBoundsException.class, () -> player.discardCardAtIndex(-1));
    }

    @Test
    public void testContingencyPlannerTakeCardFromDiscardPile() {
        GameWindow gameWindow = EasyMock.createMock(GameWindow.class);
        setupBSC(gameWindow);

        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);

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

        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
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

        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);

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

        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
        City chicago = new City("Chicago", new Point(0, 0), CityColor.BLUE);

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
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
        PlayerCard card = new PlayerCard(atlanta);
        assertEquals("Atlanta", card.toString());
    }

    @Test
    public void testPlayerToString() {
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
        Player player = new Scientist(atlanta);
        player.name = "Scientist";
        assertEquals("Scientist", player.toString());
    }

    @Test
    public void testPlayerIcon() {
        City atlanta = new City("Atlanta", new Point(0, 0), CityColor.BLUE);
        Player player = new Scientist(atlanta);
        assertInstanceOf(Image.class, player.getIcon());
    }
}
