package eu.software4you.math;

import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;

public class MathUtils {

    public static Coordinate2D getCircleCoordinateFromKnownX(double r, double x) {
		/*
		 x² + y² = r² | -x²
		 y² = r² - x² | sqrt
		  */
        if (x > r)
            throw new ArithmeticException("Cannot calculate circle coordinate from x bigger than the radius");
        return new Coordinate2D(x, Math.sqrt((r * r) - (x * x)));
    }

    public static List<Coordinate2D> getCircleCoordinates(double radius, int amount) {
        return getCircleCoordinates(radius, amount, 0);
    }

    /**
     * returns a collection of 2d coordinates which form a circle
     *
     * @param radius   the radius
     * @param amount   the amount of points wanted
     * @param starting the degree of the first point
     * @return
     */
    public static List<Coordinate2D> getCircleCoordinates(double radius, int amount, double starting) {

        List<Coordinate2D> coordinates = new ArrayList<>();
        /*for (double d = radius * -1; d <= radius; d += (radius / (double) amount) * 4) {

            Coordinate2D positive = getCircleCoordinateFromKnownX(radius, d);
            coordinates.add(positive);

            if (positive.getX() == 0)
                continue;

            Coordinate2D negative = new Coordinate2D(positive.getX(), positive.getY() * -1d);
            coordinates.add(negative);
        }*/
        Validate.isTrue(radius > 0, "Cannot calculate circle with negative radius");
        Validate.isTrue(amount > 0, "Cannot calculate circle with negative amount of points");

        double degreeDistance = 360d / (double) amount;
        for (double degree = starting; degree < starting + 360d; degree += degreeDistance) {
            coordinates.add(rotateCoordinate2D(new Coordinate2D(0, -radius), degree));
        }
        return coordinates;
    }

    public static Coordinate2D rotateCoordinate2D(Coordinate2D in, double deg) {
        if (deg == 0 || deg % 360 == 0)
            return in;
        double a = Math.toRadians(deg);
        double x = in.getX();
        double y = in.getY();

        double newX = (Math.cos(a) * x - Math.sin(a) * y);
        double newY = (Math.sin(a) * x + Math.cos(a) * y);

        return new Coordinate2D(newX, newY);
    }

    public static boolean isBetween(int end1, int end2, int looking) {
        return end2 > end1 ? looking > end1 && looking < end2 : looking > end2 && looking < end1;
    }

    /**
     * Generates a roman number from a decimal one.
     *
     * @param num decimal input number
     * @return roman number
     */
    public static String decToRoman(int num) {
        int[] dec = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanLiterals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        for (int i = 0; i < dec.length; i++) {
            while (num >= dec[i]) {
                num -= dec[i];
                roman.append(romanLiterals[i]);
            }
        }
        return roman.toString();
    }
}
