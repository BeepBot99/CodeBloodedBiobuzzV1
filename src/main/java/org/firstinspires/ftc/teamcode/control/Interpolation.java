package org.firstinspires.ftc.teamcode.control;

import smile.interpolation.BilinearInterpolation;
import smile.interpolation.Interpolation2D;

public final class Interpolation {
    public static final Interpolation2D flywheelVelocity = new BilinearInterpolation(
            new double[]{6.15, 29.2, 53, 76.7, 103.5, 124.1},
            new double[]{6.15, 20.5},
            new double[][]{
//                    {1810, 1710, 1680, 1670, 1780, 1950},
//                    {1770, 1655, 1680, 1670, 1720, 1865}
                    {1850, 1760},
                    {1740, 1650},
                    {1685, 1655},
                    {1670, 1660},
                    {1800, 1720},
                    {1960, 1875}
            }
    );

    public static double getFlywheelVelocity(double x, double y) {
        return flywheelVelocity.interpolate(x, -Math.abs(y - 70.75) + 70.75);
    }

    private static final Interpolation2D turretAngle = new BilinearInterpolation(
            new double[]{6.15, 29.2, 53, 76.7, 103.5, 124.1},
            new double[]{6.15, 20.5},
            new double[][]{
//                    {44, 63, 88, 113, 136, 145},
//                    {33, 53, 89, 122, 147, 155}
                    {44, 35},
                    {63, 53},
                    {88, 89},
                    {115, 122.5},
                    {138, 148.5},
                    {147, 157.5}
            }
    );
}
