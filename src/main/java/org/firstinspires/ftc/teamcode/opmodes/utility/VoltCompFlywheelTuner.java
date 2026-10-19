package org.firstinspires.ftc.teamcode.opmodes.utility;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Utility(name = "Voltage Compensated Flywheel Tuner")
@Config("Voltage Compensated Flywheel Tuner")
public class VoltCompFlywheelTuner extends OpMode {
    private DcMotorEx motor;
    private VoltageSensor voltageSensor;

    public static double kP = 0;
    public static double kV = 0;
    public static double kS = 0;
    public static double targetVelocity = 0;
    public static double backEMFCompensation = 0; // likely unnecessary
    public static double velocityTolerance = 10;

    public static double shotDropThreshold = 150;
    public static double shotCooldownMs = 100;

    private double lastVelocity = 0;
    private double lastShotTime = 0;

    private final Telemetry telemetry = new MultipleTelemetry(super.telemetry, FtcDashboard.getInstance().getTelemetry());

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, "flywheel");
        voltageSensor = hardwareMap.get(VoltageSensor.class, "voltageSensor");
    }

    @Override
    public void loop() {
        double voltage = voltageSensor.getVoltage();
        double currentVelocity = motor.getVelocity();
        double error = (targetVelocity - currentVelocity);
        double feedforwardVoltage = kV * targetVelocity + kS * Math.signum(targetVelocity);
        double feedforwardPower = feedforwardVoltage / (voltage - currentVelocity * backEMFCompensation);

        // voltage compensation on kP is not ideal cause it reacts to voltage drops as balls pass through the shooter,
        // but it might not matter depending on the physical flywheel design
        double power = kP * error + feedforwardPower;
        motor.setPower(power);

        boolean isAtTarget = Math.abs(error) <= velocityTolerance;

        boolean wasBallShot = ballWasShot(currentVelocity);

        telemetry.addData("Battery/Voltage", voltage);
        telemetry.addData("Flywheel/Target Velocity", targetVelocity);
        telemetry.addData("Flywheel/Current Velocity", currentVelocity);
        telemetry.addData("Flywheel/Current", motor.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("Flywheel/Power", power);
        telemetry.addData("Flywheel/Applied Voltage", power * voltage);
        telemetry.addData("Flywheel/isAtTarget", isAtTarget);
        telemetry.addData("Flywheel/wasBallShot", wasBallShot);

        telemetry.update();
    }

    public boolean ballWasShot(double currentVelocity) {
        double rpmDrop = lastVelocity - currentVelocity;
        lastVelocity = currentVelocity;
        if (rpmDrop > shotDropThreshold) {
            double now = System.currentTimeMillis();
            if (lastShotTime == 0 || now - lastShotTime > shotCooldownMs) {
                lastShotTime = now;
                return true;
            }
        }
        return false;
    }
}
