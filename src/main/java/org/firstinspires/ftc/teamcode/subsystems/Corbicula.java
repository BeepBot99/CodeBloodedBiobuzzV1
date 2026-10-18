package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;
import static com.pedropathing.ivy.commands.Commands.instant;

@Config
public class Corbicula {
    public static final double UP_POSITION = 1.0;
    public static final double DOWN_POSITION = 0.0;

    private final ServoImplEx servo1;
    private final ServoImplEx servo2;

    private final Context context;

    private Mode mode;

    public Corbicula(Context context) {
        this.context = context;
        this.servo1 = (ServoImplEx) context.servo("corbicula1");
        this.servo2 = (ServoImplEx) context.servo("corbicula2");
    }

    enum Mode {
        UP,
        DOWN,
        REST
    }

    public void setPosition(double position) {
        servo1.setPosition(position);
        servo2.setPosition(position);
    }

    public Command down() {
        return instant(() -> mode = Mode.DOWN);
    }

    public Command up() {
        return instant(() -> mode = Mode.UP);
    }

    public Command rest() {
        return instant(() -> mode = Mode.REST);
    }

    public Command periodic() {
        return infinite(() -> {
            switch (mode) {
                case UP:
                    setPosition(UP_POSITION);
                    break;
                case DOWN:
                    setPosition(DOWN_POSITION);
                    break;
                case REST:
                    servo1.setPwmDisable();
                    servo2.setPwmDisable();
                    break;
            }

            context.telemetry.addData("Corbicula/position", servo1.getPosition());
            context.telemetry.addData("Corbicula/enabled", servo1.isPwmEnabled());
            context.telemetry.addData("Corbicula/mode", mode);
        });
    }
}
