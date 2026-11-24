
package org.codeblooded.ftcodesim.hardware.devices;

public class SimMotorConfig {

    public final String hardwareMapName;
    public final String name;

    // Motor specifications
    public double nominalVoltage = 12.0;
    public double freeSpeedRPM = 6000;
    public double freeCurrent = 1.5;
    public double stallCurrent = 10.0;
    public double stallTorque = 0.2;

    // Mechanical properties
    public double gearRatio = 1.0;
    public double motorCount = 1.0;
    public double efficiency = 1.0;

    // Rotational inertia
    public double motorInertia = 1.0e-5;
    public double loadInertia = 0.0;

    // Encoder properties
    public double ticksPerRevolution = 28.0;

    // Simulation properties
    public double staticVelocityRegion = 0.0;
    public double staticFriction = 0.0;

    // Stop response coefficients
    public double kL = 0.0;
    public double kQ = 0.0;

    // Electrical properties (derived)
    public double resistance;
    public double torqueConstant;
    public double velocityConstant;

    public SimVoltageSensor voltageSensor;

    public SimMotorConfig(
            String hardwareMapName,
            String name,
            SimVoltageSensor voltageSensor
    ) {
        this.hardwareMapName = hardwareMapName;
        this.name = name;
        this.voltageSensor = voltageSensor;
        updateConstants();
    }

    public static SimMotorConfig empty(String hardwareMapName) {
        return new SimMotorConfig(hardwareMapName, hardwareMapName, new SimVoltageSensor());
    }

    public SimMotorConfig(
            String hardwareMapName,
            String name,
            double nominalVoltage,
            double freeSpeedRPM,
            double freeCurrent,
            double stallCurrent,
            double stallTorque,
            SimVoltageSensor voltageSensor
    ) {
        this.hardwareMapName = hardwareMapName;
        this.name = name;
        this.nominalVoltage = nominalVoltage;
        this.freeSpeedRPM = freeSpeedRPM;
        this.freeCurrent = freeCurrent;
        this.stallCurrent = stallCurrent;
        this.stallTorque = stallTorque;
        this.voltageSensor = voltageSensor;

        updateConstants();
    }

    /**
     * Derives electrical constants from motor specifications.
     */
    public void updateConstants() {
        resistance = nominalVoltage / stallCurrent;

        torqueConstant = stallTorque / stallCurrent;

        velocityConstant =
                (freeSpeedRPM * 2.0 * Math.PI / 60.0)
                        / (nominalVoltage - resistance * freeCurrent);
    }

    /**
     * Calculates output shaft torque.
     *
     * @param power Applied motor power [-1, 1]
     * @param angularVelocity Output shaft angular velocity (rad/s)
     */
    public double getTorque(double power, double angularVelocity) {
        double voltage = voltageSensor.getVoltage();

        double motorVoltage = power * voltage;

        double backEMF = angularVelocity * gearRatio / velocityConstant;

        double current = (motorVoltage - backEMF) / resistance;

        double maxCurrent = stallCurrent * voltage / nominalVoltage;

        current = Math.max(-maxCurrent, Math.min(maxCurrent, current));

        return current * torqueConstant
                * gearRatio
                * motorCount
                * efficiency;
    }

    /**
     * Calculates motor current draw.
     */
    public double getCurrent(double power, double angularVelocity) {
        double voltage = voltageSensor.getVoltage();

        double motorVoltage = power * voltage;
        double backEMF = angularVelocity * gearRatio / velocityConstant;

        return Math.abs((motorVoltage - backEMF) / resistance);
    }

    /**
     * Calculates theoretical output shaft free speed (rad/s).
     */
    public double getFreeSpeed() {
        return freeSpeedRPM * 2.0 * Math.PI / 60.0
                * voltageSensor.getVoltage() / nominalVoltage
                / gearRatio;
    }

    /**
     * Calculates theoretical output shaft stall torque (Nm).
     */
    public double getStallTorque() {
        return stallTorque
                * voltageSensor.getVoltage() / nominalVoltage
                * gearRatio
                * motorCount
                * efficiency;
    }

    /**
     * Calculates reflected output shaft inertia.
     */
    public double getOutputInertia() {
        return motorInertia * gearRatio * gearRatio + loadInertia;
    }

    // ---------------------------------------------------------
    // goBILDA presets
    // ---------------------------------------------------------

    public static SimMotorConfig gobilda5000(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return gobildaMotor(
                hardwareMapName,
                "goBILDA 5000 Series Bare Motor",
                1.0,
                6000,
                1.47,
                voltageSensor
        );
    }

    public static SimMotorConfig yellowJacket30(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return yellowJacket(hardwareMapName, 30, 250, voltageSensor);
    }

    public static SimMotorConfig yellowJacket43(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return yellowJacket(hardwareMapName, 43, 185, voltageSensor);
    }

    public static SimMotorConfig yellowJacket60(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return yellowJacket(hardwareMapName, 60, 133.2, voltageSensor);
    }

    public static SimMotorConfig yellowJacket84(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return yellowJacket(hardwareMapName, 84, 93.6, voltageSensor);
    }

    public static SimMotorConfig yellowJacket117(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return yellowJacket(hardwareMapName, 117, 68.4, voltageSensor);
    }

    public static SimMotorConfig yellowJacket223(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return yellowJacket(hardwareMapName, 223, 38.0, voltageSensor);
    }

    public static SimMotorConfig yellowJacket312(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return yellowJacket(hardwareMapName, 312, 24.3, voltageSensor);
    }

    public static SimMotorConfig yellowJacket435(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return yellowJacket(hardwareMapName, 435, 18.7, voltageSensor);
    }

    public static SimMotorConfig yellowJacket1150(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return yellowJacket(hardwareMapName, 1150, 7.9, voltageSensor);
    }

    public static SimMotorConfig yellowJacket1620(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return yellowJacket(hardwareMapName, 1620, 5.4, voltageSensor);
    }

    /**
     * Creates a bare 5000 Series motor with optional gearing.
     *
     * Stall torque is specified in kg.cm.
     */
    private static SimMotorConfig gobildaMotor(
            String hardwareMapName,
            String name,
            double gearRatio,
            double freeSpeedRPM,
            double stallTorqueKgCm,
            SimVoltageSensor voltageSensor
    ) {
        SimMotorConfig config = new SimMotorConfig(
                hardwareMapName,
                name,
                voltageSensor
        );

        config.nominalVoltage = 12.0;
        config.freeSpeedRPM = freeSpeedRPM;
        config.freeCurrent = 0.25;
        config.stallCurrent = 9.2;
        config.stallTorque = stallTorqueKgCm * 0.0980665;

        config.gearRatio = gearRatio;
        config.motorCount = 1.0;

        config.updateConstants();

        return config;
    }

    /**
     * Creates a goBILDA Yellow Jacket planetary motor.
     *
     * Output stall torque is specified in kg.cm.
     */
    private static SimMotorConfig yellowJacket(
            String hardwareMapName,
            double outputRPM,
            double outputStallTorqueKgCm,
            SimVoltageSensor voltageSensor
    ) {
        double gearRatio = 6000.0 / outputRPM;

        SimMotorConfig config = gobildaMotor(
                hardwareMapName,
                "goBILDA Yellow Jacket " + (int) outputRPM + " RPM",
                gearRatio,
                6000.0,
                1.47,
                voltageSensor
        );

        double idealOutputTorque = config.stallTorque * gearRatio;
        double actualOutputTorque =
                outputStallTorqueKgCm * 0.0980665;

        config.efficiency = actualOutputTorque / idealOutputTorque;

        return config;
    }

    /**
     * Creates an unconfigured motor.
     */
    public static SimMotorConfig empty(
            String hardwareMapName,
            SimVoltageSensor voltageSensor
    ) {
        return new SimMotorConfig(
                hardwareMapName,
                "Unknown Motor",
                voltageSensor
        );
    }
}
