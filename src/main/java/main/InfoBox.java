package main;

import java.awt.Color;

import java.awt.Graphics2D;

import javax.swing.JLabel;

/**
 * Class: InfoBox
 *
 * @author f23-a-402  <br>
 *         Purpose: Defines InfoBox for UI
 *         <br>
 *
 */
public class InfoBox {
    private int xPos;
    private int yPos;
    private int width;
    private String text;

    public static final Color INFO_BOX_BACKGROUND = Color.BLACK;
    public static final int INFO_BOX_HEIGHT = 40;
    public static final int INFO_TEXT_Y_OFFSET = 25;
    public static final int INFO_TEXT_X_OFFSET = 5;

    public InfoBox(int xPos, int yPos, int width, String text) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.text = text;
        this.width = width;
    }

    /**
     * ensures: renders infobox
     */

    public void draw(Graphics2D g) {
        g.setColor(INFO_BOX_BACKGROUND);
        g.fillRoundRect(xPos, yPos, width, INFO_BOX_HEIGHT, 10, 10);
        g.setColor(Color.WHITE);
        g.drawString(text, xPos + INFO_TEXT_X_OFFSET, yPos + INFO_TEXT_Y_OFFSET);
    }
    /**
     * ensures: returns xpos of infobox
     */
    public int getxPos() {
        return xPos;
    }

    /**
     * ensures: sets xpos of infobox
     * @param xpos as int
     */

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    /**
     * ensures: returns ypos of infobox
     */

    public int getyPos() {
        return yPos;
    }

    /**
     * ensures: sets ypos of infobox
     * @param: ypos as int
     */
    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    /**
     * ensures: returns with of infobox
     */
    public int getWidth() {
        return width;
    }

    /**
     * ensures: sets width of infobox
     * @param: width as int
     */

    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * ensures: returns string content
     */

    public String getText() {
        return text;
    }

    /**
     * ensures: sets Text of infobox
     * @param: String for infobox text
     */
    public void setText(String text) {
        this.text = text;
    }
}