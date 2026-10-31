package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import gay.zharel.fateweaver.telemetry.Fate;
import gay.zharel.fateweaver.telemetry.FateTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pedropathing.utils.Utils.quadraticFit;

@TeleOp(name = "Turret Braking Tuner", group = "Tuning")
@Config
public class TurretTuner extends LinearOpMode { // is linear opmode cuz copied from pedro tuner
    private final FateTable table = Fate.table("Turret");

    public static double accelTime = 2;
    public static int trials = 12;
    public static double maxPower = 1;
    public static double minPower = 0.2;
    public static double bias = 1.5; // how much it favors doing trials with higher powers
    public static double brakingPower = 0.001;

    public static double MAX_BRAKE_TIME = 2;

    private final ElapsedTime timer = new ElapsedTime();

    private final List<double[]> velocityToBrakingDistance = new ArrayList<>();
    private State state = State.DRIVE;
    private int iteration = 0;
    private double power;

    private double startHeading;
    private double measuredVelocity;

    @Override
    public void runOpMode() {
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, "turret");

        List<Double> coefficients = Collections.emptyList();

        double[] POWERS = biasedGradient(trials, maxPower, minPower, bias);

        waitForStart();
        timer.reset();

        while (state != State.DONE && opModeIsActive()) {
            int currentHeading = motor.getCurrentPosition();
            double currentVelocity = motor.getVelocity();

            table.log("Heading", currentHeading);
            table.log("Velocity", currentVelocity);

            int direction = (iteration % 2 == 0) ? 1 : -1;
            if (iteration < POWERS.length) {
                power = POWERS[iteration];
            }

            switch (state) {
                case DRIVE: {
                    if (timer.seconds() > accelTime) {
                        startHeading = currentHeading;
                        measuredVelocity = Math.abs(currentVelocity);

                        motor.setPower(-brakingPower * direction);
                        state = State.BRAKE;
                        timer.reset();
                        break;
                    }
                    motor.setPower(power * direction);
                    break;
                }
                case BRAKE: {
                    if (Math.abs(currentVelocity) > 0.001 && timer.seconds() < MAX_BRAKE_TIME) {
                        motor.setPower(-brakingPower * direction);
                        break;
                    }

                    double brakingDistance = Math.abs(currentHeading - startHeading);

                    velocityToBrakingDistance.add(new double[]{measuredVelocity, brakingDistance});

                    iteration++;

                    if (iteration >= POWERS.length) {
                        motor.setPower(0.0);

                        double[] c = quadraticFit(velocityToBrakingDistance);
                        coefficients = List.of(c[0], c[1]);

                        state = State.DONE;
                    } else {
                        timer.reset();
                        state = State.DRIVE;
                    }
                    break;
                }
                case DONE: {}
            }
        }

        while (opModeIsActive()) {
            table.log("kL", coefficients.get(0));
            table.log("kQ", coefficients.get(1));
            telemetry.update();
        }
    }

    private enum State {
        DRIVE,
        BRAKE,
        DONE
    }

    private static double[] biasedGradient(
            int count,
            double max,
            double min,
            double bias
    ) {
        if (count < 2) return new double[]{max};

        double[] values = new double[count];

        for (int i = 0; i < count; i++) {
            double t = (double) i / (count - 1);

            double curved = 1 - Math.pow(t, bias);

            values[i] = min + curved * (max - min);
        }

        return values;
    }
}