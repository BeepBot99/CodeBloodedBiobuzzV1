package org.firstinspires.ftc.teamcode.opmodes.autos;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import dev.nextftc.telemetry.ftc.FtcTelemetry;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;

import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

@Autonomous(group = "Competition")
public class Auto extends RobotOpMode {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(53, 6.15, 90);
    private final Pose path1 = poseFactory.of(11.7185, 96.697, 90);
    private final Pose path1Control1 = poseFactory.of(22.1752, 35.8281, 0);



    public Path path1() {
        return Paths.curve(start, path1Control1, path1).constant(path1);
    }

    @Override
    public void start() {
        flywheel.setTarget(1685);
        turret.setTargetDegrees(88 - 90);
        drivetrain.follower.setPose(start);
        schedule(
                sequential(
                        instant(flywheel::on),
                        waitUntil(flywheel::atTarget),
                        intake.lower.on(),
                        intake.upper.setPower(0.5),
                        waitMs(2000),
                        intake.off(),
                        instant(flywheel::off),
                        follow(drivetrain.follower, path1())
                )
        );
    }

    @Override
    public void loop() {
        wrapLoop(() -> {});
    }
}
