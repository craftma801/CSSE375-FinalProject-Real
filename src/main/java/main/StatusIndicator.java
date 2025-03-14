package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatusIndicator extends JPanel {
    JLabel valueLabel;

    public StatusIndicator(String labelText, String defaultValue) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JLabel indLabel = new JLabel(labelText);
        this.valueLabel = new JLabel(defaultValue);
        indLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.valueLabel.setFont(new Font("Indicator", Font.BOLD, 24));

        this.setBorder(new EmptyBorder(0, 10, 0, 10));

        this.add(indLabel);
        this.add(this.valueLabel);
    }

    public void updateValue(String newText) {
        this.valueLabel.setText(newText);
    }
}
