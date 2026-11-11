package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.TelemetryTable;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;

@Config
public final class Intake {
    public static boolean logCurrent = false;

    public final IntakeShaft lower;
    public final IntakeShaft upper;
    private final TelemetryTable table = Telemetry.table("Intake");

    public Intake(Context context) {
        lower = new IntakeShaft(context.motor("lowerIntake"));
        upper = new IntakeShaft(context.motor("upperIntake"));
    }

    public Command on() {
        return sequential(upper.on(), lower.on());
    }

    public Command off() {
        return sequential(upper.off(), lower.off());
    }

    public Command reverse() {
        return sequential(upper.reverse(), lower.reverse());
    }

    public Command toggle() {
        return conditional(() -> upper.isOff() && lower.isOff(), on(), off());
    }

    public Command periodic() {
        return infinite(() -> {
            table.log("Lower/Velocity", lower.motor.getVelocity());
            table.log("Upper/Velocity", upper.motor.getVelocity());
            table.log("Lower/Power", lower.motor.getPower());
            table.log("Upper/Power", upper.motor.getPower());

            if (logCurrent) {
                table.log("Lower/Current", lower.getCurrent());
                table.log("Upper/Current", upper.getCurrent());
            }
        });
    }
}