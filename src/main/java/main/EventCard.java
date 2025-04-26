package main;

public class EventCard extends PlayerCard {
    EventName eventName;
    BoardStatusController bsc;

    public EventCard(EventName eventName, BoardStatusController bsc) {
        super(eventName);
        this.eventName = eventName;
        this.bsc = bsc;
    }

    public void use() {
        switch (eventName) {
            case AIRLIFT -> this.bsc.airLift();
            case FORECAST -> this.bsc.forecast();
            case GOVERNMENT_GRANT -> this.bsc.governmentGrant();
            case ONE_QUIET_NIGHT -> this.bsc.oneQuietNight();
            case RESILIENT_POPULATION -> this.bsc.resilientPopulation();
        }
        bsc.playerDiscardPile.add(this);
        bsc.gameWindow.repaintGameBoard();
    }

    public String getEventName() {
        return eventName.toString();
    }
}
