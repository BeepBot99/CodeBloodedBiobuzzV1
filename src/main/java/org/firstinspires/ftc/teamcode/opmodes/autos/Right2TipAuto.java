package org.firstinspires.ftc.teamcode.opmodes.autos;

import com.pedropathing.api.PoseFactory;
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

@Autonomous(group = "Competition")
public class Right2TipAuto extends RobotOpMode {
    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(54.3, 6.15, 180);
    private final Pose cornerPose = poseFactory.of(10.5, 6.15, 180);
    private final Path intakeCorner = line(start, cornerPose).constant(cornerPose);
    private final Pose shootByFlower = poseFactory.of(46.5, 123, 90);
    private final Path toSecondShoot = curve(
            cornerPose,
            poseFactory.of(35, 24, 0),
            poseFactory.of(15, 100, 0),
            shootByFlower
    ).heading(
            Interpolator.piecewise()
                    .until(0.2, Interpolator.constant(cornerPose))
                    .until(1.0, Interpolator.constant(shootByFlower))
    );
    private final Pose flowerBackup = poseFactory.of(46.5, 118, 90);
    private final Pose flowerIntake = poseFactory.of(46.5, 126, 90);
    private final Path intakeFlowerLittleBackup = line(shootByFlower, flowerBackup).constant(flowerBackup);
    private final Path intakeFlowerForward = line(flowerBackup, flowerIntake).constant(flowerIntake);
    private final Path intakeFlowerBigBackup = line(flowerIntake, flowerBackup).constant(flowerBackup);
    private final Path toThirdShoot = line(flowerIntake, shootByFlower).constant(shootByFlower);

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
                        instant(() -> shootingController.prepareForLocation(start)),
                        instant(flywheel::on),
                        waitUntil(flywheel::atTarget),
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
                        instant(() -> shootingController.prepareForLocation(shootByFlower)),
                        parallel(
                                drivetrain.follow(toSecondShoot),
                                sequential(
                                        waitUntil(() -> drivetrain.follower.completion() >= 0.1),
                                        instant(corbicula::down)
                                )
                        ),
                        waitMs(1000),
                        intake.shoot(),
                        instant(intake.lower::on),
                        drivetrain.follow(intakeFlowerLittleBackup),
                        drivetrain.follow(intakeFlowerForward).raceWith(waitMs(2000)),
                        waitMs(1000),
                        drivetrain.follow(intakeFlowerBigBackup),
                        drivetrain.follow(intakeFlowerForward).raceWith(waitMs(2000)),
                        waitMs(1000),
                        drivetrain.follow(toThirdShoot),
                        waitMs(1000),
                        intake.shoot()
                )
        );
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
            drivetrain.follower.update();
            Drivetrain.localize(drivetrain.pose());
        });
    }
}
