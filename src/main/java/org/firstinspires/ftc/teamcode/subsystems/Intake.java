package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.TelemetryTable;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.*;

@Config
public final class Intake {
    public static boolean logCurrent = false;
    public final IntakeShaft lower;
    public final IntakeShaft upper;
    private final TelemetryTable table = Telemetry.table("Intake");

    public Intake(Context context) {
        lower = new IntakeShaft(context.motor("lowerIntake"), 1, table.table("Lower"));
        upper = new IntakeShaft(context.motor("upperIntake"), 0.5, table.table("Upper"));
    }

    public Command shoot() {
        return instant(() -> {
            upper.on();
            lower.on();
        }).then(waitMs(2000)).then(instant(() -> {
            upper.off();
            lower.off();
        }));
    }

    public Command periodic() {
        return infinite(() -> {
            upper.periodic(logCurrent);
            lower.periodic(logCurrent);
        });
    }
}