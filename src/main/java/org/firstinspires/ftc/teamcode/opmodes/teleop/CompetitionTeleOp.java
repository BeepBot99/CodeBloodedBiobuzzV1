package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.ftc.FtcTelemetry;
import org.firstinspires.ftc.teamcode.control.ShootingController;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

@TeleOp(name = "TeleOp", group = "Competition")
public class CompetitionTeleOp extends RobotOpMode {
    private boolean hasMoved = false;

    @Override
    public void start() {
        turret.usePreviousStartingAngle();
    }

    @Override
    public void init_loop() {
        Telemetry.log("Robot/Loop/Time (ms)", loopTimer.milliseconds());
        loopTimer.reset();

        FtcTelemetry.update();
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
            if (!hasMoved && (Math.abs(gamepad1.left_stick_x) > 0.2 || Math.abs(gamepad1.right_stick_x) > 0.2)) {
                hasMoved = true;
                flywheel.on();
                intake.lower.on();
                turret.on();
            }

            drivetrain.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, -gamepad1.right_stick_x);

            shootingController.prepareForLocation(drivetrain.pose(), drivetrain.follower.velocity());

            if (gamepad1.right_bumper /*|| sensors.hasAllBalls()*/) intake.lower.off();
            else if (gamepad1.left_bumper) intake.lower.reverse();
            else intake.lower.on();

            if (gamepad1.right_trigger_pressed) intake.upper.on();
            else if (gamepad1.left_bumper) intake.upper.reverse();
            else intake.upper.off();

            if (gamepad1.left_trigger_pressed) corbicula.down();
            else corbicula.up();

            if (gamepad2.dpadLeftWasPressed()) turret.moveLeft();
            if (gamepad2.dpadRightWasPressed()) turret.moveRight();

//            if (gamepad2.aWasPressed()) {
//                Drivetrain.localize(new Pose(7.28, 6.15, 0));
//                Turret.localize(0);
//            }

            if (gamepad2.right_stick_button) {
                ShootingController.hivePosition = Vector2D.cartesian(57.7, 54.9);
            }
            if (gamepad2.yWasPressed()) {
                ShootingController.hivePosition = Vector2D.cartesian(ShootingController.hivePosition.x() + 0.5, ShootingController.hivePosition.y());
            }
            if (gamepad2.aWasPressed()) {
                ShootingController.hivePosition = Vector2D.cartesian(ShootingController.hivePosition.x() - 0.5, ShootingController.hivePosition.y());
            }
            if (gamepad2.xWasPressed()) {
                ShootingController.hivePosition = Vector2D.cartesian(ShootingController.hivePosition.x(), ShootingController.hivePosition.y() + 0.5);
            }
            if (gamepad2.bWasPressed()) {
                ShootingController.hivePosition = Vector2D.cartesian(ShootingController.hivePosition.x(), ShootingController.hivePosition.y() - 0.5);
            }

            Telemetry.log("hivePosition", ShootingController.hivePosition);

            if (gamepad1.dpadRightWasPressed()) {
                Drivetrain.localize(new Pose(83.755, 5.6, Math.toDegrees(180))); // or 5.6
                gamepad1.rumble(500);
                //Turret.localize(0);
            }

            // TODO localize at flowers with dpads, and another button to prevent misclick
        });
    }
}
