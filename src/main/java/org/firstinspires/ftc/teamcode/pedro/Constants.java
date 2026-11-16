package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-2.8030743936854083);
        c.yPodOffset.set(-6.4368697038785685);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static MecanumConfig drivetrainConfig = new MecanumConfig(
            c -> {
                c.frontLeftName.set("frontLeft");
                c.backLeftName.set("backLeft");
                c.frontRightName.set("frontRight");
                c.backRightName.set("backRight");
                c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
                c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
                c.manualBrakeMode.set(true);
            }
    );

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.3749354705595852);
                Controller secondaryTranslationalForward = Controller.proportional(0.13852863791148073);
                Controller primaryTranslationalLateral = Controller.proportional(0.674955779793376);
                Controller secondaryTranslationalLateral = Controller.proportional(0.2493781254830582);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.012319384853404922));
                c.brake.set(Controller.proportionalFeedforward(0.010471477125394184));

                c.headingFeedback.set(Controller.proportional(6.482189589500453));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.06591885262508325, 0.006258982920009603));

                c.linearBrakeCoefficients.set(Matrix.diag(0.11480045964089415, 0.06938219862646579));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0013365208578786604, 0.0019249516302999278));

                c.maxAchievableForwardVelocity.set(77.05649601993028);
                c.maxAchievableStrafeVelocity.set(61.043326538685434);
                c.naturalForwardDeceleration.set(29.616247467875542);
                c.naturalStrafeDeceleration.set(69.69103931170474);
            }
    );


    public static Follower create(HardwareMap hardwareMap) {
        return new Follower(new PinpointLocalizer(hardwareMap, localizerConfig), new Mecanum(hardwareMap, drivetrainConfig), new Foresight(foresightConfig));
    }
}