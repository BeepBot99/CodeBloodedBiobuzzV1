package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.TelemetryTable;
import org.firstinspires.ftc.teamcode.control.PredictiveController;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.*;

@Config
public final class Turret {
    private static final double TICKS_PER_REVOLUTION = 28/((9.0/80.0)*(38.0/110.0));
    public static double minimumAngleDegrees = -90;
    public static double incrementDegrees = 2.5;
    public static PredictiveController controller = new PredictiveController(0,0,0);
    private static double angleTransfer = 0;
    private final DcMotorEx motor;
    private final Context context;
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
        angleTransfer = angle;
    }

    private double legalizeAngle(double angle) {
        return ((angle - minimumAngleDegrees) % 360 + 360) % 360 + minimumAngleDegrees;
    }

    private double getRawAngleDegrees() {
        return motor.getCurrentPosition() / TICKS_PER_REVOLUTION * 360;
    }

    public double getAngleDegrees() {
        return getRawAngleDegrees() + angleOffsetDegrees;
    }

    private void setAngleDegrees(double angle) {
        angleOffsetDegrees = angle - getRawAngleDegrees();
    }

    public Command off() {
        return instant(() -> mode = Mode.OFF);
    }

    public Command on() {
        return instant(() -> mode = Mode.POSITION);
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

    public void usePreviousStartingAngle() {
        setAngleDegrees(angleTransfer);
    }

    public Command toggle() {
        return conditional(() -> mode == Mode.OFF, on(), off());
    }
    
    public Command periodic() {
        return infinite(() -> {
            double error = targetDegrees - getAngleDegrees();

            switch (mode) {
                case POSITION:
                    motor.setPower(controller.getOutput(error, motor.getVelocity()));
                    break;
                case OFF:
                    motor.setPower(0);
                    break;
            }

            angleTransfer = getAngleDegrees();

            Telemetry.log("Turret/angle", getAngleDegrees());
            Telemetry.log("Turret/target", targetDegrees);
            Telemetry.log("Turret/error", error);
            Telemetry.log("Turret/power", motor.getPower());
            Telemetry.log("Turret/motor", motor.getVelocity());
            Telemetry.log("Turret/mode", mode);
        });
    }

    enum Mode {
        POSITION,
        OFF
    }
}