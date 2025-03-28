package main;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Pandemic {
    public static final int BOARD_WIDTH = 2200;
    public static final int BOARD_HEIGHT = 1400;
    public static final Dimension BOARD_SIZE = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private static BoardStatusController boardStatusController;
    public static ResourceBundle bundle;
    public static Locale locale;

    public static void main(String[] args) {
        locale = GameWindow.selectLocale("Select your Locale");
        bundle = ResourceBundle.getBundle("messages", locale);
        ArrayList<City> cityMap = createMap();
        boardStatusController = new BoardStatusController(new GameWindow(cityMap), cityMap);
        boardStatusController.setup();
        boardStatusController.startGame();
        boardStatusController.displayGame();
    }

    public static void handleAction(PlayerAction playerAction) {
        boardStatusController.handleAction(playerAction);
    }

    private static City createCity(String name, Point location, CityColor color) {
        return new City(name, location, color);
    }

    public static ArrayList<City> createMap() {
        List<CityData> cityDataList = WorldMapLoader.loadCityData();
        Map<String, City> cityMap = createCities(cityDataList);
        addConnections(cityMap, cityDataList);
        return new ArrayList<>(cityMap.values());
    }

    private static Map<String, City> createCities(List<CityData> cityDataList) {
        Map<String, City> cityMap = new HashMap<>();
        for (CityData data : cityDataList) {
            City city = createCity(bundle.getString(data.name), new Point(data.x, data.y), CityColor.valueOf(data.color));
            cityMap.put(data.name, city);
        }
        return cityMap;
    }

    private static void addConnections(Map<String, City> cityMap, List<CityData> cityDataList) {
        for (CityData data : cityDataList) {
            City from = cityMap.get(data.name);
            for (String connectionName : data.connections) {
                City to = cityMap.get(connectionName);
                if (to != null) {
                    from.addConnection(to);
                }
            }
        }
    }
}
