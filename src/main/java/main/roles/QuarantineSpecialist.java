package main.roles;

import main.City;
import main.Player;

import java.awt.*;
import java.util.ArrayList;

public class QuarantineSpecialist extends Player {
    public static final Color ROLE_COLOR = Color.decode("0x00FF00");
    public static QuarantineSpecialist currentInstance;

    public QuarantineSpecialist(City startLocation) {
        super(ROLE_COLOR, startLocation);
        this.displayName = "QS";
        currentInstance = this;
    }
}
