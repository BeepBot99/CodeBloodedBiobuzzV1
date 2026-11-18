package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.Servo;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.TelemetryTable;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;

@Config
public class Corbicula {
    public static double upPosition = 0.22;
    public static double downPosition = 0.635;

    private final Servo servo;
    private final TelemetryTable table = Telemetry.table("Corbicula");
    private Mode mode = Mode.UP;

    public Corbicula(Context context) {
        this.servo = context.servo("corbicula");
    }

    public void down() {
        mode = Mode.DOWN;
    }

    public void up() {
        mode = Mode.UP;
    }

    public Command periodic() {
        return infinite(() -> {
            switch (mode) {
                case UP:
                    servo.setPosition(upPosition);
                    break;
                case DOWN:
                    servo.setPosition(downPosition);
                    break;
            }

            table.log("Position", servo.getPosition());
            table.log("Mode", mode);
        });
    }

    enum Mode {
        UP,
        DOWN
    }
}
