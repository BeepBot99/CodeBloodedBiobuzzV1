package org.firstinspires.ftc.teamcode.control;

public class LockController {
    private double lockedTarget = 0;
    private boolean decelerating = false;
    public double lockingInputThreshold;
    public double maximumLockingVelocity;
    public boolean enabled = false;

    private final boolean hasAngularError;
    private final PredictiveController predictiveController;

    /**
     * Keeps track of a target locked position and outputs the power required to hold it.
     * Releases the hold/lock when the input is above lockingInputThreshold updates the lock once maximumLockingVelocity is reached
     */
    public LockController(double kP, double kL, double kQ, double lockingInputThreshold, double maximumLockingVelocity, boolean hasAngularError) {
        this.hasAngularError = hasAngularError;
        this.lockingInputThreshold = lockingInputThreshold;
        this.maximumLockingVelocity = maximumLockingVelocity;
        this.predictiveController = new PredictiveController(kP, kL, kQ);
    }

    /**
     * @param input desired motion from joystick
     * @param current current position
     * @param velocity current velocity
     */
    public double getOutput(double input, double current, double velocity) {
        if (!enabled) {
            return input;
        }

        if (Math.abs(input) > lockingInputThreshold) {
            decelerating = true;
            return input;
        } else if (Math.abs(velocity) < maximumLockingVelocity && decelerating) {
            lockedTarget = current;
            decelerating = false;
            return 0;
        } else if (decelerating) {
            return 0;
        } else {
            double error = lockedTarget - current;
            if (hasAngularError) {
                error = angleWrap(error);
            }
            return predictiveController.getOutput(error, velocity);
        }
    }

    public double getLockedTarget() {
        return lockedTarget;
    }

    private static double angleWrap(double angle) {
        while (angle <= -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}
