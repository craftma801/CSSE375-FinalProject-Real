package main;

public class Point {
    private final int x;
    private final int y;
    private double xScale;
    private double yScale;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.xScale = 1;
        this.yScale = 1;
    }

    public void setScale(double xScale, double yScale) {
        this.xScale = xScale;
        this.yScale = yScale;
    }

    public int getX() {
        return (int) (x * xScale);
    }

    public int getY() {
        return (int) (y * yScale);
    }
}
