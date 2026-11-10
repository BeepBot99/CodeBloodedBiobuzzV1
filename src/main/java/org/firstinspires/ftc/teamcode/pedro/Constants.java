package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.OctoQuadConfig;
import com.pedropathing.revhub.localizers.OctoQuadLocalizer;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static OctoQuadConfig localizerConfig = new OctoQuadConfig(c -> {
        c.name.set("octoquad");
        c.xPodPort.set(0);
        c.yPodPort.set(1);
        c.ticksPerUnit.set(505.316944406);
        c.xPodOffset.set(-2.8346456692913384);
        c.yPodOffset.set(-6.397637795275591);
        c.xPodDirection.set(OctoQuad.EncoderDirection.REVERSE);
        c.yPodDirection.set(OctoQuad.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
        c.i2cRecoveryMode.set(OctoQuad.I2cRecoveryMode.MODE_1_PERIPH_RST_ON_FRAME_ERR);
        c.headingScalar.set(1.017021619801847);
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
                Controller primaryTranslationalForward = Controller.proportional(0.32725355134895723);
                Controller secondaryTranslationalForward = Controller.proportional(0.12091144284750019);
                Controller primaryTranslationalLateral = Controller.proportional(0.5080243003837853);
                Controller secondaryTranslationalLateral = Controller.proportional(0.18770140433249435);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.010374570392262049));
                c.brake.set(Controller.proportionalFeedforward(0.00881838483342274));

                c.headingFeedback.set(Controller.proportional(4.947213059865608));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.04894079092629648, 0.006489019044603466));

                c.linearBrakeCoefficients.set(Matrix.diag(0.08794740681202247, 0.033167483034046985));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.001578161985380227, 0.0023440005767685077));

                c.maxAchievableForwardVelocity.set(90.58113485638546);
                c.maxAchievableStrafeVelocity.set(70.79941311879176);
                c.naturalForwardDeceleration.set(29.28599244245199);
                c.naturalStrafeDeceleration.set(62.208428990072804);
            }
    );


    public static Follower create(HardwareMap hardwareMap) {
        return new Follower(new OctoQuadLocalizer(hardwareMap, localizerConfig), new Mecanum(hardwareMap, drivetrainConfig), new Foresight(foresightConfig));
    }
}