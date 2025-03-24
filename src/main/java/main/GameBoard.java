package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class GameBoard extends JComponent {
    public static final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
    private final Image background;
    private final ArrayList<City> cities;
    private CompletableFuture<City> selectedCity;
    private boolean canSelectCity; //Whether or not the user can currently select a city
    private InfoBox selectionInfo;

    public GameBoard(ArrayList<City> cities) {
        background = defaultToolkit.getImage("assets/PandemicBoard.png");
        this.cities = cities;
        this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        canSelectCity = false;
        selectionInfo = new InfoBox(5, 5, 150, "Please select a city.");
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = ((Graphics2D) graphics);
        super.paintComponent(graphics2D);
        Dimension boardSize = this.getSize();
        graphics.drawImage(background, 0, 0, boardSize.width, boardSize.height, this);
        double xScale = boardSize.width / (double) Pandemic.BOARD_WIDTH;
        double yScale = boardSize.height / (double) Pandemic.BOARD_HEIGHT;
        for (City city : cities) {
            city.draw(graphics2D, this, xScale, yScale);
        }
        if(canSelectCity) {
            selectionInfo.draw(graphics2D);
        }
        System.out.println(this.getSize());
    }

    public CompletableFuture<City> selectCity() {
        selectedCity = new CompletableFuture<>();
        canSelectCity = true;
        return selectedCity;
    }

    @Override
    protected void processMouseEvent(MouseEvent m) {
        if(m.getID() == MouseEvent.MOUSE_PRESSED) {
            for (City city : cities) {
                if(city.isClicked(m.getX(), m.getY())) {
                    System.out.println(city.name);
                    if(canSelectCity) {
                        selectedCity.complete(city);
                        canSelectCity = false;
                        this.repaint();
                    }
                }
            }
            System.out.println(m.getX() + ", " + m.getY());
        }
    }
}
