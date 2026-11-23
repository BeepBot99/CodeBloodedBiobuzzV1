package org.firstinspires.ftc.teamcode.control;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector2D;
import com.pedropathing.math.Velocity;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.util.Context;

@Config("Shooting Controller")
public final class ShootingController {
    private static final Vector2D hivePositionRight = Vector2D.cartesian(57.7, 54.9);
    private static final Vector2D hivePositionLeft = Vector2D.cartesian(57.7 + 1, 141.5 - 3 - 54.9);
    private static final int SOTM_ITERATIONS = 10;
    public static double turningGain = 2.4;
    private final Context context;
    private final Turret turret;
    private final Flywheel flywheel;

    public ShootingController(Context context, Turret turret, Flywheel flywheel) {
        this.context = context;
        this.turret = turret;
        this.flywheel = flywheel;
    }

    private static Vector2D calculateVirtualRobot(Vector2D robotPose, Vector2D robotVelocity) {
        if (robotVelocity.magnitude() < 1) return robotPose;
        Vector2D virtualPose = robotPose;
        for (int i = 0; i < SOTM_ITERATIONS; i++) {
            double airTime = Interpolation.getAirTime(virtualPose.x(), virtualPose.y());
            virtualPose = robotPose.plus(robotVelocity.times(airTime));
        }
        return virtualPose;
    }

    public void prepareForLocation(Pose pose, Velocity velocity) {
        spinUp(pose);
        aim(pose, velocity);
    }

    public void prepareForLocationSlow(Pose pose) {
        flywheel.setTarget(Interpolation.getFlywheelVelocity(pose.x(), pose.y()) - 20);
        aim(pose, Velocity.zero());
    }

    public void prepareForLocationSotm(Pose pose, Velocity velocity) {
        Pose virtualPose = calculateVirtualRobot(pose.toVector2D(), velocity.toVector2D()).toPose(pose.heading());
        prepareForLocation(virtualPose, velocity);
    }

    public void prepareForLocation(Pose pose) {
        prepareForLocation(pose, Velocity.zero());
    }

    public void spinUp(Pose pose) {
        flywheel.setTarget(Interpolation.getFlywheelVelocity(pose.x(), pose.y()));
    }

    public void aim(Pose pose, Velocity velocity) {
        Vector2D hivePosition = pose.y() > 70.75 ? hivePositionLeft : hivePositionRight;
        double turretAngle = Math.toDegrees(hivePosition.minus(pose.toVector2D()).theta());
        turret.setTargetDegrees(turretAngle - Math.toDegrees(pose.heading()) - turningGain * velocity.omega);
    }
}