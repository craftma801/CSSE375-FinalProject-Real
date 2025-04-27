package main;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

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
            case GOVERNMENT_GRANT ->
                    {
                        CompletableFuture<City> userSelection = this.bsc.gameWindow.selectCity(new HashSet<>(this.bsc.cityMap));
                        userSelection.thenAccept((city) -> {
                            city.buildResearchStation();
                        });
                    }
            case ONE_QUIET_NIGHT ->
                    {
                        this.bsc.isQuietNight = true;
                    }
            case RESILIENT_POPULATION ->
                    {
                        InfectionCard cardToRemove = this.bsc.gameWindow.promptInfectionCard(new PromptWindowInputs(this.bsc.infectionDiscardPile.toArray(new InfectionCard[0]),
                                this.bsc.bundle.getString("removeAnInfectionCard"), this.bsc.bundle.getString("selectAnInfectionCardToRemoveFromTheGame")));
                        this.bsc.infectionDiscardPile.remove(cardToRemove);
                    }
        }
        bsc.playerDiscardPile.push(this);
        bsc.gameWindow.repaintGameBoard();
    }

    public String getEventName() {
        return eventName.toString();
    }
}
