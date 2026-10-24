package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Vector2D;
import gay.zharel.fateweaver.telemetry.Fate;
import gay.zharel.fateweaver.telemetry.FateTable;
import org.firstinspires.ftc.teamcode.control.LockController;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;

@Config
public class Drivetrain {
    public static double snapAngle = 15;
    private final LockController headingLock = new LockController(0.05, 0.05, 2, 0.1876, 0.0, true);
    private final LockController xLock = new LockController(0.05, 0.05, 0.05, 0.0644, 0.0021, false);
    private final LockController yLock = new LockController(0.05, 0.05, 0.05, 0.0644, 0.0021, false);
    private final Follower follower;
    private final FateTable table = Fate.table("Drivetrain");

    public Drivetrain(Context context) {
        follower = Constants.create(context.hardwareMap);
    }

    public void drive(double forward, double strafe, double turn) {
        Vector2D snappedDrive = Vector2D.cartesian(strafe, forward).rotate(-follower.pose().heading());

        double angle = Math.atan2(snappedDrive.y(), snappedDrive.x());
        double nearestAxis = Math.rint(angle / (Math.PI / 2)) * (Math.PI / 2);

        if (Math.abs(Math.atan2(
                Math.sin(angle - nearestAxis),
                Math.cos(angle - nearestAxis)
        )) < Math.toRadians(snapAngle)) {
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
    }

    public Command periodic() {
        return infinite(() -> {
            table.log("X Lock", xLock.getLockedTarget());
            table.log("Y Lock", yLock.getLockedTarget());
            table.log("Heading Lock", headingLock.getLockedTarget());
            table.log("Pose", follower.pose());
            table.log("Velocity", follower.velocity());
            table.log("Speed", follower.velocity().linSpeed());
        });
    }
}
