package org.firstinspires.ftc.teamcode.control;

import smile.interpolation.BilinearInterpolation;
import smile.interpolation.Interpolation2D;

public final class Interpolation {
    public static final Interpolation2D flywheelVelocity = new BilinearInterpolation(
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

    public static double getFlywheelVelocity(double x, double y) {
        return flywheelVelocity.interpolate(x, -Math.abs(y - 70.75) + 70.75);
    }
}
