package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.pedro.PedroCommands;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.math.Velocity;
import com.pedropathing.paths.Path;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.TelemetryTable;
import org.firstinspires.ftc.teamcode.control.LockController;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.util.Context;
import org.psilynx.psikit.core.Logger;

import static com.pedropathing.ivy.commands.Commands.infinite;

@Config
public class Drivetrain {
    public static double snapAngle = 15;
    public static double minTranslationalPower = 0.1;
    public static double joystickDeadZone = 0.05;
    public static double translationExponent = 1.5;
    public static double minTurnPower = 0.1;
    public static double turnExponent = 3;
    public static double relativeDriveOffsetRadians = Math.PI / 2;

    private static Pose transferPose = null;
    public final Follower follower;
    private final TelemetryTable table = Telemetry.table("Drivetrain");
    private final TelemetryTable manualDriveTable = table.table("ManualDrive");

    public final LockController headingLock = new LockController(0.5, Constants.foresightConfig.headingBrakeCoefficients.get().x(), Constants.foresightConfig.headingBrakeCoefficients.get().y(), 0.05, 0.05, true);
    public final LockController xLock = new LockController(0.2, Constants.foresightConfig.linearBrakeCoefficients.get().get(0, 0), Constants.foresightConfig.quadraticBrakeCoefficients.get().get(0, 0), 0.05, 0.05, false);
    public final LockController yLock = new LockController(0.2, Constants.foresightConfig.linearBrakeCoefficients.get().get(1, 1), Constants.foresightConfig.quadraticBrakeCoefficients.get().get(1, 1), 0.05, 0.05, false);

    public Drivetrain(Context context) {
        follower = Constants.create(context.hardwareMap);
        if (transferPose != null) follower.setPose(transferPose);
    }

    public static void setTransferPose(Pose pose) {
        transferPose = pose;
    }

    public void drive(double y, double x, double turn) {
        turn = scaleInput(turn, minTurnPower, joystickDeadZone, turnExponent);

        double relativeHeading = follower.pose().heading() + relativeDriveOffsetRadians;

        Vector2D fieldInput = scaleTranslationInput(Vector2D.cartesian(x, y));
        Vector2D unsnappedInput = fieldInput.rotate(-relativeHeading);
        Vector2D snappedInput = snapToAxis(unsnappedInput);
        Vector2D fieldTranslation = snappedInput.rotate(relativeHeading);

        x = xLock.getOutput(fieldTranslation.x(), follower.pose().x(), follower.velocity().vx);
        y = yLock.getOutput(fieldTranslation.y(), follower.pose().y(), follower.velocity().vy);
        turn = headingLock.getOutput(turn, follower.pose().heading(), follower.velocity().omega);

        Vector2D robotTranslational = Vector2D.cartesian(x, y).rotate(-relativeHeading);

        follower.manual(
                robotTranslational.x(),
                robotTranslational.y(),
                turn
        );

        manualDriveTable.log("Input/X", x);
        manualDriveTable.log("Input/Y", y);
        manualDriveTable.log("Input/Turn", turn);
        manualDriveTable.log("Relative Heading (deg)", Math.toDegrees(relativeHeading));
        manualDriveTable.log("Scaled/X", fieldInput.x());
        manualDriveTable.log("Scaled/Y", fieldInput.y());
        manualDriveTable.log("Scaled/Turn", turn);
        manualDriveTable.log("Snap/Original Angle (deg)", Math.toDegrees(unsnappedInput.theta()));
        manualDriveTable.log("Snap/Snapped Angle (deg)", Math.toDegrees(snappedInput.theta()));
        manualDriveTable.log("Snap/X", fieldTranslation.x());
        manualDriveTable.log("Snap/Y", fieldTranslation.y());
        manualDriveTable.log("Output/Field/X", x);
        manualDriveTable.log("Output/Field/Y", y);
        manualDriveTable.log("Output/Robot/X", robotTranslational.x());
        manualDriveTable.log("Output/Robot/Y", robotTranslational.y());
        manualDriveTable.log("Output/Turn", turn);
        manualDriveTable.log("Output/Magnitude", robotTranslational.magnitude());
    }

    private static double scaleInput(double input, double minPower, double deadZone, double exponent) {
        if (Math.abs(input) <= deadZone) {
            return 0;
        }

        double normalizedInput = (Math.abs(input) - deadZone) / (1 - deadZone);

        return Math.signum(input) * (
                minPower + (1 - minPower) * Math.pow(normalizedInput, exponent)
        );
    }

    private Vector2D scaleTranslationInput(Vector2D input) {
        double magnitude = input.magnitude();

        if (magnitude <= joystickDeadZone) {
            return Vector2D.cartesian(0, 0);
        }

        double scaledMagnitude = scaleInput(
                Math.min(magnitude, 1),
                minTranslationalPower,
                joystickDeadZone,
                translationExponent
        );

        return Vector2D.polar(
                scaledMagnitude,
                input.theta()
        );
    }

    private Vector2D snapToAxis(Vector2D vector) {
        double angle = vector.theta();
        double nearestAxis = Math.rint(angle / (Math.PI / 2)) * (Math.PI / 2);

        if (Math.abs(angle - nearestAxis) < Math.toRadians(snapAngle)) {
            return Vector2D.polar(vector.magnitude(), nearestAxis);
        }

        return vector;
    }

    public Pose pose() {
        return follower.pose();
    }

    public Velocity velocity() {
        return follower.velocity();
    }

    public Command follow(Path path) {
        return PedroCommands.follow(follower, path);
    }

    public Command periodic() {
        return infinite(() -> {
            follower.update();
            transferPose = pose();

            table.log("X Lock", xLock.getLockedTarget());
            table.log("Y Lock", yLock.getLockedTarget());
            table.log("Heading Lock", headingLock.getLockedTarget());
            table.log("Pose", follower.pose());
            table.log("Pose x", follower.pose().x());
            table.log("Pose y", follower.pose().y());
            table.log("Pose heading (deg)", Math.toDegrees(follower.pose().heading()));
            table.log("Velocity", follower.velocity());
            table.log("Speed", follower.velocity().linSpeed());
        });
    }
}
