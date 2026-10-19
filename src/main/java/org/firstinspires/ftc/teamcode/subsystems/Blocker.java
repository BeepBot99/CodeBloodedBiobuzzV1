package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;
import static com.pedropathing.ivy.commands.Commands.instant;

@Config
public class Blocker {
    public static double blockPosition = 0.415;
    public static double unblockPosition = 0.217;

    private final ServoImplEx blockerServo;
    private final Context context;

    private Mode mode;

    public Blocker(Context context) {
        this.context = context;
        blockerServo = (ServoImplEx) context.servo("blocker");
    }

    enum Mode {
        BLOCKED,
        UNBLOCKED,
        REST
    }

    public void setPosition(double position) {
        blockerServo.setPosition(position);
    }

    public Command block() {
        return instant(() -> mode = Mode.BLOCKED);
    }

    public Command unblock() {
        return instant(() -> mode = Mode.UNBLOCKED);
    }

    public Command rest() {
        return instant(() -> mode = Mode.REST);
    }

    public Command periodic() {
        return infinite(() -> {
            switch (mode) {
                case BLOCKED:
                    setPosition(blockPosition);
                    break;
                case UNBLOCKED:
                    setPosition(unblockPosition);
                    break;
                case REST:
                    blockerServo.setPwmDisable();
                    break;
            }

            context.telemetry.addData("Blocker/position", blockerServo.getPosition());
            context.telemetry.addData("Blocker/enabled", blockerServo.isPwmEnabled());
            context.telemetry.addData("Blocker/mode", mode);
        });
    }
}