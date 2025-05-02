package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ActionButton extends JButton {
    public ActionButton(String label) {
        super(label);
        this.setMargin(new Insets(10, 10, 10, 10));
        this.setBackground(new Color(0x64AEFF));
        this.setForeground(Color.WHITE);
        this.setBorder(new EmptyBorder(0, 10, 0, 10));
    }
}
