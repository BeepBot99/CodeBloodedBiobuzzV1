package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.Servo;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.TelemetryTable;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;

@Config
public class Blocker {
    public static double blockPosition = 0.415;
    public static double unblockPosition = 0.217;

    private final Servo servo;
    private final TelemetryTable table = Telemetry.table("Blocker");

    private Mode mode = Mode.BLOCKED; // may move between transition

    public Blocker(Context context) {
        servo = context.servo("blocker");
    }

    public void block() {
        mode = Mode.BLOCKED;
    }

    public void unblock() {
        mode = Mode.UNBLOCKED;
    }

    public Command periodic() {
        return infinite(() -> {
            switch (mode) {
                case BLOCKED:
                    servo.setPosition(blockPosition);
                    break;
                case UNBLOCKED:
                    servo.setPosition(unblockPosition);
                    break;
            }

            table.log("Position", servo.getPosition());
            table.log("Mode", mode);
        });
    }

    enum Mode {
        BLOCKED,
        UNBLOCKED,
    }
}