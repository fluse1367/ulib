package eu.software4you.ulib.core.api.math;

public class Coordinate3D {
    public static final Coordinate3D ORIGIN = new Coordinate3D(0, 0, 0);

    private final double x;
    private final double y;
    private final double z;

    public Coordinate3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
