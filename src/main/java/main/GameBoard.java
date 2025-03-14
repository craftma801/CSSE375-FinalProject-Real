package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameBoard extends JComponent {
    public static final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
    private final Image background;
    private final ArrayList<City> cities;

    public GameBoard(ArrayList<City> cities) {
        background = defaultToolkit.getImage("assets/PandemicBoard.png");
        this.cities = cities;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = ((Graphics2D) graphics);
        super.paintComponent(graphics2D);
        graphics.drawImage(background, 0, 0, Pandemic.BOARD_WIDTH, Pandemic.BOARD_HEIGHT, this);
        for (City city : cities) {
            city.draw(graphics2D, this);
        }
    }
}
