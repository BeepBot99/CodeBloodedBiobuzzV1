package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import dev.nextftc.telemetry.TelemetryTable;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class IntakeShaft {
    private final DcMotorEx motor;
    private final double onPower;
    private final TelemetryTable table;

    public IntakeShaft(DcMotorEx motor, double onPower, TelemetryTable table) {
        this.motor = motor;
        this.onPower = onPower;
        this.table = table;

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setPower(double power) {
        motor.setPower(power);
    }

    public void on() {
        setPower(onPower);
    }

    public void off() {
        setPower(0);
    }

    public void reverse() {
        setPower(-1);
    }

    public double getCurrent() {
        return motor.getCurrent(CurrentUnit.AMPS);
    }

    public void periodic(boolean logCurrent) {
        table.log("Velocity", motor.getVelocity());
        table.log("Power", motor.getPower());

        if (logCurrent) {
            table.log("Current", getCurrent());
        }
    }
}