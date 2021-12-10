package eu.software4you.ulib.core.api.math;

public class Coordinate2D {
    public static final Coordinate2D ORIGIN = new Coordinate2D(0, 0);
    private final double x;
    private final double y;

    public Coordinate2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Coordinate3D toCooradiante3D(double h) {
        return new Coordinate3D(getX(), getY(), h);
    }
}
