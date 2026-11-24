
package org.codeblooded.ftcodesim.hardware.devices;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class SimMotor implements DcMotorEx, SimHardwareDevice {

    public double velocity = 0;
    private double acceleration = 0;

    private double power = 0;
    private double current = 0;
    private double currentAlert = Double.POSITIVE_INFINITY;

    private boolean enabled = true;
    private boolean powerFloat = false;

    private int targetPosition = 0;
    private int currentPosition = 0;
    private int targetPositionTolerance = 5;

    private RunMode mode = RunMode.RUN_WITHOUT_ENCODER;
    private PIDCoefficients pidCoefficients = new PIDCoefficients(0, 0, 0);
    private PIDFCoefficients pidfCoefficients = new PIDFCoefficients(0, 0, 0, 0);

    public String deviceName;

    public ZeroPowerBehavior zeroPowerBehavior = ZeroPowerBehavior.FLOAT;
    public Direction direction = Direction.FORWARD;

    public SimMotorConfig config;

    public SimMotor(SimMotorConfig config) {
        this.config = config;
        this.deviceName = config.name;
    }

    private double getDirectionSign() {
        return direction == Direction.REVERSE ? -1.0 : 1.0;
    }

    private double getEffectivePower() {
        return power * getDirectionSign();
    }

    /**
     * Updates motor physics.
     *
     * Velocity is output-shaft angular velocity in rad/s.
     */
    @Override
    public void update(double deltaTime) {
        if (!enabled || deltaTime <= 0) {
            return;
        }

        double effectivePower = getEffectivePower();

        // FLOAT: Motor terminals are disconnected.
        // BRAKE: Motor terminals are shorted, allowing back-EMF
        // to generate opposing torque.
        boolean coasting = Math.abs(power) < 1e-5
                && zeroPowerBehavior == ZeroPowerBehavior.FLOAT;

        double torque;

        if (coasting) {
            torque = 0;
            current = 0;
        } else {
            torque = config.getTorque(effectivePower, velocity);
            current = config.getCurrent(effectivePower, velocity);
        }

        // Reflect motor rotor inertia through the gearbox.
        double gearRatio = config.gearRatio;

        double outputInertia =
                config.motorInertia * gearRatio * gearRatio
                        + config.loadInertia;

        if (outputInertia <= 0) {
            outputInertia = 1e-8;
        }

        // Newton's rotational equivalent: alpha = torque / inertia.
        acceleration = torque / outputInertia;

        // Integrate angular velocity.
        velocity += acceleration * deltaTime;

        // Update encoder position.
        double ticksPerRevolution = config.ticksPerRevolution;

        currentPosition += (int) Math.round(
                velocity * deltaTime
                        * ticksPerRevolution
                        / (2.0 * Math.PI)
                        * getDirectionSign()
        );
    }

    @Override
    public void reset() {
        power = 0;
        velocity = 0;
        acceleration = 0;
        current = 0;
        currentPosition = 0;
        targetPosition = 0;
        powerFloat = false;
    }

    public void setRollVelocity(double velocity) {
        this.velocity = velocity;
    }

    public boolean isStationary() {
        return Math.abs(velocity) < 1e-5
                && Math.abs(acceleration) < 1e-5;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setPower(double power) {
        this.power = Math.max(-1.0, Math.min(1.0, power));

        if (Math.abs(this.power) < 1e-5) {
            this.power = 0;
        }

        powerFloat = false;
    }

    @Override
    public double getPower() {
        return power;
    }

    public double getAcceleration() {
        return acceleration;
    }

    @Override
    public void setMotorEnable() {
        enabled = true;
    }

    @Override
    public void setMotorDisable() {
        enabled = false;
        power = 0;
    }

    @Override
    public boolean isMotorEnabled() {
        return enabled;
    }

    @Override
    public void setVelocity(double angularRate) {
        // Velocity control can be implemented with a simulated PID controller.
    }

    @Override
    public void setVelocity(double angularRate, AngleUnit unit) {
        setVelocity(AngleUnit.RADIANS.fromUnit(unit, angularRate));
    }

    @Override
    public double getVelocity() {
        return velocity * getDirectionSign();
    }

    @Override
    public double getVelocity(AngleUnit unit) {
        return unit.fromUnit(
                AngleUnit.RADIANS,
                getVelocity()
        );
    }

    @Override
    public void setPIDCoefficients(
            RunMode mode,
            PIDCoefficients pidCoefficients
    ) {
        this.pidCoefficients = pidCoefficients;
    }

    @Override
    public void setPIDFCoefficients(
            RunMode mode,
            PIDFCoefficients pidfCoefficients
    ) throws UnsupportedOperationException {
        this.pidfCoefficients = pidfCoefficients;
    }

    @Override
    public void setVelocityPIDFCoefficients(
            double p, double i, double d, double f
    ) {
        pidfCoefficients = new PIDFCoefficients(p, i, d, f);
    }

    @Override
    public void setPositionPIDFCoefficients(double p) {
        pidCoefficients = new PIDCoefficients(p, 0, 0);
    }

    @Override
    public PIDCoefficients getPIDCoefficients(RunMode mode) {
        return pidCoefficients;
    }

    @Override
    public PIDFCoefficients getPIDFCoefficients(RunMode mode) {
        return pidfCoefficients;
    }

    @Override
    public void setTargetPositionTolerance(int tolerance) {
        targetPositionTolerance = tolerance;
    }

    @Override
    public int getTargetPositionTolerance() {
        return targetPositionTolerance;
    }

    @Override
    public double getCurrent(CurrentUnit unit) {
        return  CurrentUnit.AMPS.convert(current, unit);
    }

    @Override
    public double getCurrentAlert(CurrentUnit unit) {
        return CurrentUnit.AMPS.convert(currentAlert, unit);
    }

    @Override
    public void setCurrentAlert(double current, CurrentUnit unit) {
        //currentAlert = CurrentUnit.AMPS.fromUnit(unit, current);
    }

    @Override
    public boolean isOverCurrent() {
        return current > currentAlert;
    }

    @Override
    public MotorConfigurationType getMotorType() {
        return new MotorConfigurationType();
    }

    @Override
    public void setMotorType(MotorConfigurationType motorType) {
    }

    @Override
    public DcMotorController getController() {
        return null;
    }

    @Override
    public int getPortNumber() {
        return 0;
    }

    @Override
    public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
        this.zeroPowerBehavior = zeroPowerBehavior;
    }

    @Override
    public ZeroPowerBehavior getZeroPowerBehavior() {
        return zeroPowerBehavior;
    }

    @Override
    public void setPowerFloat() {
        power = 0;
        powerFloat = true;
        zeroPowerBehavior = ZeroPowerBehavior.FLOAT;
    }

    @Override
    public boolean getPowerFloat() {
        return powerFloat;
    }

    @Override
    public void setTargetPosition(int position) {
        targetPosition = position;
    }

    @Override
    public int getTargetPosition() {
        return targetPosition;
    }

    @Override
    public boolean isBusy() {
        return mode == RunMode.RUN_TO_POSITION
                && Math.abs(targetPosition - currentPosition)
                > targetPositionTolerance;
    }

    @Override
    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void setMode(RunMode mode) {
        this.mode = mode;

        if (mode == RunMode.STOP_AND_RESET_ENCODER) {
            currentPosition = 0;
            targetPosition = 0;
            velocity = 0;
            acceleration = 0;
            power = 0;
        }
    }

    @Override
    public RunMode getMode() {
        return mode;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public String getConnectionInfo() {
        return "Simulated motor: " + deviceName;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {
        reset();
    }

    @Override
    public void close() {
        setMotorDisable();
    }
}
