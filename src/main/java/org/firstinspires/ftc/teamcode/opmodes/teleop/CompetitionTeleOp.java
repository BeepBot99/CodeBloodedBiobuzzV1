package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.ftc.FtcTelemetry;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;

@TeleOp(name = "TeleOp", group = "Competition")
public class CompetitionTeleOp extends RobotOpMode {
    @Override
    public void start() {
        turret.usePreviousStartingAngle();
        flywheel.on();
        intake.lower.on();
        turret.on();
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

            if (gamepad2.circleWasPressed())
                relocalize(new Pose(83.755, 5.6, Math.toRadians(180))); // jacob used degrees like a chud

            if (gamepad2.crossWasPressed())
                relocalize(new Pose(6.15, 7.28, Math.PI / 2)); // davis used radians like a real one

            if (gamepad2.triangleWasPressed()) {
                relocalize(new Pose(39.25, 79.2, 0));
            }
        });
    }

    private void relocalize(Pose pose) {
        drivetrain.follower.setPose(pose);
        gamepad1.rumble(500);
        gamepad2.rumble(500);
    }
}
