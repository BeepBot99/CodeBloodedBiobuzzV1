package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;
import smile.interpolation.BilinearInterpolation;
import smile.interpolation.Interpolation2D;


@TeleOp(name = "TeleOp", group = "Competition")
public class CompetitionTeleOp extends RobotOpMode {
    private static final Interpolation2D flywheelVelocity = new BilinearInterpolation(
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

    @Override
    public void start() {
        flywheel.on();
        intake.lower.on().schedule();
        turret.on();
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
            drivetrain.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, -gamepad1.right_stick_x);

            double x = drivetrain.pose().x();
            double y = drivetrain.pose().y();
            flywheel.setTarget(flywheelVelocity.interpolate(x, -Math.abs(y - 70.75) + 70.75));
            double turretTarget = turretAngle.interpolate(x, -Math.abs(y - 70.75) + 70.75);
            turret.setTargetDegrees((y > 70.75 ? -turretTarget : turretTarget) - Math.toDegrees(drivetrain.pose().heading()));

            if (gamepad1.right_bumper /*|| sensors.hasAllBalls()*/) intake.lower.off().schedule();
            else intake.lower.setPower(1).schedule();

            if (gamepad1.right_trigger_pressed) intake.upper.setPower(0.5).schedule();
            else intake.upper.off().schedule();

            if (gamepad1.left_trigger_pressed) corbicula.down();
            else corbicula.up();
        });
    }
}
