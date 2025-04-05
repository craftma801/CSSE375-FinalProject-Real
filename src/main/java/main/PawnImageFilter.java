package main;

import java.awt.*;
import java.awt.image.RGBImageFilter;

// Safe to remove entirely
public class PawnImageFilter extends RGBImageFilter {
    private final Color color;

    public PawnImageFilter(Color color) {
        canFilterIndexColorModel = true;
        this.color = color;
    }

    @Override
    public int filterRGB(int x, int y, int rgb) {
        if (rgb > 0) {
            return 0x00000000;
        } else {
            return color.getRGB();
        }
    }
}
