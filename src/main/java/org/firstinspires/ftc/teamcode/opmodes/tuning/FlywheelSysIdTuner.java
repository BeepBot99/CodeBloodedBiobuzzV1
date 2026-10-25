
package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import gay.zharel.fateweaver.telemetry.Fate;
import gay.zharel.fateweaver.telemetry.FateTable;

import java.util.ArrayList;
import java.util.List;

@Config
@TeleOp(name = "Flywheel SysId Tuner", group = "Tuning")
public class FlywheelSysIdTuner extends OpMode {

    public static double START_POWER = 0.1;
    public static double END_POWER = 1.0;
    public static double POWER_STEP = 0.1;

    public static double SETTLE_TIME_SECONDS = 3.0;
    public static int SAMPLES_PER_STEP = 10;
    public static double SAMPLE_INTERVAL_SECONDS = 0.05;

    private final FateTable table = Fate.table("Flywheel SysId Tuner");

    private final List<Double> velocities = new ArrayList<>();
    private final List<Double> powers = new ArrayList<>();

    private DcMotorEx motor;

    private double currentPower;
    private long stepStartTime;
    private long lastSampleTime;

    private double velocitySum;
    private int sampleCount;

    private int stepSamples;
    private boolean settling;
    private boolean finished;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, "flywheel");

        currentPower = START_POWER;
        settling = true;
        finished = false;

        table.log("Status", "Ready");
        table.log("Start Power", START_POWER);
        table.log("End Power", END_POWER);
        table.log("Power Step", POWER_STEP);
        table.log("Settle Time (s)", SETTLE_TIME_SECONDS);
        table.log("Samples Per Step", SAMPLES_PER_STEP);
    }

    @Override
    public void start() {
        beginStep();
    }

    @Override
    public void loop() {
        if (finished) {
            motor.setPower(0);
            return;
        }

        long now = System.nanoTime();

        motor.setPower(currentPower);

        table.log("Power", currentPower);
        table.log("Velocity", motor.getVelocity());

        if (settling) {
            double elapsed = (now - stepStartTime) / 1e9;

            table.log("Status", "Settling");
            table.log("Elapsed (s)", elapsed);

            if (elapsed >= SETTLE_TIME_SECONDS) {
                settling = false;
                velocitySum = 0;
                sampleCount = 0;
                stepSamples = 0;
                lastSampleTime = now;
            }

            return;
        }

        double sampleInterval = SAMPLE_INTERVAL_SECONDS * 1e9;

        if (now - lastSampleTime >= sampleInterval) {
            lastSampleTime = now;

            velocitySum += motor.getVelocity();
            sampleCount++;
            stepSamples++;

            table.log("Sample Velocity", motor.getVelocity());
            table.log("Sample Count", stepSamples);
        }

        if (stepSamples >= SAMPLES_PER_STEP) {
            recordStep();

            currentPower += POWER_STEP;

            if (currentPower > END_POWER + 1e-9) {
                finish();
            } else {
                beginStep();
            }
        }
    }

    private void beginStep() {
        settling = true;
        stepStartTime = System.nanoTime();
        velocitySum = 0;
        sampleCount = 0;
        stepSamples = 0;

        motor.setPower(currentPower);

        table.log("Status", "New Step");
        table.log("Step Power", currentPower);
    }

    private void recordStep() {
        if (sampleCount == 0) {
            return;
        }

        double avgVelocity = velocitySum / sampleCount;

        velocities.add(avgVelocity);
        powers.add(currentPower);

        table.log("Average Velocity", avgVelocity);
        table.log("Recorded Power", currentPower);
        table.log("Data Points", velocities.size());

        double[] fit = linearRegression(velocities, powers);

        table.log("kS Estimate", fit[0]);
        table.log("kV Estimate", fit[1]);
        table.log("R^2", fit[2]);
    }

    private void finish() {
        finished = true;
        motor.setPower(0);

        if (velocities.size() >= 2) {
            double[] fit = linearRegression(velocities, powers);

            table.log("Status", "Complete");
            table.log("Final kS", fit[0]);
            table.log("Final kV", fit[1]);
            table.log("Final R^2", fit[2]);
            table.log("Total Data Points", velocities.size());
        } else {
            table.log("Status", "Insufficient Data");
        }
    }

    private static double[] linearRegression(
            List<Double> xValues,
            List<Double> yValues
    ) {
        int n = xValues.size();

        double meanX = 0;
        double meanY = 0;

        for (int i = 0; i < n; i++) {
            meanX += xValues.get(i);
            meanY += yValues.get(i);
        }

        meanX /= n;
        meanY /= n;

        double numerator = 0;
        double denominator = 0;

        for (int i = 0; i < n; i++) {
            double dx = xValues.get(i) - meanX;
            double dy = yValues.get(i) - meanY;

            numerator += dx * dy;
            denominator += dx * dx;
        }

        if (denominator == 0) {
            return new double[] {
                    Double.NaN, Double.NaN, Double.NaN
            };
        }

        double kV = numerator / denominator;
        double kS = meanY - kV * meanX;

        double residualSum = 0;
        double totalSum = 0;

        for (int i = 0; i < n; i++) {
            double predicted = kS + kV * xValues.get(i);
            double residual = yValues.get(i) - predicted;

            residualSum += residual * residual;

            double deviation = yValues.get(i) - meanY;
            totalSum += deviation * deviation;
        }

        double rSquared = totalSum == 0
                ? Double.NaN
                : 1.0 - residualSum / totalSum;

        return new double[] {kS, kV, rSquared};
    }

    @Override
    public void stop() {
        if (motor != null) {
            motor.setPower(0);
        }
    }
}
