package org.firstinspires.ftc.teamcode.opmodes.autos;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import dev.nextftc.telemetry.ftc.FtcTelemetry;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

@Autonomous(group = "Competition")
public class Auto2 extends RobotOpMode {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(6.15, 141.5 - 7.28, -90);
    private final Pose path1 = poseFactory.of(12, 114, -90);


    @Override
    public void start() {
        drivetrain.follower.setPose(start);
        schedule(
                sequential(
                        follow(drivetrain.follower, line(start, path1))
                )
        );
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
            Drivetrain.localize(drivetrain.pose());
        });
    }
}
