package main;

import java.util.HashMap;

public class DiseaseCubeBank {
    private HashMap<CityColor, Integer> cubeBanks;

    public DiseaseCubeBank() {
        cubeBanks = new HashMap<>();
        cubeBanks.put(CityColor.YELLOW, 24);
        cubeBanks.put(CityColor.RED, 24);
        cubeBanks.put(CityColor.BLUE, 24);
        cubeBanks.put(CityColor.BLACK, 24);
    }

    private void validateColor(CityColor cityColor) {
        if(!cubeBanks.containsKey(cityColor))
            throw new InvalidColorException(cityColor + " is not a valid color!");
    }

    public int remainingCubes(CityColor cityColor) {
        validateColor(cityColor);
        return cubeBanks.get(cityColor);
    }

    public void cityInfected(CityColor cityColor) {
        validateColor(cityColor);
        cubeBanks.put(cityColor, cubeBanks.get(cityColor) - 1);
    }

    public void colorTreated(CityColor cityColor) {
        validateColor(cityColor);
        cubeBanks.put(cityColor, cubeBanks.get(cityColor) + 1);
    }
}
