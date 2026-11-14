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
    public static double upPositionLeft = 0.33;
    public static double downPositionLeft = 0.73;
    public static double upPositionRight = 0.81;
    public static double downPositionRight = 0.05;

    private final Servo rightServo;
    private final Servo leftServo;
    private final TelemetryTable table = Telemetry.table("Corbicula");
    private Mode mode = Mode.UP; // may move between transition if gravity moves it

    public Corbicula(Context context) {
        this.rightServo = context.servo("corbiculaRight");
        this.leftServo = context.servo("corbiculaLeft");
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
                    rightServo.setPosition(upPositionRight);
                    leftServo.setPosition(upPositionLeft);
                    break;
                case DOWN:
                    rightServo.setPosition(downPositionRight);
                    leftServo.setPosition(downPositionLeft);
                    break;
            }

            table.log("Position", rightServo.getPosition());
            table.log("Mode", mode);
        });
    }

    enum Mode {
        UP,
        DOWN
    }
}
