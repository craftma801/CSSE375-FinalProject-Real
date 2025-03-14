package main;

public class OutbreakManager {
    private int outbreaks;
    private final GameWindowInterface gameWindow;

    public OutbreakManager(GameWindowInterface gameWindow) {
        this.outbreaks = 0;
        this.gameWindow = gameWindow;
    }

    public int getOutbreaks() {
        return outbreaks;
    }

    public void incrementOutbreaks() {
        outbreaks++;
        gameWindow.updateOutbreaks(outbreaks);
    }
}
