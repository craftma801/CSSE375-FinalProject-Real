package main;

import main.roles.QuarantineSpecialist;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class City {
    public static final int RESEARCH_STATION_WIDTH = 20;
    public static final int RESEARCH_STATION_HEIGHT = 25;
    public static final int CUBE_SIZE = 8;
    public static final int CUBE_GAP = 4;

    private static final int PAWN_WIDTH = 20;
    private static final int PAWN_HEIGHT = 35;
    private static final int PAWN_GAP = 2;

    private static final int CITY_RADIUS = 38;

    public final String name;
    public final int x;
    public final int y;
    private double xScale;
    private double yScale;

    public final CityColor color;

    public ArrayList<Player> players;
    public ArrayList<City> connectedCities;

    private final HashMap<CityColor, Integer> diseaseLevels;

    private boolean hasResearchStation;
    public boolean outbreakIsHappening;

    public City(String cityName, int x, int y, CityColor color) {
        this.name = cityName;
        this.x = x;
        this.y = y;
        this.color = color;
        this.players = new ArrayList<>();
        diseaseLevels = new HashMap<>();
        diseaseLevels.put(CityColor.YELLOW, 0);
        diseaseLevels.put(CityColor.RED, 0);
        diseaseLevels.put(CityColor.BLUE, 0);
        diseaseLevels.put(CityColor.BLACK, 0);
        this.hasResearchStation = false;
        this.connectedCities = new ArrayList<>();
        this.outbreakIsHappening = false;
    }

    public int getInfectionLevel(CityColor color) {
        return diseaseLevels.getOrDefault(color, 0);
    }

    public boolean treatDisease(CityColor infectionColor, DiseaseCubeBank diseaseCubeBank) {
        return diseaseCubeBank.treatDiseaseAt(this, infectionColor, false);
    }

    public boolean medicTreatDisease(CityColor infectionColor, DiseaseCubeBank diseaseCubeBank) {
        return diseaseCubeBank.treatDiseaseAt(this, infectionColor, true);
    }

    // treatDisease will be pulled mostly into Disease Cube Bank so that will handle feature envy, and small class
    // disease cube bank.
//    public boolean treatDisease(CityColor infectionColor, DiseaseCubeBank diseaseCubeBank, boolean isMedic) {
//        int diseaseAmount = diseaseLevels.get(infectionColor);
//        if (diseaseAmount > 0) {
//            if (isMedic) {
//                diseaseLevels.put(infectionColor, 0);
//                for (int i = diseaseAmount; i > 0; i--) {
//                    diseaseCubeBank.colorTreated(infectionColor);
//                }
//                return true;
//            } else {
//                diseaseLevels.put(infectionColor, diseaseAmount - 1);
//                diseaseCubeBank.colorTreated(infectionColor);
//                return true;
//            }
//        }
//        return false;
//    }


    public void addCube(CityColor color){
        diseaseLevels.put(color, getInfectionLevel(color) + 1);
    }

    public void removeCube(CityColor color){
        int current = getInfectionLevel(color);
        if(current > 0){
            diseaseLevels.put(color, current-1);
        }
    }

    public void removeAllCubes(CityColor color){
        diseaseLevels.put(color, 0);
    }

    private boolean hasQuarantineSpecialist() {
        for (Player player : players)
            if(player.getClass() == QuarantineSpecialist.class)
                return true;
        return false;
    }

    public boolean quarantineSpecialistNearby() {
        for(City city : connectedCities)
            if(city.hasQuarantineSpecialist())
                return true;
        return this.hasQuarantineSpecialist();
    }

    // Infect will be pulled mostly into Disease Cube Bank so that will handle feature envy, and small class
    // disease cube bank.
//    public void infect(CityColor infectionColor, DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
//        if (quarantineSpecialistNearby())
//            return;
//        if (diseaseLevels.get(infectionColor) >= 3) {
//            outbreak(infectionColor, diseaseCubeBank, outbreakManager);
//        } else {
//            diseaseLevels.put(infectionColor, diseaseLevels.get(infectionColor) + 1);
//            diseaseCubeBank.cityInfected(infectionColor);
//        }
//    }

    public void outbreak(CityColor diseaseColor, DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        if (!this.outbreakIsHappening) {
            this.outbreakIsHappening = true;
            for (City toInfect : this.connectedCities) {
                diseaseCubeBank.infectCity(toInfect, diseaseColor, outbreakManager);
            }
            outbreakManager.incrementOutbreaks();
        }
    }

    public CityColor defaultColor() {
        return this.color;
    }

    public boolean hasResearchStation() {
        return this.hasResearchStation;
    }

    public void buildResearchStation() {
        this.hasResearchStation = true;
    }

    public void addPawn(Player player) {
        players.add(player);
    }

    private int getTotalNumCubes() {
        int total = 0;
        for(int diseaseLevel : diseaseLevels.values()){
            total += diseaseLevel;
        }
        return total;
    }

    public void draw(Graphics2D graphics2D, JComponent observer, double xScale, double yScale, boolean enabled) {
        this.xScale = xScale;
        this.yScale = yScale;
        int scaledRadius = (int) (CITY_RADIUS * xScale);
        Color drawColor = switch (this.color) {
            case YELLOW -> new Color(209, 187, 88);
            case RED -> new Color(188, 75, 75);
            case BLACK -> new Color(80, 80, 80);
            default -> new Color(96, 96, 236);
        };
        if(enabled) {
            graphics2D.setColor(drawColor);
        } else {
            graphics2D.setColor(drawColor.darker().darker());
        }
        graphics2D.fillOval(this.x - scaledRadius, this.y - scaledRadius, 2*scaledRadius, 2*scaledRadius);

        if (hasResearchStation()) {
            drawResearchStation(graphics2D, getTotalNumCubes());
        }
        if (getTotalNumCubes() > 0) {
            drawCubes(graphics2D, getTotalNumCubes());
        }
        drawPawns(graphics2D, observer);
    }

    private void drawPawns(Graphics2D graphics2D, JComponent observer) {
        int numPawns = players.size();
        int pawnBlockSize = numPawns * PAWN_WIDTH + (numPawns - 1) * PAWN_GAP;
        int pawnBlockX = this.x - (pawnBlockSize) / 2;
        int pawnBlockY = this.y - PAWN_HEIGHT;

        FontMetrics fm = graphics2D.getFontMetrics();
        graphics2D.setFont(new Font("Indicator", Font.BOLD, 12));

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Image coloredPawn = player.getIcon();
            int pawnX = pawnBlockX + i * (PAWN_WIDTH + PAWN_GAP);

            graphics2D.setColor(player.getColor());
            graphics2D.drawImage(coloredPawn, pawnX, pawnBlockY, PAWN_WIDTH, PAWN_HEIGHT, observer);

            int pNumWidth = fm.stringWidth(player.playerNum);
            graphics2D.drawString(player.playerNum, pawnX + (PAWN_WIDTH - pNumWidth) / 2, pawnBlockY);

            String role = player.getDisplayName();
            int roleWidth = fm.stringWidth(role);
            graphics2D.drawString(role, pawnX + (PAWN_WIDTH - roleWidth) / 2, pawnBlockY + PAWN_HEIGHT + 12);
        }
    }

    private void drawCubes(Graphics2D graphics2D, int totalCubes) {
        int xOffset = hasResearchStation() ? -CUBE_GAP : CUBE_SIZE + (CUBE_GAP / 2);
        int yOffset = this.players.isEmpty() ? -10 : 5;

        int gridX = this.x + xOffset;
        int gridY = this.y + yOffset;

        Color cubeColor;
        int yellowRemaining = diseaseLevels.get(Color.YELLOW);
        int redRemaining = diseaseLevels.get(Color.RED);
        int blueRemaining = diseaseLevels.get(Color.BLUE);
        for (int i = 0; i < totalCubes; i++) {
            if (yellowRemaining > 0) {
                cubeColor = Color.YELLOW;
                yellowRemaining--;
            } else if (redRemaining > 0) {
                cubeColor = Color.RED;
                redRemaining--;
            } else if (blueRemaining > 0) {
                cubeColor = Color.BLUE;
                blueRemaining--;
            } else {
                cubeColor = Color.BLACK;
            }
            graphics2D.setColor(cubeColor);

            int cubeOffset = i % 2 == 0 ? CUBE_SIZE : 2 * CUBE_SIZE + CUBE_GAP;
            int cubeX = gridX - cubeOffset;
            int cubeY = gridY + (i / 2) * (CUBE_SIZE + CUBE_GAP);
            Rectangle cube = new Rectangle(cubeX, cubeY, CUBE_SIZE, CUBE_SIZE);
            graphics2D.fill(cube);
        }
    }

    private void drawResearchStation(Graphics2D graphics2D, int totalCubes) {
        graphics2D.setColor(Color.LIGHT_GRAY);

        int xOffset = totalCubes > 0 ? 0 : -(RESEARCH_STATION_WIDTH / 2);
        int yOffset = players.isEmpty() ? 10 : 25;

        int stationX = this.x + xOffset;
        int stationY = this.y + yOffset;

        int baseRectHeight = (int) (RESEARCH_STATION_HEIGHT * 0.6);
        int baseRectY = stationY - baseRectHeight;

        Rectangle researchStationBase = new Rectangle(stationX, baseRectY, RESEARCH_STATION_WIDTH, baseRectHeight);
        Polygon researchStationRoof = new Polygon(new int[]{stationX, stationX + (RESEARCH_STATION_WIDTH / 2),
                stationX + RESEARCH_STATION_WIDTH}, new int[]{stationY - baseRectHeight,
                stationY - RESEARCH_STATION_HEIGHT, stationY - baseRectHeight}, 3);
        graphics2D.fill(researchStationBase);
        graphics2D.fill(researchStationRoof);
    }

    public void addConnection(City connection) {
        connectedCities.add(connection);
    }

    public boolean isClicked(int x, int y) {
        return getScaledDistance(x, y) < 25;
    }

    public double getScaledDistance(int destX, int destY) {
        double xDist = Math.abs(destX - this.x) * xScale;
        double yDist = Math.abs(destY - this.y) * yScale;
        return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
    }

    public int getScaledX() {
        return (int) (this.x * xScale);
    }

    public int getScaledY() {
        return (int) (this.y * yScale);
    }

}
