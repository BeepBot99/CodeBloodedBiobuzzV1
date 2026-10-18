package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.control.LockController;
import org.firstinspires.ftc.teamcode.opmodes.base.TeleOpMode;
import org.firstinspires.ftc.teamcode.utils.Alliance;


@Disabled
@TeleOp
public class FieldCentricTeleop extends TeleOpMode {

    // locks hold position independently for each axis
    LockController headingLock = new LockController(0.05, 0.05, 2, 0.1876, 0.0, true);
    LockController xLock = new LockController(0.05, 0.05, 0.05, 0.0644, 0.0021, false);
    LockController yLock = new LockController(0.05, 0.05, 0.05, 0.0644, 0.0021, false);

    // usage:
//            xLock.getOutput(gamepad1.left_stick_x, follower.pose().x(), follower.velocity().vx),
//            yLock.getOutput(-gamepad1.left_stick_y, follower.pose().y(), follower.velocity().vy),
//            headingLock.getOutput(-gamepad1.right_stick_x, follower.pose().heading(), follower.velocity().omega),
//            follower.pose().heading(),
//    alliance == Alliance.RED ? Math.PI : 0
//            ));

    @Override
    public void init() {
        super.init();
        headingLock = new LockController(0.05, 0.05, 2, 0.1876, 0.0, true);
    }

    @Override
    public void loop() {
        super.loop();

        telemetry.addData("xLock", xLock.getLockedTarget());
        telemetry.addData("yLock", yLock.getLockedTarget());
        telemetry.addData("headingLock", headingLock.getLockedTarget());
        telemetry.addData("pose", follower.pose());
        telemetry.addData("velocity", follower.velocity().toVector2D().magnitude());

        follower.manual(ManualDrive.fieldCentric(
                    gamepad1.left_stick_x,
                    -gamepad1.left_stick_y,
                    -gamepad1.right_stick_x/3,
                    follower.pose().heading(),
                    alliance == Alliance.RED ? Math.PI : 0
        ));
    }
}
