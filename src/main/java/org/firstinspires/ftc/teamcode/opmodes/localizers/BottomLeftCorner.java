package org.firstinspires.ftc.teamcode.opmodes.localizers;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;

@Autonomous(name = "Bottom Left Corner", group = "Localizer")
public class BottomLeftCorner extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Drivetrain.localize(new Pose(7.28, 6.15, 0));
    }
}
