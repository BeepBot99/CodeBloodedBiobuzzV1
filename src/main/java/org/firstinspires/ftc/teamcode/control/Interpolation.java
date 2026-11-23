package org.firstinspires.ftc.teamcode.control;

import smile.interpolation.BilinearInterpolation;
import smile.interpolation.Interpolation2D;

public final class Interpolation {
    private static final Interpolation2D flywheelVelocity = new BilinearInterpolation(
            new double[]{10.6, 29.2, 53, 76.7, 103.5, 124.1},
            new double[]{6.15, 20.5},
            new double[][]{
                    {1850, 1760},
                    {1750, 1650},
                    {1685, 1655},
                    {1670, 1660},
                    {1800, 1720},
                    {1960, 1875}
            }
    );

    private static final Interpolation2D airTime = new BilinearInterpolation(
            new double[]{10.6, 29.2, 53, 76.7, 103.5, 124.1},
            new double[]{6.15, 20.5},
            new double[][]{
                    {0.87, 0.79},
                    {0.76, 0.67},
                    {0.59, 0.45},
                    {0.73, 0.53},
                    {0.74, 0.81},
                    {0.91, 0.9}
            }
    );

    public static double getFlywheelVelocity(double x, double y) {
        double flywheel = flywheelVelocity.interpolate(x, prepareY(y));
        if (y > 70.5) {
            return flywheel * 0.96;
        }
        return flywheel;
    }

    private static double prepareY(double y) {
        return -Math.abs(y - 70.5) + 70.5;
    }

    public static double getAirTime(double x, double y) {
        return airTime.interpolate(x, prepareY(y));
    }
}
