package main.roles;

import main.City;
import main.Player;

import java.awt.*;

public class Dispatcher extends Player {
    public static final Color ROLE_COLOR = Color.decode("0xf9a711");

    public Dispatcher(City startLocation) {
        super(ROLE_COLOR, startLocation);
    }
}
