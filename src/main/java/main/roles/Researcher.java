package main.roles;

import main.City;
import main.Player;
import main.PlayerCard;

import java.awt.*;

public class Researcher extends Player {
    public static final Color ROLE_COLOR = Color.decode("0x32AB32");

    public Researcher(City startLocation) {
        super(ROLE_COLOR, startLocation);
        this.displayName = "RS";
    }

    @Override
    protected boolean cardMatchesCurrentLocation(PlayerCard card){
        return true;
    }
}
