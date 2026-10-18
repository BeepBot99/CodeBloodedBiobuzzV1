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
                Controller primaryTranslationalForward = Controller.proportional(0.226493318780903);
                Controller secondaryTranslationalForward = Controller.proportional(0.08368322927660432);
                Controller primaryTranslationalLateral = Controller.proportional(0.5015381114306278);
                Controller secondaryTranslationalLateral = Controller.proportional(0.18530493082846347);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.012957603483588636));
                c.brake.set(Controller.proportionalFeedforward(0.01101396296105034));

                c.headingFeedback.set(Controller.proportional(2.447421655157805));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.04730634263294, 0.007540928288975995));

                c.linearBrakeCoefficients.set(Matrix.diag(0.038561008825678214, 0.06927454668889409));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.002985751705369463, 0.0020371201468262264));

                c.maxAchievableForwardVelocity.set(62.80318372444954);
                c.maxAchievableStrafeVelocity.set(66.35440133280454);
                c.naturalForwardDeceleration.set(42.84137414187512);
                c.naturalStrafeDeceleration.set(62.12942961650345);
            }
    );

    public static OctoQuadConfig localizerConfig = new OctoQuadConfig(c -> {
        c.name.set("octoquad");
        c.xPodPort.set(0);
        c.yPodPort.set(1);
        c.xPodOffset.set(-1.41);
        c.yPodOffset.set(-2.559);
        c.xPodDirection.set(OctoQuad.EncoderDirection.REVERSE);
        c.yPodDirection.set(OctoQuad.EncoderDirection.REVERSE);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
        c.encoderResolutionUnit.set(DistanceUnit.MM);
        c.ticksPerUnit.set(37.25);
        c.i2cRecoveryMode.set(OctoQuad.I2cRecoveryMode.MODE_1_PERIPH_RST_ON_FRAME_ERR);
        c.headingScalar.set(1.0173443763967929);
    });

    public static Follower create(HardwareMap h) {
        return new Follower(new OctoQuadLocalizer(h, localizerConfig), new Mecanum(h, drivetrainConfig), new Foresight(foresightConfig));
    }
}