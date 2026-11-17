package org.firstinspires.ftc.teamcode.opmodes.autos;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Velocity;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.interpolator.Interpolator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;
import org.firstinspires.ftc.teamcode.opmodes.teleop.CompetitionTeleOp;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

@Autonomous(group = "Competition")
public class Right2TipAuto extends RobotOpMode {
    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(54.3, 6.15, 180);
    private final Pose drivePosition = poseFactory.of(10.5, 6.4, 180);
//    private final Pose turnPosition = poseFactory.of(18.5, 23, 82);
//    private final Pose shoot1Position = poseFactory.of(45.45, 120.4, 80);
//    private final Pose flowerIntakePosition = poseFactory.of(47.5, 125.5, 90);

    private final Pose backUpPose = poseFactory.of(17.8, 11.1, 138);
    private final Pose acrossToFlowerPose = poseFactory.of(30.3, 109, 90);
    private final Pose preFlowerPose = poseFactory.of(47.5, 120.8, 90);
    private final Pose flowerPose = poseFactory.of(47.5, 125.2, 90);

    @Override
    public void init() {
        super.init();
        turret.setStartingAngle(0);
        turret.on();
    }

    @Override
    public void start() {
        drivetrain.follower.setPose(start);
        drivetrain.follower.update();
        schedule(
                sequential(
                        instant(() -> shootingController.prepareForLocation(start, Velocity.zero())),
                        instant(flywheel::on),
                        waitUntil(flywheel::atTarget),
                        intake.shoot(),
                        waitMs(2000),
                        intake.intake(),
                        instant(flywheel::off),
                        follow(drivetrain.follower, Paths.line(start, drivePosition).constant(start).with(
                                Constants.foresightConfig.maxPathSpeed.at(0.3))),
                        waitMs(1500),
                        follow(drivetrain.follower, Paths.line(drivePosition, backUpPose).constant(start)), // back up pose
                        instant(corbicula::down),
                        instant(flywheel::on),
                        //follow(drivetrain.follower, Paths.line(drivePosition, turnPosition).linear(drivePosition, turnPosition)),
//                        follow(drivetrain.follower, Paths.line(turnPosition, shoot1Position).heading(Interpolator.piecewise().until(0.7, Interpolator.tangent).until(1, Interpolator.constant(flowerIntakePosition)))),

                        follow(drivetrain.follower, Paths.line(backUpPose, acrossToFlowerPose).constant(acrossToFlowerPose)),
                        follow(drivetrain.follower, Paths.line(acrossToFlowerPose, preFlowerPose).constant(preFlowerPose)),

//                        follow(drivetrain.follower, Paths.line(turnPosition, shoot1Position).heading(Interpolator.piecewise().until(0.7, Interpolator.tangent).until(1, Interpolator.constant(flowerIntakePosition)))),
                        instant(() -> shootingController.prepareForLocation(drivetrain.follower.pose(), Velocity.zero())),
                        waitMs(1500),
                        waitUntil(flywheel::atTarget),
                        intake.shoot(),
                        waitMs(1000),
                        follow(drivetrain.follower, Paths.line(preFlowerPose, flowerPose).constant(flowerPose)),

//
//                        follow(drivetrain.follower, Paths.line(shoot1Position, flowerIntakePosition).constant(flowerIntakePosition).with(Constants.foresightConfig.maxPathSpeed.at(.9))),
//                        waitMs(2000),
//                        follow(drivetrain.follower, Paths.line(flowerIntakePosition, shoot1Position.withHeading(Math.toDegrees(90))).constant(flowerIntakePosition)),
//                        follow(drivetrain.follower, Paths.line(shoot1Position, flowerIntakePosition).constant(flowerIntakePosition).with(Constants.foresightConfig.maxPathSpeed.at(.9))),
                        waitMs(2000),
                        intake.off()
                )
        );
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
            drivetrain.follower.update();
            Drivetrain.localize(drivetrain.pose());
            shootingController.prepareForLocation(drivetrain.follower.pose(), Velocity.zero());
        });
    }
}
