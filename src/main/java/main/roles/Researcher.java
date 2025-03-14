package main.roles;

import main.City;
import main.Player;
import main.PlayerCard;

import java.awt.*;

public class Researcher extends Player {
    public static final Color ROLE_COLOR = Color.decode("0x825b15");

    public Researcher(City startLocation) {
        super(ROLE_COLOR, startLocation);
    }

    @Override
    protected boolean cardMatchesCurrentLocation(PlayerCard card){
        return true;
    }
}
