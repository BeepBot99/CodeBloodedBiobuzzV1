package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.ftc.FtcTelemetry;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;


@TeleOp(name = "Tuning TeleOp", group = "Tuning")
@Config("Tuning")
public class TuningTeleOp extends RobotOpMode {
    public static double flywheelTarget = 0;
    public static double upperIntakePower = 0.5;
    public static double lowerIntakePower = 1;
    public static double turretDegrees = 0;
    public static boolean autoAim = true;

    @Override
    public void init() {
        super.init();
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

            if (gamepad1.right_bumper) {
                intake.lower.setPower(lowerIntakePower);
            }
            else intake.lower.off();

            if (gamepad1.leftBumperWasPressed()) flywheel.toggle();
            flywheel.setTarget(flywheelTarget);

            if (gamepad1.right_trigger_pressed) intake.upper.setPower(upperIntakePower);
            else intake.upper.off();

            if (gamepad1.left_trigger_pressed) corbicula.down();
            else corbicula.up();

            if (gamepad1.triangleWasPressed()) turret.toggle();

            if (autoAim) shootingController.aim(drivetrain.pose(), drivetrain.velocity());
            else turret.setTargetDegrees(turretDegrees - Math.toDegrees(drivetrain.pose().heading()));
        });
    }
}
