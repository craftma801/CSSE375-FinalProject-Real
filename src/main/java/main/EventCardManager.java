package main;

import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Stack;

public class EventCardManager {
    private final GameWindowInterface gameWindow;
    private final ResourceBundle bundle;

    public EventCardManager(GameWindowInterface gameWindow, ResourceBundle bundle) {
        this.gameWindow = gameWindow;
        this.bundle = bundle;
    }

    public boolean handlePlayEventCard(Player currentPlayer) {
        ArrayList<PlayerCard> eventCardsInHand = getEventCardsInHand(currentPlayer);
        if (eventCardsInHand.isEmpty()) {
            return false;
        }

        EventCard eventCardToPlay = (EventCard) gameWindow.promptSelectPlayerCard(eventCardsInHand.toArray(new PlayerCard[0]),
                bundle.getString("selectAnEventCard"), bundle.getString("whichEventCardWouldYouLikeToPlay"));
        if (eventCardToPlay == null) {
            return false;
        }

        playEventCard(currentPlayer, eventCardToPlay);
        return false;
    }

    public boolean handContainsEventCard(Player player) {
        return getEventCardsInHand(player).size() > 0;
    }

    public boolean givePlayerOptionToPlayEventCard(Player currentPlayer) {
        String[] options = new String[]{bundle.getString("playAnEventCard"), bundle.getString("discardACard")};
        String choice = gameWindow.promptSelectOption(options,
                bundle.getString("yourHandIsFull"), bundle.getString("youreHoldingAnEventCardWouldYouLikeToPlayItOrDiscardACard"));

        if (!choice.equals(bundle.getString("playAnEventCard"))) {
            return false;
        }

        ArrayList<String> cardsToSelectFrom = getPossibleEventCardsToPlay(currentPlayer);

        String selectedName = gameWindow.promptSelectOption(cardsToSelectFrom.toArray(new String[0]),
                bundle.getString("playAnEventCard"), bundle.getString("selectAnEventCardToPlay"));

        for (PlayerCard card : currentPlayer.cardsInHand) {
            if (card.name.equals(selectedName)) {
                playEventCard(currentPlayer, (EventCard) card);
                break;
            }
        }
        return true;
    }

    private ArrayList<String> getPossibleEventCardsToPlay(Player player) {
        ArrayList<String> listOfPossibleEventCardsToPlay = new ArrayList<>();
        for (PlayerCard card : player.cardsInHand) {
            if (card.isEvent()) {
                listOfPossibleEventCardsToPlay.add(card.name);
            }
        }
        return listOfPossibleEventCardsToPlay;
    }

    public void playEventCard(Player player, EventCard card) {
        card.use();
        player.discardCardWithName(card.name);
    }

    public ArrayList<PlayerCard> getEventCardsInHand(Player player) {
        ArrayList<PlayerCard> eventCardsInHand = new ArrayList<>();
        for (PlayerCard card : player.getCardsInHand()) {
            if (card.isEvent()) {
                eventCardsInHand.add(card);
            }
        }
        return eventCardsInHand;
    }

    public void shuffleEventCardsIntoPlayerDeck(Stack<PlayerCard> playerDeck, BoardStatusController bsc) {
        EventCard[] eventCards = createArrayWithAllEventCards(bsc);
        for (EventCard eventCard : eventCards) {
            insertEventCardIntoPlayerDeckAtRandomIndex(eventCard, playerDeck);
        }
    }

    private EventCard[] createArrayWithAllEventCards(BoardStatusController bsc) {
        return new EventCard[]{
                new EventCard(EventName.AIRLIFT, bsc),
                new EventCard(EventName.ONE_QUIET_NIGHT, bsc),
                new EventCard(EventName.GOVERNMENT_GRANT, bsc),
                new EventCard(EventName.FORECAST, bsc),
                new EventCard(EventName.RESILIENT_POPULATION, bsc),
        };
    }

    private void insertEventCardIntoPlayerDeckAtRandomIndex(EventCard eventCard, Stack<PlayerCard> playerDeck) {
        Random rand = new Random();
        int indexToInsertEventCard = rand.nextInt(playerDeck.size());
        playerDeck.insertElementAt(eventCard, indexToInsertEventCard);
    }
}

