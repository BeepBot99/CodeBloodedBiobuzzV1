package org.firstinspires.ftc.teamcode.subsystems;

import static com.pedropathing.ivy.commands.Commands.instant;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.utils.Context;

import static com.pedropathing.ivy.commands.Commands.*;

@Config
public final class Intake {
    public static boolean logCurrent = false;
    public static double onPower = -1;
    public static double offPower = 0;
    public static double reversePower = 1;
    public static double shortReverseTimeMs = 150;
    private final DcMotorEx lowerIntakeMotor;
    private final DcMotorEx upperIntakeMotor;
    private final Context context;
    private Mode mode = Mode.OFF;

    public Intake(Context context) {
        this.context = context;
        lowerIntakeMotor = context.motor("lowerIntake");
        upperIntakeMotor = context.motor("upperIntake");
    }

    public Command on() {
        return instant(() -> mode = Mode.FULL_ON).requiring(lowerIntakeMotor);
    }

    // not sure how if/how we would separately use intake motors
    public Command upperOn() {
        return instant(() -> mode = Mode.UPPER_ON).requiring(upperIntakeMotor);
    }

    public Command lowerOn() {
        return instant(() -> mode = Mode.LOWER_ON).requiring(lowerIntakeMotor);
    }

    public Command off() {
        return instant(() -> mode = Mode.OFF).requiring(lowerIntakeMotor);
    }

    public Command reverse() {
        return instant(() -> mode = Mode.REVERSE).requiring(lowerIntakeMotor);
    }

    public Command shortReverse() {
        return reverse().then(waitMs(shortReverseTimeMs)).then(on()).setConflictBehavior(ConflictBehavior.OVERRIDE);
    }

    public Command toggle() {
        return conditional(() -> mode == Mode.OFF, on(), off());
    }

    public Command periodic() {
        return infinite(() -> {
            switch (mode) {
                case FULL_ON:
                    lowerIntakeMotor.setPower(onPower);
                    upperIntakeMotor.setPower(onPower);
                    break;
                case UPPER_ON:
                    upperIntakeMotor.setPower(onPower);
                    break;
                case LOWER_ON:
                    lowerIntakeMotor.setPower(onPower);
                    break;
                case OFF:
                    lowerIntakeMotor.setPower(offPower);
                    upperIntakeMotor.setPower(offPower);
                    break;
                case REVERSE:
                    lowerIntakeMotor.setPower(reversePower);
                    upperIntakeMotor.setPower(reversePower);
                    break;
            }

            if (logCurrent) {
                context.telemetry.addData("Intake/lower current", getCurrentLower());
                context.telemetry.addData("Intake/upper current", getCurrentUpper());
            }
            context.telemetry.addData("Intake/velocity", lowerIntakeMotor.getVelocity());
            context.telemetry.addData("Intake/power", lowerIntakeMotor.getPower());
        });
    }

    public double getCurrentLower() {
        return lowerIntakeMotor.getCurrent(CurrentUnit.AMPS);
    }

    public double getCurrentUpper() {
        return upperIntakeMotor.getCurrent(CurrentUnit.AMPS);
    }

    enum Mode {
        FULL_ON,
        UPPER_ON,
        LOWER_ON,
        OFF,
        REVERSE
    }
}