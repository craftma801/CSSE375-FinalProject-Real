package main.roles;

import main.City;
import main.Player;

import java.awt.*;

public class Scientist extends Player {
    public static final Color ROLE_COLOR = Color.decode("0xFFFFFF");

    public Scientist(City startLocation) {
        super(ROLE_COLOR, startLocation);
        this.displayName = "SC";
    }
}
