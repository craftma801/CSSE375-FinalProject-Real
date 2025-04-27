package tests;

import main.*;
import main.roles.Medic;
import main.roles.Researcher;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.junit.jupiter.api.Assertions.*;

public class ActionTest extends BoardStatusControllerTest {

    @Test
    public void testAirlift() {
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        EventCard airlift = new EventCard(EventName.AIRLIFT, bsc);

        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(), anyObject(), anyObject())).andReturn(airlift);
        EasyMock.expect(gw.promptSelectPlayer(anyObject(), anyObject(), anyObject())).andReturn(bsc.players[1]);
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
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        EventCard governmentGrant = new EventCard(EventName.GOVERNMENT_GRANT, bsc);

        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(), anyObject(), anyObject())).andReturn(governmentGrant);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(chicago));
        EasyMock.replay(gw);

        assertEquals(4, bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);
        assertEquals(4, bsc.currentPlayerRemainingActions);
        assertFalse(chicago.hasResearchStation());

        bsc.players[0].drawCard(governmentGrant);
        assertTrue(bsc.players[0].getCardsInHand().contains(governmentGrant));
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);
        assertTrue(chicago.hasResearchStation());
        assertFalse(bsc.players[0].getCardsInHand().contains(governmentGrant));
    }

    @Test
    public void testEventCardToPlayNull() {
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        EventCard governmentGrant = new EventCard(EventName.GOVERNMENT_GRANT, bsc);

        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(), anyObject(), anyObject())).andReturn(null);
        EasyMock.replay(gw);

        assertEquals(4, bsc.currentPlayerRemainingActions);
        assertFalse(chicago.hasResearchStation());
        bsc.players[0].drawCard(governmentGrant);
        assertTrue(bsc.players[0].getCardsInHand().contains(governmentGrant));
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);
        assertEquals(4, bsc.currentPlayerRemainingActions);
        assertFalse(chicago.hasResearchStation());
        assertTrue(bsc.players[0].getCardsInHand().contains(governmentGrant));
    }


    @Test
    public void testDriveFerry() {
        Pandemic.bundle = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(chicago));
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(montreal));
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(newYork));
        EasyMock.replay(gw);

        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();

        bsc.handleAction(PlayerAction.DRIVE_FERRY);
        bsc.handleAction(PlayerAction.DRIVE_FERRY);
        bsc.handleAction(PlayerAction.DRIVE_FERRY);

        EasyMock.verify(gw);
    }

    @Test
    public void testPlayEventCard() {
        ArrayList<City> cityMap = Pandemic.createMap();
        GameWindow gw = EasyMock.createMock(GameWindow.class);
        bsc = new BoardStatusController(gw, cityMap, NUM_PLAYERS, NUM_EPIDEMIC_CARDS);
        Medic medic = new Medic(atlanta);
        bsc.players = new Player[]{medic};
        bsc.currentPlayerTurn = 0;
        bsc.currentPlayerRemainingActions = 4;
        EventCard oneQuietNightCard = new EventCard(EventName.ONE_QUIET_NIGHT, bsc);
        medic.drawCard(oneQuietNightCard);
        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(), anyObject(), anyObject())).andReturn(oneQuietNightCard);
        gw.repaintGameBoard();
        EasyMock.expectLastCall();
        EasyMock.replay(gw);
        assertEquals(1, medic.getCardsInHand().size());
        assertFalse(bsc.isQuietNight);
        bsc.handleAction(PlayerAction.PLAY_EVENT_CARD);
        assertEquals(0, medic.getCardsInHand().size());
        assertTrue(bsc.isQuietNight);
        assertEquals(4, bsc.currentPlayerRemainingActions);
    }

    @Test
    public void infectEradicatedColor() {
        OutbreakManager obm = EasyMock.niceMock(OutbreakManager.class);
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        bsc.cureDisease(CityColor.BLUE);
        bsc.handleAction(PlayerAction.SKIP_ACTION);
        assertEquals(DiseaseStatus.ERADICATED, bsc.getStatus(CityColor.BLUE));
        InfectionCard testCard = new InfectionCard(atlanta);
        testCard.cardDrawn(bsc.getStatus(CityColor.BLUE), dcb, obm);
        assertEquals(0, atlanta.getInfectionLevel(CityColor.BLUE));
    }

    @Test
    public void researchStationBuilt() {
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(chicago));
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        assertDoesNotThrow(() -> bsc.handleAction(PlayerAction.DRIVE_FERRY));
        assertEquals(3, bsc.currentPlayerRemainingActions);
        PlayerCard chicagoCard = new PlayerCard(chicago);
        Player currentPlayer = bsc.players[bsc.currentPlayerTurn];
        currentPlayer.drawCard(chicagoCard);
        bsc.handleAction(PlayerAction.BUILD_RESEARCH_STATION);
        EasyMock.verify(gw);
        assertEquals(2, bsc.currentPlayerRemainingActions);
        assertTrue(currentPlayer.getCity().hasResearchStation());
    }

    @Test
    public void testHandleViewCards() {
        gw.displayPlayerCards(anyObject(), anyObject());
        EasyMock.expectLastCall();
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        bsc.handleAction(PlayerAction.VIEW_CARDS);
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleTreatDiseaseYellow() {
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.YELLOW);
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initFourGenericPlayers();
        bsc.currentPlayerTurn = 0;
        atlanta.infect(CityColor.YELLOW, dcb, om);
        assertEquals(1, atlanta.getInfectionLevel(CityColor.YELLOW));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);
        EasyMock.verify(gw);
        assertEquals(0, atlanta.getInfectionLevel(CityColor.YELLOW));
    }

    @Test
    public void testHandleTreatDiseaseBlue() {
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.BLUE);
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initFourGenericPlayers();
        bsc.currentPlayerTurn = 0;
        atlanta.infect(CityColor.BLUE, dcb, om);
        assertEquals(1, atlanta.getInfectionLevel(CityColor.BLUE));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);
        EasyMock.verify(gw);
        assertEquals(0, atlanta.getInfectionLevel(CityColor.BLUE));
    }

    @Test
    public void testHandleTreatDiseaseRed() {
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.RED);
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initFourGenericPlayers();
        bsc.currentPlayerTurn = 0;
        atlanta.infect(CityColor.RED, dcb, om);
        assertEquals(1, atlanta.getInfectionLevel(CityColor.RED));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);
        EasyMock.verify(gw);
        assertEquals(0, atlanta.getInfectionLevel(CityColor.RED));
    }

    @Test
    public void testHandleTreatDiseaseBlack() {
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.BLACK);
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initFourGenericPlayers();
        bsc.currentPlayerTurn = 0;
        atlanta.infect(CityColor.BLACK, dcb, om);
        assertEquals(1, atlanta.getInfectionLevel(CityColor.BLACK));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);
        EasyMock.verify(gw);
        assertEquals(0, atlanta.getInfectionLevel(CityColor.BLACK));
    }

    @Test
    public void testGiveKnowledge() {
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        Player firstTestPlayer = new Medic(atlanta);
        Player secondTestPlayer = new Researcher(atlanta);
        PlayerCard atlantaCard = new PlayerCard(atlanta);
        EasyMock.expect(gw.promptSelectPlayer(anyObject(), anyObject(), anyObject())).andReturn(secondTestPlayer);
        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(), anyObject(), anyObject())).andReturn(atlantaCard);
        gw.displayNextPlayerInfo(anyObject(), anyInt());
        EasyMock.expectLastCall();
        EasyMock.replay(gw);
        bsc.players[0] = firstTestPlayer;
        bsc.players[1] = secondTestPlayer;
        firstTestPlayer.drawCard(atlantaCard);
        bsc.handleAction(PlayerAction.GIVE_KNOWLEDGE);
        assertTrue(secondTestPlayer.getCardsInHand().contains(atlantaCard));
        assertFalse(firstTestPlayer.getCardsInHand().contains(atlantaCard));
    }

    @Test
    public void testTakeKnowledge() {
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        Player firstTestPlayer = new Medic(atlanta);
        Player secondTestPlayer = new Researcher(atlanta);
        PlayerCard atlantaCard = new PlayerCard(atlanta);
        EasyMock.expect(gw.promptSelectPlayer(anyObject(), anyObject(), anyObject())).andReturn(secondTestPlayer);
        EasyMock.expect(gw.promptSelectPlayerCard(anyObject(), anyObject(), anyObject())).andReturn(atlantaCard);
        EasyMock.replay(gw);
        bsc.players[0] = firstTestPlayer;
        bsc.players[1] = secondTestPlayer;
        secondTestPlayer.drawCard(atlantaCard);
        bsc.handleAction(PlayerAction.TAKE_KNOWLEDGE);
        assertTrue(firstTestPlayer.getCardsInHand().contains(atlantaCard));
        assertFalse(secondTestPlayer.getCardsInHand().contains(atlantaCard));
    }


    @Test
    public void testHandleCharterFlight() {
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(miami));
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        PlayerCard atlantaCard = new PlayerCard(atlanta);
        bsc.players[0].drawCard(atlantaCard);
        assertTrue(bsc.players[0].getCardsInHand().contains(atlantaCard));
        assertDoesNotThrow(() -> bsc.handleAction(PlayerAction.CHARTER_FLIGHT));
        assertEquals(miami, bsc.players[0].getCity());
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleDirectFlight() {
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(miami));
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        assertEquals(atlanta, bsc.players[0].getCity());
        PlayerCard miamiCard = new PlayerCard(miami);
        bsc.players[0].drawCard(miamiCard);
        assertTrue(bsc.players[0].getCardsInHand().contains(miamiCard));
        assertDoesNotThrow(() -> bsc.handleAction(PlayerAction.DIRECT_FLIGHT));
        assertEquals(miami, bsc.players[0].getCity());
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleShuttleFlight() {
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(miami));
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        assertEquals(atlanta, bsc.players[0].getCity());
        miami.buildResearchStation();
        bsc.handleAction(PlayerAction.SHUTTLE_FLIGHT);
        assertEquals(miami, bsc.players[0].getCity());
        EasyMock.verify(gw);
    }


    @Test
    public void handleCureDiseaseFail() {
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
        bsc.playerDiscardPile = new Stack<>();
        bsc.players[0] = medic;
        bsc.players[0].drawCard(chicagoCard);
        bsc.players[0].drawCard(montrealCard);
        bsc.players[0].drawCard(washingtonCard);
        bsc.players[0].drawCard(atlantaCard);
        bsc.players[0].drawCard(miamiCard);
        assertEquals(5, bsc.players[0].handSize());
        bsc.handleAction(PlayerAction.DISCOVER_CURE);
        assertEquals(5, bsc.players[0].handSize());
    }


    @Test
    public void testHandleTreatDiseaseNull() {
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(null);
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.nextPlayerTurn();
        assertEquals(4, bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.TREAT_DISEASE);
        assertEquals(4, bsc.currentPlayerRemainingActions);
        EasyMock.verify(gw);
    }

    @Test
    public void testHandleTreatDiseaseCured() {
        DiseaseCubeBank dcb = EasyMock.niceMock(DiseaseCubeBank.class);
        OutbreakManager om = EasyMock.niceMock(OutbreakManager.class);
        EasyMock.expect(gw.promptColorToCure(anyObject())).andReturn(CityColor.YELLOW);
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initFourGenericPlayers();
        bsc.nextPlayerTurn();
        atlanta.infect(CityColor.YELLOW, dcb, om);
        atlanta.infect(CityColor.YELLOW, dcb, om);
        bsc.cureDisease(CityColor.YELLOW);
        assertEquals(2, atlanta.getInfectionLevel(CityColor.YELLOW));
        bsc.handleAction(PlayerAction.TREAT_DISEASE);
        EasyMock.verify(gw);
        assertEquals(0, atlanta.getInfectionLevel(CityColor.YELLOW));
    }

}
