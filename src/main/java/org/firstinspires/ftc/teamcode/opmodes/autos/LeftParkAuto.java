package org.firstinspires.ftc.teamcode.opmodes.autos;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

@Autonomous(group = "Competition")
public class LeftParkAuto extends RobotOpMode {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(7.86, 133.4, -90);
    private final Pose park = poseFactory.of(12, 125, -90);

    @Override
    public void init() {
        super.init();
        turret.setStartingAngle(0);
    }

    @Override
    public void start() {
        drivetrain.follower.setPose(start);
        schedule(
                sequential(
                        follow(drivetrain.follower, line(start, park).constant(3 * Math.PI / 2))
                )
        );
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
            drivetrain.follower.update();
            Drivetrain.setTransferPose(drivetrain.pose());
        });
    }
}
