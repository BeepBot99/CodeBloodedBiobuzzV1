package org.firstinspires.ftc.teamcode.opmodes.localizers;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

@Autonomous(name = "Bottom Left Corner", group = "Localizer")
public class BottomLeftCorner extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        Drivetrain.localize(new Pose(6.15, 7.28, Math.PI / 2));
        Turret.localize(0);
    }
}
