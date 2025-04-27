package tests;

import main.*;

import java.util.ArrayList;

public class TestBSC {

    public City chicago;
    public City atlanta;
    public City london;
    public City newYork;
    public City sanFrancisco;
    public City montreal;
    public City washington;
    public City miami;

    private GameWindowInterface gameWindow;
    private ArrayList<City> map;
    private int numPlayers;
    private int numEpidemicCards;

    public TestBSC() {
    }

    public TestBSC withGameWindow(GameWindowInterface gameWindow) {
        this.gameWindow = gameWindow;
        return this;
    }

    public TestBSC withDefaultMap() {
        this.chicago = new City("Chicago", 315, 405, CityColor.BLUE);
        this.atlanta = new City("Atlanta", 380, 515, CityColor.BLUE);
        this.london = new City("London", 0, 0, CityColor.BLUE);
        this.sanFrancisco = new City("San Francisco", 105, 460, CityColor.BLUE);
        this.montreal = new City("Montreal", 485, 400, CityColor.BLUE);
        this.washington = new City("Washington", 555, 510, CityColor.BLUE);
        this.newYork = new City("New York", 610, 420, CityColor.BLUE);
        this.miami = new City("Miami", 485, 635, CityColor.YELLOW);

        this.chicago.addConnection(this.atlanta);
        this.chicago.addConnection(this.montreal);
        this.montreal.addConnection(this.newYork);
        this.montreal.addConnection(this.chicago);
        this.newYork.addConnection(this.montreal);
        this.atlanta.addConnection(this.chicago);

        ArrayList<City> basicMap = new ArrayList<>();
        basicMap.add(this.chicago);
        basicMap.add(this.atlanta);
        basicMap.add(this.london);
        basicMap.add(this.newYork);
        basicMap.add(this.sanFrancisco);
        basicMap.add(this.montreal);
        basicMap.add(this.washington);
        basicMap.add(this.miami);

        this.map = basicMap;
        return this;
    }

    public TestBSC withMap(ArrayList<City> map) {
        this.map = map;
        return this;
    }

    public TestBSC withPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
        return this;
    }

    public TestBSC withEpidemicCards(int numEpidemicCards) {
        this.numEpidemicCards = numEpidemicCards;
        return this;
    }

    public BoardStatusController build() {
        return new BoardStatusController(gameWindow, map, numPlayers, numEpidemicCards);
    }
}
