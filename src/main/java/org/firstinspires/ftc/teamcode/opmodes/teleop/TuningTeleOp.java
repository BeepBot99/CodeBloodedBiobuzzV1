package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;


@TeleOp(name = "Tuning TeleOp", group = "Tuning")
@Config("Tuning")
public class TuningTeleOp extends RobotOpMode {
    public static double flywheelTarget = 0;

    @Override
    public void loop() {
        wrapLoop(() -> {
            drivetrain.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, -gamepad1.right_stick_x);

            if (gamepad1.right_bumper) intake.on().schedule();
            else intake.off().schedule();
            if (gamepad1.left_bumper) flywheel.toggle();
            flywheel.setTarget(flywheelTarget);

            if (gamepad1.right_trigger_pressed) blocker.unblock();
            else blocker.block();

            if (gamepad1.left_trigger_pressed) corbicula.down();
            else corbicula.up();
        });
    }
}
