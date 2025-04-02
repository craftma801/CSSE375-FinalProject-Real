package main.roles;

import main.ActionFailedException;
import main.City;
import main.Player;

import java.awt.*;

public class OperationsExpert extends Player {
    public static final Color ROLE_COLOR = Color.decode("0xFF00FF");

    public OperationsExpert(City startLocation) {
        super(ROLE_COLOR, startLocation);
        this.displayName = "OE";
    }

    public boolean operationsExpertAction(City destination, String toDiscard) {
        if (this.currentLocation.hasResearchStation()) {
            for (int i = 0; i < cardsInHand.size(); i++) {
                if (cardsInHand.get(i).getName().equals(toDiscard)) {
                    discardCardAtIndex(i);
                }
            }
            currentLocation.players.remove(this);
            this.currentLocation = destination;
            destination.players.add(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void buildResearchStation() {
        City targetCity = this.currentLocation;
        if (targetCity.hasResearchStation()) {
            throw new ActionFailedException("There is already a research station here!");
        }
        targetCity.buildResearchStation();
    }
}
