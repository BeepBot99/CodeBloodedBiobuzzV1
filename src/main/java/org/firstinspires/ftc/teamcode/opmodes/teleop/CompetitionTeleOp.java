package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;


@TeleOp(name = "TeleOp", group = "Competition")
public class CompetitionTeleOp extends RobotOpMode {
    @Override
    public void start() {
        flywheel.on();
        intake.lower.on().schedule();
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
            drivetrain.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, -gamepad1.right_stick_x);

            if (gamepad1.right_bumper || sensors.ballCount() == 4) intake.lower.off().schedule();
            else intake.lower.on().schedule();

            if (gamepad1.right_trigger_pressed) intake.upper.on().schedule();
            else intake.upper.off().schedule();

            if (gamepad1.left_trigger_pressed) corbicula.down();
            else corbicula.up();
        });
    }
}
