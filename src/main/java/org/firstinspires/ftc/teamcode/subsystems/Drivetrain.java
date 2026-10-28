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

    public static class HeadingLock {
        public static double lockingInputThreshold = 0.05;
        public static double maximumLockingVelocity = 0.05;
        public static double kP = 2;
        public static double kL = 0.1876;
        public static double kQ = 0.0;
        public static boolean enabled = false;
    }

    public static class XLock {
        public static double lockingInputThreshold = 0.05;
        public static double maximumLockingVelocity = 0.05;

        public static double kP = 0.05;
        public static double kL = 0.0644;
        public static double kQ = 0.0021;
        public static boolean enabled = false;
    }

    public static class YLock {
        public static double lockingInputThreshold = 0.05;
        public static double maximumLockingVelocity = 0.05;

        public static double kP = 0.05;
        public static double kL = 0.0644;
        public static double kQ = 0.0021;
        public static boolean enabled = false;
    }

    private final LockController headingLock = new LockController(true);
    private final LockController xLock = new LockController(false);
    private final LockController yLock = new LockController(false);
    private final Follower follower;
    private final FateTable table = Fate.table("Drivetrain");

    public Drivetrain(Context context) {
        follower = Constants.create(context.hardwareMap);

        updateGains();
    }

    public void drive(double forward, double strafe, double turn) {
        if (XLock.enabled) {
            forward = xLock.getOutput(forward, follower.pose().x(), follower.velocity().vx);
        }
        if (YLock.enabled) {
            strafe = yLock.getOutput(strafe, follower.pose().y(), follower.velocity().vy);
        }
        if (HeadingLock.enabled) {
            turn = headingLock.getOutput(turn, follower.pose().heading(), follower.velocity().omega);
        }

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
        follower.update();
    }

    public Command periodic() {
        return infinite(() -> {
            updateGains();

            table.log("X Lock", xLock.getLockedTarget());
            table.log("Y Lock", yLock.getLockedTarget());
            table.log("Heading Lock", headingLock.getLockedTarget());
            table.log("Pose", follower.pose());
            table.log("Velocity", follower.velocity());
            table.log("Speed", follower.velocity().linSpeed());
        });
    }

    public void updateGains() {
        xLock.setGains(XLock.lockingInputThreshold, XLock.maximumLockingVelocity, XLock.kP, XLock.kL, XLock.kQ);
        yLock.setGains(YLock.lockingInputThreshold, YLock.maximumLockingVelocity, YLock.kP, YLock.kL, YLock.kQ);
        headingLock.setGains(HeadingLock.lockingInputThreshold, HeadingLock.maximumLockingVelocity, HeadingLock.kP, HeadingLock.kL, HeadingLock.kQ);
    }
}
