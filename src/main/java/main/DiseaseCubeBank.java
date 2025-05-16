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

    public void infectCity(City city, CityColor color, OutbreakManager outbreakManager) {
        if(city.quarantineSpecialistNearby()){
            return;
        }

        int currentLevel = city.getInfectionLevel(color);
        if(currentLevel >= 3){
            city.outbreak(color, this, outbreakManager);
        }
        else {
            city.addCube(color);
            cityInfected(color);
        }
    }

    public boolean treatDiseaseAt(City city, CityColor color, boolean isMedic) {
        int diseaseAmount = city.getInfectionLevel(color);
        if(diseaseAmount > 0){
            if(isMedic){
                city.removeAllCubes(color);
                for(int i = 0; i < diseaseAmount; i++){
                    colorTreated(color);
                }
            }
            else{
                city.removeCube(color);
                colorTreated(color);
            }
            return true;
        }
        return false;
    }

}
