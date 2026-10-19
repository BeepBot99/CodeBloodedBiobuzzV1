package org.firstinspires.ftc.teamcode.opmodes.base;

public abstract class TeleOpMode extends BaseOpMode {
    protected boolean initialized = false;

    @Override
    public void loop() {
        super.loop();

        double moveDelta = 0.15;
        if (!initialized &&
                (Math.abs(gamepad1.right_stick_x) > moveDelta
                        || Math.abs(gamepad1.left_stick_y) > moveDelta
                        || Math.abs(gamepad1.right_stick_x) > moveDelta)) {
            onFirstDriverInput();
            initialized = true;
        }
    }

    // allows you to hit play before beginning of teleop and only initialize and move servos after gamepad input has occurred. prolly a better way to do this than a full class
    protected void onFirstDriverInput() {};
}
