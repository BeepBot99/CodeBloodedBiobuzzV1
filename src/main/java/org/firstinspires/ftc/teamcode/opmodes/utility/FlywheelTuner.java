package org.firstinspires.ftc.teamcode.opmodes.utility;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Utility(name = "Flywheel Tuner")
@Config("Flywheel Tuner")
public class FlywheelTuner extends OpMode {
    private DcMotorEx motor;
    public static double kP = 0;
    public static double kV = 0;
    public static double kS = 0;
    public static double targetVelocity = 0;
    private final Telemetry telemetry = new MultipleTelemetry(super.telemetry, FtcDashboard.getInstance().getTelemetry());

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, "flywheel");
    }

    @Override
    public void loop() {
        double currentVelocity = motor.getVelocity();
        double power = kV * targetVelocity + kP * (targetVelocity - currentVelocity) + kS * Math.signum(targetVelocity);
        motor.setPower(power);

        telemetry.addData("Flywheel/Target Velocity", targetVelocity);
        telemetry.addData("Flywheel/Current Velocity", currentVelocity);
        telemetry.addData("Flywheel/Current", motor.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Flywheel/Power", power);

        telemetry.update();
    }
}
