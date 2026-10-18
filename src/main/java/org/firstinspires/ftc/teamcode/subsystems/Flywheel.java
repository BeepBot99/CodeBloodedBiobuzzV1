package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.utils.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;

public class Flywheel {
    private final DcMotorEx motor;
    private final VoltageSensor voltageSensor;
    private final Context context;

    public static double kP = 0;
    public static double kV = 0;
    public static double kS = 0;
    public static double backEMFCompensation = 0; // likely unnecessary
    public static double velocityTolerance = 25;

    public static double shotDropThreshold = 150;
    public static double shotCooldownMs = 100;

    private double targetVelocity = 0;
    private double currentVelocity = 0;
    private double lastVelocity = 0;
    private double lastShotTime = 0;
    private double power = 0;

    private Mode mode = Mode.OFF;

    private boolean atTarget = false;
    private boolean wasBallShot = false;

    public Flywheel(Context context) {
        this.context = context;
        motor = context.motor("flywheel");
        voltageSensor = context.voltageSensor();
    }

    public void setTarget(double target) {
        targetVelocity = target;
    }

    public void off() {
        mode = Mode.OFF;
    }

    public void on() {
        mode = Mode.VELOCITY;
    }

    public void toggle() { // why are these not instants but intake are?
        mode = mode == Mode.OFF ? Mode.VELOCITY : Mode.OFF;
    }

    private void setPower(double power) {
        this.power = power;
    }

    public double getVelocity() {
        return currentVelocity;
    }

    public double getTarget() {
        return targetVelocity;
    }

    public boolean atTarget() {
        return atTarget;
    }

    public Command periodic() {
        return infinite(() -> {
            currentVelocity = motor.getVelocity();

            switch (mode) {
                case OFF:
                    setPower(0);
                    return;
                case VELOCITY:
                    double voltage = voltageSensor.getVoltage();
                    double error = (targetVelocity - currentVelocity);
                    double feedforwardVoltage = kV * targetVelocity + kS * Math.signum(targetVelocity);
                    double feedforwardPower = feedforwardVoltage / (voltage - currentVelocity * backEMFCompensation);

                    // voltage compensation on kP is not ideal cause it reacts to voltage drops as balls pass through the shooter,
                    // but it might not matter depending on the physical flywheel design
                    double power = kP * error + feedforwardPower;
                    setPower(power);

                    atTarget = Math.abs(error) <= velocityTolerance;
                    wasBallShot = ballWasShot(currentVelocity);

                    context.telemetry.addData("Battery/Voltage", voltage);
                    context.telemetry.addData("Flywheel/Applied Voltage", power * voltage);
                    break;
            }

            motor.setPower(power);

            context.telemetry.addData("Flywheel/velocity", getVelocity());
            context.telemetry.addData("Flywheel/Target Velocity", targetVelocity);
            context.telemetry.addData("Flywheel/Current Velocity", currentVelocity);
            context.telemetry.addData("Flywheel/Current", motor.getCurrent(CurrentUnit.AMPS));
            context.telemetry.addData("Flywheel/Power", power);
            context.telemetry.addData("Flywheel/atTarget", atTarget);
            context.telemetry.addData("Flywheel/wasBallShot", wasBallShot);
        });
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

    enum Mode {
        OFF,
        VELOCITY
    }
}

