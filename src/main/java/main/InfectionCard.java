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
                this.city.infect(this.city.defaultColor(), diseaseCubeBank, outbreakManager);
            }
        }
    }

    public void cardDrawnDuringEpidemic(DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        this.city.infect(this.city.defaultColor(), diseaseCubeBank, outbreakManager);
        this.city.infect(this.city.defaultColor(), diseaseCubeBank, outbreakManager);
        this.city.infect(this.city.defaultColor(), diseaseCubeBank, outbreakManager);
    }

    public void infectDuringSetup(int diseaseCubes, DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        for (int i = 0; i < diseaseCubes; i++) {
            this.city.infect(this.city.defaultColor(), diseaseCubeBank, outbreakManager);
        }
    }

    public CityColor getCityColor() {
        return this.city.defaultColor();
    }

    public String toString() {
        return city.name;
    }
}