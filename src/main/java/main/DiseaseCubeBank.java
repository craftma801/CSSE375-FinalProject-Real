package main;

public class DiseaseCubeBank {
    private int yellowCubes;
    private int redCubes;
    private int blueCubes;
    private int blackCubes;

    public DiseaseCubeBank() {
        this.yellowCubes = 24;
        this.redCubes = 24;
        this.blueCubes = 24;
        this.blackCubes = 24;
    }

    public int remainingCubes(CityColor cityColor) {
        return switch (cityColor) {
            case BLUE -> blueCubes;
            case RED -> redCubes;
            case YELLOW -> yellowCubes;
            case BLACK -> blackCubes;
            default -> -1;
        };
    }

    public void cityInfected(CityColor cityColor) {
        switch (cityColor) {
            case BLUE -> {
                blueCubes--;
            }
            case RED -> {
                redCubes--;
            }
            case YELLOW -> {
                yellowCubes--;
            }
            case BLACK -> {
                blackCubes--;
            }
            default -> {
                throw new InvalidColorException(cityColor + " is not a valid color!");
            }
        }
    }

    public void colorTreated(CityColor cityColor) {
        switch (cityColor) {
            case BLUE -> {
                blueCubes++;
            }
            case RED -> {
                redCubes++;
            }
            case YELLOW -> {
                yellowCubes++;
            }
            case BLACK -> {
                blackCubes++;
            }
            default -> {
                throw new InvalidColorException(cityColor + " is not a valid color!");
            }
        }
    }
}
