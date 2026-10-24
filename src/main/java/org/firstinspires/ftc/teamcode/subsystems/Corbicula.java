package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import gay.zharel.fateweaver.telemetry.Fate;
import gay.zharel.fateweaver.telemetry.FateTable;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;

@Config
public class Corbicula {
    public static double upPosition = 1.0;
    public static double downPosition = 0.0;

    private final Servo servo1;
    private final Servo servo2;

    private Mode mode;
    private final FateTable table = Fate.table("Corbicula");

    public Corbicula(Context context) {
        this.servo1 = context.servo("corbicula1");
        this.servo2 = context.servo("corbicula2");
    }

    private void setPosition(double position) {
        servo1.setPosition(position);
        servo2.setPosition(position);
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
                    setPosition(upPosition);
                    break;
                case DOWN:
                    setPosition(downPosition);
                    break;
            }

            table.log("Position", servo1.getPosition());
            table.log("Mode", mode);
        });
    }

    enum Mode {
        UP,
        DOWN
    }
}
