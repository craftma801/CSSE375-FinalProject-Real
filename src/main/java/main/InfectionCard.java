package main;

public class InfectionCard {
    public final City city;
    private boolean inInfectionDeck = true;

    public InfectionCard(City city) {
        this.city = city;
    }

    public void cardDrawn(DiseaseStatus status, DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        if (inInfectionDeck) {
            this.inInfectionDeck = false;
            if (status != DiseaseStatus.ERADICATED) {
                diseaseCubeBank.infectCity(this.city, this.city.defaultColor(), outbreakManager);
            }
        }
    }

    public void cardDrawnDuringEpidemic(DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        diseaseCubeBank.infectCity(this.city, this.city.defaultColor(), outbreakManager);
        diseaseCubeBank.infectCity(this.city, this.city.defaultColor(), outbreakManager);
        diseaseCubeBank.infectCity(this.city, this.city.defaultColor(), outbreakManager);
    }

    public void infectDuringSetup(int diseaseCubes, DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        for (int i = 0; i < diseaseCubes; i++) {
            diseaseCubeBank.infectCity(this.city, this.city.defaultColor(), outbreakManager);
        }
    }

    public CityColor getCityColor() {
        return this.city.defaultColor();
    }

    public String toString() {
        return city.name;
    }
}