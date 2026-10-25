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

    public static double passiveUpperIntakePower = 0.25;

    private RevTouchSensor pollenSensor4;

    @Override
    public void init() {
        super.init();
        pollenSensor4 = hardwareMap.get(RevTouchSensor.class, "pollenSensor4");
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
           // drivetrain.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, -gamepad1.right_stick_x);

            boolean hasFirstPollen = !pollenSensor4.isPressed(); // sensor is reversed

            if (gamepad1.right_bumper && hasFirstPollen) {
                intake.upper.off().schedule();
                intake.lower.on().schedule();
            }
            else if (gamepad1.right_bumper && !hasFirstPollen) {
                intake.upper.setPower(passiveUpperIntakePower).schedule();
                intake.lower.on().schedule();
            }
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
