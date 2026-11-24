
package org.firstinspires.ftc.teamcode.opmodes.autos;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.interpolator.Interpolator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.paths.interpolator.Interpolator.constant;

@Autonomous(group = "Competition")
public class WrenchDressingAuto extends RobotOpMode {
    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose shootingPose = poseFactory.of(54.3, 6.15, 180);
    private final Pose offWallShoot = poseFactory.of(54.3, 6.15+3, 180);
    private final Pose cornerPose = poseFactory.of(10.5, 6.15, 180);
    private final Path intakeCorner = line(shootingPose, cornerPose).constant(cornerPose);
    private final Path toThirdShoot = line(cornerPose, offWallShoot).constant(shootingPose);
    private final Pose flowerBackup = poseFactory.of(23.5, 46.5, 180);
    private final Pose flowerIntake = poseFactory.of(15, 46.5, 180);
    private final Path prepareForFlower = line(shootingPose, flowerBackup).constant(flowerBackup);
    private final Path slamIntoFlower = line(flowerBackup, flowerIntake).constant(flowerIntake);
    private final Path backUp = line(flowerIntake, flowerBackup).constant(flowerBackup);
    private final Path toSecondShoot = line(flowerBackup, offWallShoot).constant(shootingPose);

    private final Pose parkPose = poseFactory.of(14, 94, 0);
    private final Pose parkPoseControl = poseFactory.of(18, 32, 0);
    private final Path parkPath = curve(shootingPose, parkPoseControl, parkPose).heading(
            Interpolator.piecewise()
                    .until(0.5, constant(shootingPose))
                    .until(1.0, constant(parkPose))
    );

    @Override
    public void init() {
        super.init();
        turret.setStartingAngle(0);
        turret.on();
    }

    @Override
    public void start() {
        drivetrain.follower.setPose(shootingPose);
        drivetrain.follower.update();
        schedule(
                sequential(
                        say("i am start"),
                        instant(() -> shootingController.prepareForLocationSlow(shootingPose)),
                        instant(flywheel::on),
                        waitUntil(flywheel::atTarget),
                        say("i am shoot"),
                        intake.shoot(),
                        instant(corbicula::down),
                        drivetrain.follow(prepareForFlower),
                        instant(intake.lower::on),
                        drivetrain.follow(slamIntoFlower).raceWith(waitMs(2000)),
                        waitMs(1000),
                        drivetrain.follow(backUp),
                        drivetrain.follow(slamIntoFlower).raceWith(waitMs(2000)),
                        waitMs(1000),
                        drivetrain.follow(backUp),
                        instant(intake.lower::off),
                        drivetrain.follow(toSecondShoot).raceWith(waitMs(4500)),
                        instant(corbicula::up),
                        waitMs(11500),
                        intake.shoot(),
                        parallel(
                                drivetrain.follow(intakeCorner),
                                sequential(
                                        waitUntil(() -> drivetrain.follower.completion() >= 0.1),
                                        instant(() -> Constants.foresightConfig.maxPathSpeed.set(0.2)),
                                        instant(intake.lower::on)
                                )
                        ),
                        instant(() -> Constants.foresightConfig.maxPathSpeed.set(1.0)),
                        waitMs(500),
                        instant(intake.lower::off),
                        drivetrain.follow(toThirdShoot).raceWith(waitMs(4500)),
                        //waitMs(200),
                        intake.shoot(),
                        instant(intake.lower::off)
                        //drivetrain.follow(parkPath)
                )
        );
    }

    private Command say(String something) {
        return instant(() -> telemetry.speak(something));
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
            drivetrain.follower.update();
            Drivetrain.setTransferPose(drivetrain.pose());
        });
    }
}
