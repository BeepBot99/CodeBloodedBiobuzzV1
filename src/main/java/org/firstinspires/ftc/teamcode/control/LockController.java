package org.firstinspires.ftc.teamcode.control;

public class LockController {
    private double lockedTarget = 0;
    private boolean decelerating = false;
    private double lockingInputThreshold;
    private double maximumLockingVelocity;

    private final boolean hasAngularError;
    private final PredictiveController predictiveController;

    /**
     * Keeps track of a target locked position and outputs the power required to hold it.
     * Releases the hold/lock when the input is above lockingInputThreshold updates the lock once maximumLockingVelocity is reached
     */
    public LockController(boolean hasAngularError) {
        this.hasAngularError = hasAngularError;
        this.predictiveController = new PredictiveController(0,0,0);
    }

    public void setGains(double lockingInputThreshold, double maximumLockingVelocity, double kP, double kL, double kQ) {
        this.lockingInputThreshold = lockingInputThreshold;
        this.maximumLockingVelocity = maximumLockingVelocity;
        predictiveController.setGains(kP, kL, kQ);
    }

    /**
     * @param input desired motion from joystick
     * @param current current position
     * @param velocity current velocity
     */
    public double getOutput(double input, double current, double velocity) {
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
