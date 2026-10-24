package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import gay.zharel.fateweaver.telemetry.Fate;
import gay.zharel.fateweaver.telemetry.FateTable;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;

@Config
public class Flywheel {
    public static double kP = 0.015;
    public static double kV = 0.000364;
    public static double kS = 0.05;
    public static boolean logCurrent = false;
    private final DcMotorEx motor;
    private final FateTable table = Fate.table("Flywheel");
    private double target = 0;
    private Mode mode = Mode.OFF;

    public Flywheel(Context context) {
        motor = context.motor("flywheel");
    }

    public void off() {
        mode = Mode.OFF;
    }

    public void on() {
        mode = Mode.VELOCITY;
    }

    public void toggle() { // why are these not instants but intake are?
        mode = mode == Mode.OFF ? Mode.VELOCITY : Mode.OFF;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public Command periodic() {
        return infinite(() -> {

            switch (mode) {
                case OFF:
                    motor.setPower(0);
                    return;
                case VELOCITY:
                    double velocity = motor.getVelocity();
                    double power = kP * (target - velocity) + kV * target + kS * Math.signum(target);
                    motor.setPower(power);
                    break;
            }

            table.log("Velocity", motor.getVelocity());
            table.log("Target", target);
            table.log("Power", motor.getPower());
            table.log("Mode", mode);

            if (logCurrent) {
                table.log("Current", motor.getCurrent(CurrentUnit.AMPS));
            }
        });
    }

    enum Mode {
        OFF,
        VELOCITY
    }
}

