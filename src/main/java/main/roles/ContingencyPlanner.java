package main.roles;

import main.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class ContingencyPlanner extends Player {
    public static final Color ROLE_COLOR = Color.decode("0x35f6ec");
    public boolean isHoldingEventCard;
    public EventCard heldEventCard;
    private final BoardStatusController bsc;

    public ContingencyPlanner(City startingLocation, BoardStatusController bsc) {
        super(ROLE_COLOR, startingLocation);
        this.isHoldingEventCard = false;
        this.bsc = bsc;
    }

    public boolean takeEventCardFromDiscardPile() {
        if (this.isHoldingEventCard) {
            return false;
        }
        Stack<PlayerCard> discardPile = bsc.getPlayerDiscardPile();
        ArrayList<String> eventCardsInDiscardPile = getEventCardsInDiscardPile(discardPile);
        if (eventCardsInDiscardPile.isEmpty()) {
            return false;
        }
        try{
            EventName cardNameToTake = EventName.valueOf(bsc.gameWindow.promptSelectOption(eventCardsInDiscardPile.toArray(new String[0]),
                    "Select a card", "Take an event card from the discard pile"));
            EventCard cardToTake = getCardToTake(discardPile, cardNameToTake);
            this.isHoldingEventCard = true;
            this.heldEventCard = cardToTake;
            bsc.removeEventCardFromPlayerDiscardPile(cardToTake);
        } catch(IllegalArgumentException e){
            return false;
        }
        return true;
    }

    private EventCard getCardToTake(Stack<PlayerCard> discardPile, EventName cardNameToTake) {
        EventCard cardToTake = null;
        for (PlayerCard playerCard : discardPile) {
            if (playerCard.isEvent()) {
                if (((EventCard) playerCard).getEventName().equals(cardNameToTake.toString())) {
                    cardToTake = (EventCard) playerCard;
                    break;
                }
            }
        }
        return cardToTake;
    }

    private ArrayList<String> getEventCardsInDiscardPile(Stack<PlayerCard> discardPile) {
        ArrayList<String> eventCardsInDiscardPile = new ArrayList<>();
        for (PlayerCard card : discardPile) {
            if (card.getClass().equals(EventCard.class)) {
                eventCardsInDiscardPile.add(((EventCard) card).getEventName());
            }
        }
        return eventCardsInDiscardPile;
    }

    public void playEventCardFromRoleCard(){
        if (this.isHoldingEventCard) {
            this.heldEventCard.use();
            this.isHoldingEventCard = false;
            this.heldEventCard = null;
        }
    }
}
