package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.base.TeleOpMode;
@TeleOp(group = "Demos")
@Config("Demo Robot-centric Teleop")
public class DemoRobotCentricTeleop extends TeleOpMode {
    public static double maxPower = 0.25;
    public static long debounceTime = 150;
    public static double powerStep = 0.05;

    private long lastPowerChangeTime = 0;

    @Override
    public void init_loop() {
        super.init_loop();

        telemetry.addData("Max Power", maxPower);
        telemetry.addLine("D-pad up/down to adjust power");
        telemetry.update();

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPowerChangeTime >= debounceTime) {
            if (gamepad1.dpad_up) {
                maxPower += powerStep;
                lastPowerChangeTime = currentTime;
            } else if (gamepad1.dpad_down) {
                maxPower -= powerStep;
                lastPowerChangeTime = currentTime;
            }
        }

        maxPower = Math.max(0, Math.min(1, maxPower));
    }

    @Override
    public void loop() {
        super.loop();

        follower.manual(
                -gamepad1.left_stick_y * maxPower,
                -gamepad1.left_stick_x * maxPower,
                -gamepad1.right_stick_x * maxPower
        );

        follower.update();
    }
}