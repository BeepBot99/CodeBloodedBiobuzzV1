package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

import static com.pedropathing.ivy.commands.Commands.waitMs;


@TeleOp(name = "Tuning TeleOp", group = "Tuning")
@Config("Tuning")
public class TuningTeleOp extends RobotOpMode {
    public static double flywheelTarget = 0;
    public static double upperIntakePower = 1;
    public static double lowerIntakePower = 1;

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
            drivetrain.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, -gamepad1.right_stick_x);

            if (gamepad1.right_bumper) {
                intake.upper.setPower(upperIntakePower).schedule();
                intake.lower.setPower(lowerIntakePower).schedule();
            }
            else intake.off().schedule();

            if (gamepad1.leftBumperWasPressed()) flywheel.toggle();
            flywheel.setTarget(flywheelTarget);

            if (gamepad1.right_trigger_pressed) blocker.unblock();
            else blocker.block();

            if (gamepad1.left_trigger_pressed) corbicula.down();
            else corbicula.up();
        });
    }
}
