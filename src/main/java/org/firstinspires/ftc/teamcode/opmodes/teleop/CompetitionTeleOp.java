package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;
import smile.interpolation.Interpolation;
import smile.interpolation.LinearInterpolation;


@TeleOp(name = "TeleOp", group = "Competition")
public class CompetitionTeleOp extends RobotOpMode {
    private static final Interpolation flywheelVelocity = new LinearInterpolation(new double[]{6.15, 29.2, 53, 76.7, 103.5, 124.1}, new double[]{1810, 1710, 1680, 1670, 1780, 1950});
    private static final Interpolation turretAngle = new LinearInterpolation(new double[]{6.15, 29.2, 53, 76.7, 103.5, 124.1}, new double[]{44, 63, 88, 113, 136, 145});
//    private static final Interpolation upperIntake = new LinearInterpolation(new double[]{6.15, 29.2, 53, 76.8, 103.4, 135.4}, new double[]{0.8, 0.7, 0.5, 0.6, 0.7, 0.5});
//    private static final Interpolation lowerIntake = new LinearInterpolation(new double[]{6.15, 29.2, 53, 76.8, 103.4, 135.4}, new double[]{1, 0.8, 0.8, 0.8, 0.8, 0.8});

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
            flywheel.setTarget(flywheelVelocity.interpolate(x));
            turret.setTargetDegrees(turretAngle.interpolate(x) - Math.toDegrees(drivetrain.pose().heading()));

            if (gamepad1.right_bumper || sensors.ballCount() == 4) intake.lower.off().schedule();
            else intake.lower.setPower(1).schedule();

            if (gamepad1.right_trigger_pressed) intake.upper.setPower(0.5).schedule();
            else intake.upper.off().schedule();

            if (gamepad1.left_trigger_pressed) corbicula.down();
            else corbicula.up();
        });
    }
}
