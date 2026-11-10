package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.controllers.Controller;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.TelemetryTable;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.*;

@Config
public final class Turret {
    private static final double TICKS_PER_REVOLUTION = 8192 / (38.0 / 120.0);
    public static double minimumAngleDegrees = 0;
    public static double incrementDegrees = 2.5;
    public static double kP = 0.025;
    public static double kD = 0.0009;
    public static boolean logCurrent = false;

    private static double lastAngle = 0;

    private final Controller controller = Controller.proportional(() -> kP).plus(Controller.derivative(() -> kD));
    private final DcMotorEx motor;
    private final Context context;
    private final TelemetryTable table = Telemetry.table("Turret");

    private double targetDegrees = 0;
    private Mode mode = Mode.OFF;
    private double angleOffsetDegrees = 0;

    public Turret(Context context) {
        this.context = context;
        motor = context.motor("turret");

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public static void localize(double angle) {
        lastAngle = angle;
    }

    private double legalizeAngle(double angle) {
        return ((angle - minimumAngleDegrees) % 360 + 360) % 360 + minimumAngleDegrees;
    }

    private double getRawAngleDegrees() {
        return motor.getCurrentPosition() / TICKS_PER_REVOLUTION * 360;
    }

    private double getRawDegreesPerSecond() {
        return motor.getVelocity() / TICKS_PER_REVOLUTION * 360;
    }

    public double getAngleDegrees() {
        return getRawAngleDegrees() + angleOffsetDegrees;
    }

    private void setAngleDegrees(double angle) {
        angleOffsetDegrees = angle - getRawAngleDegrees();
    }

    public void off() {
       mode = Mode.OFF;
    }

    public void on() {
        mode = Mode.POSITION;
    }

    public void usePreviousStartingAngle() {
        setAngleDegrees(lastAngle);
    }

    public double getTargetDegrees() {
        return targetDegrees;
    }

    public void setTargetDegrees(double targetDegrees) {
        this.targetDegrees = legalizeAngle(targetDegrees);
    }

    public void moveLeft() {
        setAngleDegrees(getAngleDegrees() - incrementDegrees);
    }

    public void moveRight() {
        setAngleDegrees(getAngleDegrees() + incrementDegrees);
    }

    public void setStartingAngle(double angle) {
        setAngleDegrees(angle);
    }

    public void toggle() {
        mode = mode == Mode.OFF ? Mode.POSITION : Mode.OFF;
    }
    
    public Command periodic() {
        return infinite(() -> {
            double current = getAngleDegrees();
            double error = targetDegrees - current;

            switch (mode) {
                case POSITION:
                    motor.setPower(-controller.calculate(targetDegrees, error));
                    break;
                case OFF:
                    motor.setPower(0);
                    break;
            }

            lastAngle = current;

            table.log("Angle", current);
            table.log("Target", targetDegrees);
            table.log("Error", error);
            table.log("Power", motor.getPower());
            table.log("Velocity", getRawDegreesPerSecond());
            table.log("velocityFromPosition", (lastAngle - current) / context.loopTimer.seconds());
            table.log("Mode", mode);

            if (logCurrent) table.log("CurrentAMPS", motor.getCurrent(CurrentUnit.AMPS));
        });
    }

    enum Mode {
        POSITION,
        OFF
    }
}