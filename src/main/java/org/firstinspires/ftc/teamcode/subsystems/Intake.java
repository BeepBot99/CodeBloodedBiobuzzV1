package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import gay.zharel.fateweaver.telemetry.Fate;
import gay.zharel.fateweaver.telemetry.FateTable;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.util.Context;

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
    private Mode mode = Mode.OFF;
    private final FateTable table = Fate.table("Intake");

    public Intake(Context context) {
        lowerIntakeMotor = context.motor("lowerIntake");
        upperIntakeMotor = context.motor("upperIntake");
    }

    public Command on() {
        return instant(() -> mode = Mode.ON).requiring(lowerIntakeMotor);
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
                case ON:
                    lowerIntakeMotor.setPower(onPower);
                    upperIntakeMotor.setPower(onPower);
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

            table.log("Lower/Velocity", lowerIntakeMotor.getVelocity());
            table.log("Upper/Velocity", upperIntakeMotor.getVelocity());
            table.log("Lower/Power", lowerIntakeMotor.getPower());
            table.log("Upper/Power", upperIntakeMotor.getPower());
            table.log("Mode", mode);

            if (logCurrent) {
                table.log("Lower/Current", getCurrentLower());
                table.log("Upper/Current", getCurrentUpper());
            }
        });
    }

    public double getCurrentLower() {
        return lowerIntakeMotor.getCurrent(CurrentUnit.AMPS);
    }

    public double getCurrentUpper() {
        return upperIntakeMotor.getCurrent(CurrentUnit.AMPS);
    }

    enum Mode {
        ON,
        OFF,
        REVERSE
    }
}