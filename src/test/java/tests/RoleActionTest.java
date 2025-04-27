package tests;

import main.*;
import main.roles.ContingencyPlanner;
import main.roles.Dispatcher;
import main.roles.Medic;
import main.roles.OperationsExpert;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.easymock.EasyMock.anyObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleActionTest extends BoardStatusControllerTest {

    @Test
    public void testHandleContingencyPlannerRoleAction() {
        EventCard oqn = new EventCard(EventName.ONE_QUIET_NIGHT, bsc);
        EasyMock.expect(gw.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn(oqn.getEventName());
        EasyMock.replay(gw);
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        bsc.playerDiscardPile = new Stack<>();
        bsc.playerDiscardPile.push(oqn);
        ContingencyPlanner contingencyPlanner = new ContingencyPlanner(atlanta, bsc);
        bsc.players[0] = contingencyPlanner;
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertTrue(contingencyPlanner.isHoldingEventCard);
        EasyMock.verify(gw);
    }

    @Test
    public void testDispatcherRoleAction() {
        bsc.setup();
        bsc.initializePlayers();
        EasyMock.expect(gw.promptSelectPlayer(anyObject(), anyObject(), anyObject())).andReturn(bsc.players[1]);
        EasyMock.expect(gw.selectCity(anyObject())).andReturn(generateTestFuture(chicago));
        EasyMock.replay(gw);
        bsc.transferPlayToNextPlayer();
        Dispatcher dispatcher = new Dispatcher(atlanta);
        bsc.players[0] = dispatcher;
        assertEquals(atlanta, bsc.players[1].getCity());
        assertEquals(4, bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertEquals(chicago, bsc.players[1].getCity());
        assertEquals(3, bsc.currentPlayerRemainingActions);
    }

    @Test
    public void testOperationsExpertRoleAction() {
        bsc.setup();
        bsc.initializePlayers();
        PlayerCard chicagoCard = new PlayerCard(chicago);
        EasyMock.expect(gw.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn(miami.name);
        EasyMock.expect(gw.promptSelectOption(anyObject(), anyObject(), anyObject())).andReturn(chicagoCard.getName());
        EasyMock.replay(gw);
        bsc.transferPlayToNextPlayer();
        OperationsExpert operationsExpert = new OperationsExpert(atlanta);
        bsc.players[0] = operationsExpert;
        operationsExpert.drawCard(chicagoCard);
        assertEquals(atlanta, bsc.players[0].getCity());
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertEquals(miami, bsc.players[0].getCity());
    }

    @Test
    public void testNoRoleAction() {
        bsc.setup();
        bsc.initializePlayers();
        bsc.transferPlayToNextPlayer();
        Medic medic = new Medic(atlanta);
        bsc.players[0] = medic;
        assertEquals(4, bsc.currentPlayerRemainingActions);
        bsc.handleAction(PlayerAction.ROLE_ACTION);
        assertEquals(4, bsc.currentPlayerRemainingActions);
    }
}
