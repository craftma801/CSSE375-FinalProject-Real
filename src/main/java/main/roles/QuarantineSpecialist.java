package main.roles;

import main.City;
import main.Player;

import java.awt.*;
import java.util.ArrayList;

public class QuarantineSpecialist extends Player {
    public static final Color ROLE_COLOR = Color.decode("0x3d8b4b");
    public static boolean activeInGame = false;
    public static QuarantineSpecialist currentInstance;

    public QuarantineSpecialist(City startLocation) {
        super(ROLE_COLOR, startLocation);
        activeInGame = true;
        currentInstance = this;
    }

    public static boolean blockingInfection(City cityBeingInfected) {
        if(!activeInGame)
            return false;
        if(currentInstance.currentLocation.equals(cityBeingInfected))
            return true;
        ArrayList<City> connectedCities = currentInstance.currentLocation.connectedCities;
        for(City city : connectedCities){
            if(cityBeingInfected.equals(city)){
                return true;
            }
        }
        return false;
    }
}
