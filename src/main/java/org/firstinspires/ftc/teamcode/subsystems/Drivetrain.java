package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.TelemetryTable;
import org.firstinspires.ftc.teamcode.control.LockController;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;

@Config
public class Drivetrain {
    public static double snapAngle = 15;
    private static Pose transferPose = null;
    private final Follower follower;
    private final TelemetryTable table = Telemetry.table("Drivetrain");
    public LockController headingLock = new LockController(0.05, 0.05, 0.05, 0.0644, 0.0021, true);
    public LockController xLock = new LockController(0.05, 0.05, 0.05, 0.0644, 0.0021, false);
    public LockController yLock = new LockController(0.05, 0.05, 0.05, 0.0644, 0.0021, false);

    public Drivetrain(Context context) {
        follower = Constants.create(context.hardwareMap);
        if (transferPose != null) follower.setPose(transferPose);
    }

    public static void localize(Pose pose) {
        transferPose = pose;
    }

    public void drive(double forward, double strafe, double turn) {
        turn = turn * turn * turn;

        forward = xLock.getOutput(forward, follower.pose().x(), follower.velocity().vx);
        strafe = yLock.getOutput(strafe, follower.pose().y(), follower.velocity().vy);
        turn = headingLock.getOutput(turn, follower.pose().heading(), follower.velocity().omega);

        Vector2D snappedDrive = Vector2D.cartesian(strafe, forward).rotate(-follower.pose().heading() - Math.PI);

        double angle = Math.atan2(snappedDrive.y(), snappedDrive.x());
        double nearestAxis = Math.rint(angle / (Math.PI / 2)) * (Math.PI / 2);
        double radiansToNearestAxis = Math.abs(Math.atan2(
                Math.sin(angle - nearestAxis),
                Math.cos(angle - nearestAxis)
        ));

        if (radiansToNearestAxis < Math.toRadians(snapAngle)) {
            snappedDrive = Vector2D.polar(
                    snappedDrive.magnitude(),
                    nearestAxis
            );
        }

        follower.manual(
                -snappedDrive.y(),
                snappedDrive.x(),
                turn
        );
        follower.update();
    }

    public Pose pose() {
        return follower.pose();
    }

    public Command periodic() {
        return infinite(() -> {
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
