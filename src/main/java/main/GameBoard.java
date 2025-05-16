package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class GameBoard extends JComponent {
    private final ArrayList<City> cities;
    private HashSet<City> selectableCities;
    private CompletableFuture<City> selectedCity;
    private boolean canSelectCity;
    private boolean displayingAlert;

    public GameBoard(ArrayList<City> cities) {
        this.cities = cities;
        this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        canSelectCity = false;
        displayingAlert = false;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = ((Graphics2D) graphics);
        super.paintComponent(graphics2D);
        Dimension boardSize = this.getSize();
        graphics.drawImage(Toolkit.getDefaultToolkit().getImage("resources/PandemicBoard.png"), 0, 0, boardSize.width, boardSize.height, this);
        double xScale = boardSize.width / (double) Pandemic.BOARD_WIDTH;
        double yScale = boardSize.height / (double) Pandemic.BOARD_HEIGHT;
        City.setUIScale(xScale, yScale);
        for (City city : cities) {
            city.draw(graphics2D, this, !canSelectCity || selectableCities.contains(city));
        }
        if(canSelectCity) {
            new InfoBox(5, 5, 150, "Please select a city.").draw(graphics2D);
            if(displayingAlert){
                new InfoBox(5, 50, 300, "Alert").draw(graphics2D);
            }
        }
        System.out.println(this.getSize());
    }

    public CompletableFuture<City> selectCity(HashSet<City> options) {
        selectedCity = new CompletableFuture<>();
        selectableCities = options;
        repaint(); //We need to repaint now, so that the non-selectable cities get greyed out.
        canSelectCity = true;
        return selectedCity;
    }

    @Override
    protected void processMouseEvent(MouseEvent m) {
        if(m.getID() == MouseEvent.MOUSE_PRESSED) {
            for (City city : cities) {
                if(city.isClicked(m.getX(), m.getY())) {
                    System.out.println(city.name);
                    if(canSelectCity && selectableCities.contains(city)) {
                        selectedCity.complete(city);
                        canSelectCity = false;
                        displayingAlert = false;
                        this.repaint();
                    }
                    else if(canSelectCity){
                        JOptionPane.showMessageDialog(null, "You are not allowed to select that city with this action.");
                        return;
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "No action selected.");
                    }
                }
            }
            System.out.println(m.getX() + ", " + m.getY());
        }
    }
}
