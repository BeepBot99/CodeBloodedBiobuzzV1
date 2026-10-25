package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import static com.pedropathing.ivy.commands.Commands.instant;

public class IntakeShaft {
    DcMotorEx motor;

    public IntakeShaft(DcMotorEx motor) {
        this.motor = motor;

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public Command setPower(double power) {
        return instant(() -> motor.setPower(power)).requiring(motor);
    }

    public Command on() {
        return setPower(1);
    }

    public Command off() {
        return setPower(0);
    }

    public Command reverse() {
        return setPower(-1);
    }

    public boolean isOn() {
        return motor.getPower() > 0;
    }

    public boolean isReversing() {
        return motor.getPower() > 0;
    }

    public boolean isOff() {
        return motor.getPower() == 0;
    }

    public double getCurrent() {
        return motor.getCurrent(CurrentUnit.AMPS);
    }
}