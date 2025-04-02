package main.roles;

import main.*;

import java.awt.*;

public class Medic extends Player {
    public static final Color ROLE_COLOR = Color.decode("0xAA00FF");

    public Medic(City startLocation) {
        super(ROLE_COLOR, startLocation);
        this.displayName = "MD";
    }
    @Override
    public void treatDisease(CityColor cubeColor, DiseaseCubeBank diseaseCubeBank){
        boolean result = this.getCity().medicTreatDisease(cubeColor, diseaseCubeBank);
        if (!result) {
            throw new ActionFailedException("Failed to treat disease!");
        }
    }
}
