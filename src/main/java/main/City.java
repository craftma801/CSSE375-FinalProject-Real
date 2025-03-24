package main;

import main.roles.QuarantineSpecialist;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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
    public final Point location;

    public final CityColor color;

    public ArrayList<Player> players;
    public ArrayList<City> connectedCities;

    private int yellowLevel;
    private int redLevel;
    private int blueLevel;
    private int blackLevel;
    private boolean hasResearchStation;
    public boolean outbreakIsHappening;

    public City(String cityName, Point cityLocation, CityColor color) {
        this.name = cityName;
        this.location = cityLocation;
        this.color = color;
        this.players = new ArrayList<>();
        this.yellowLevel = 0;
        this.redLevel = 0;
        this.blueLevel = 0;
        this.blackLevel = 0;
        this.hasResearchStation = false;
        this.connectedCities = new ArrayList<>();
        this.outbreakIsHappening = false;
    }

    public int getInfectionLevel(CityColor color) {
        return switch (color) {
            case YELLOW -> yellowLevel;
            case RED -> redLevel;
            case BLUE -> blueLevel;
            case BLACK -> blackLevel;
            default -> 0;
        };
    }

    public boolean treatDisease(CityColor infectionColor, DiseaseCubeBank diseaseCubeBank) {
        switch (infectionColor) {
            case YELLOW -> {
                return (treatYellow(false, diseaseCubeBank));
            }
            case RED -> {
                return (treatRed(false, diseaseCubeBank));
            }
            case BLUE -> {
                return (treatBlue(false, diseaseCubeBank));
            }
            case BLACK -> {
                return (treatBlack(false, diseaseCubeBank));
            }
        }
        return false;
    }

    public boolean medicTreatDisease(CityColor infectionColor, DiseaseCubeBank diseaseCubeBank) {
        switch (infectionColor) {
            case YELLOW -> {
                return (treatYellow(true, diseaseCubeBank));
            }
            case RED -> {
                return (treatRed(true, diseaseCubeBank));
            }
            case BLUE -> {
                return (treatBlue(true, diseaseCubeBank));
            }
            case BLACK -> {
                return (treatBlack(true, diseaseCubeBank));
            }
        }
        throw new InvalidColorException(infectionColor + " is not a valid city color");
    }

    private boolean treatBlack(boolean isMedic, DiseaseCubeBank diseaseCubeBank) {
        if (this.blackLevel > 0) {
            if (isMedic) {
                int blackAmount = blackLevel;
                for (int i = blackAmount; i > 0; i--) {
                    this.blackLevel--;
                    diseaseCubeBank.colorTreated(CityColor.BLACK);
                }
                return true;
            } else {
                this.blackLevel--;
                diseaseCubeBank.colorTreated(CityColor.BLACK);
                return true;
            }
        }
        return false;
    }

    private boolean treatBlue(boolean isMedic, DiseaseCubeBank diseaseCubeBank) {
        if (this.blueLevel > 0) {
            if (isMedic) {
                int blueAmount = blueLevel;
                for (int i = blueAmount; i > 0; i--) {
                    this.blueLevel--;
                    diseaseCubeBank.colorTreated(CityColor.BLUE);
                }
                return true;
            } else {
                this.blueLevel--;
                diseaseCubeBank.colorTreated(CityColor.BLUE);
                return true;
            }
        }
        return false;
    }

    private boolean treatRed(boolean isMedic, DiseaseCubeBank diseaseCubeBank) {
        if (this.redLevel > 0) {
            if (isMedic) {
                int redAmount = redLevel;
                for (int i = redAmount; i > 0; i--) {
                    this.redLevel--;
                    diseaseCubeBank.colorTreated(CityColor.RED);
                }
                return true;
            } else {
                this.redLevel--;
                diseaseCubeBank.colorTreated(CityColor.RED);
                return true;
            }
        }
        return false;
    }

    private boolean treatYellow(boolean isMedic, DiseaseCubeBank diseaseCubeBank) {
        if (this.yellowLevel > 0) {
            if (isMedic) {
                int yellowAmount = yellowLevel;
                for (int i = yellowAmount; i > 0; i--) {
                    this.yellowLevel--;
                    diseaseCubeBank.colorTreated(CityColor.YELLOW);
                }
                return true;
            } else {
                this.yellowLevel--;
                diseaseCubeBank.colorTreated(CityColor.YELLOW);
                return true;
            }
        }
        return false;
    }

    public void infect(CityColor infectionColor, DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        if (QuarantineSpecialist.blockingInfection(this))
            return;
        switch (infectionColor) {
            case YELLOW -> {
                infectYellow(diseaseCubeBank, outbreakManager);
            }
            case RED -> {
                infectRed(diseaseCubeBank, outbreakManager);
            }
            case BLUE -> {
                infectBlue(diseaseCubeBank, outbreakManager);
            }
            case BLACK -> {
                infectBlack(diseaseCubeBank, outbreakManager);
            }
        }
    }

    public void infectYellow(DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        if (this.yellowLevel >= 3) {
            outbreak(CityColor.YELLOW, diseaseCubeBank, outbreakManager);
        } else {
            this.yellowLevel++;
            diseaseCubeBank.cityInfected(CityColor.YELLOW);
        }
    }

    public void infectBlack(DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        if (this.blackLevel >= 3) {
            outbreak(CityColor.BLACK, diseaseCubeBank, outbreakManager);
        } else {
            this.blackLevel++;
            diseaseCubeBank.cityInfected(CityColor.BLACK);
        }
    }

    public void infectBlue(DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        if (this.blueLevel >= 3) {
            outbreak(CityColor.BLUE, diseaseCubeBank, outbreakManager);
        } else {
            this.blueLevel++;
            diseaseCubeBank.cityInfected(CityColor.BLUE);
        }
    }

    public void infectRed(DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        if (this.redLevel >= 3) {
            outbreak(CityColor.RED, diseaseCubeBank, outbreakManager);
        } else {
            this.redLevel++;
            diseaseCubeBank.cityInfected(CityColor.RED);
        }
    }

    public void outbreak(CityColor diseaseColor, DiseaseCubeBank diseaseCubeBank, OutbreakManager outbreakManager) {
        if (!this.outbreakIsHappening) {
            this.outbreakIsHappening = true;
            for (City toInfect : this.connectedCities) {
                toInfect.infect(diseaseColor, diseaseCubeBank, outbreakManager);
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

    public void draw(Graphics2D graphics2D, JComponent observer, double xScale, double yScale) {
        location.setScale(xScale, yScale);

        int scaledRadius = (int) (CITY_RADIUS * xScale);
        Color drawColor;
        switch (this.color) {
            case YELLOW:
                drawColor = new Color(246, 220, 104);
                break;
            case RED:
                drawColor = new Color(255, 142, 142);
                break;
            case BLACK:
                drawColor = new Color(76, 76, 76);
                break;
            default:
                drawColor = new Color(128, 128, 255);
        }
        graphics2D.setColor(drawColor.darker().darker());
        graphics2D.fillOval(location.getX() - scaledRadius, location.getY() - scaledRadius, 2*scaledRadius, 2*scaledRadius);

        int totalCubes = blueLevel + blackLevel + redLevel + yellowLevel;
        if (hasResearchStation()) {
            drawResearchStation(graphics2D, totalCubes);
        }
        if (totalCubes > 0) {
            drawCubes(graphics2D, totalCubes);
        }
        drawPawns(graphics2D, observer);
    }

    private void drawPawns(Graphics2D graphics2D, JComponent observer) {
        int numPawns = players.size();
        int pawnBlockSize = numPawns * PAWN_WIDTH + (numPawns - 1) * PAWN_GAP;
        int pawnBlockX = location.getX() - (pawnBlockSize) / 2;
        int pawnBlockY = location.getY() - PAWN_HEIGHT;
        for (int i = 0; i < players.size(); i++) {
            Image coloredPawn = players.get(i).getIcon();
            graphics2D.drawImage(coloredPawn, pawnBlockX + i * (PAWN_WIDTH + PAWN_GAP),
                    pawnBlockY, PAWN_WIDTH, PAWN_HEIGHT, observer);
        }
    }

    private void drawCubes(Graphics2D graphics2D, int totalCubes) {
        int xOffset = hasResearchStation() ? -CUBE_GAP : CUBE_SIZE + (CUBE_GAP / 2);
        int yOffset = this.players.isEmpty() ? -10 : 5;

        int gridX = this.location.getX() + xOffset;
        int gridY = this.location.getY() + yOffset;

        Color cubeColor;
        int yellowRemaining = this.yellowLevel;
        int redRemaining = this.redLevel;
        int blueRemaining = this.blueLevel;
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

        int stationX = location.getX() + xOffset;
        int stationY = location.getY() + yOffset;

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
        return location.getScaledDistance(x, y) < 25;
    }
}
