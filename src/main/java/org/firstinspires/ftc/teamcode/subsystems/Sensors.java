package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ivy.Command;
import com.qualcomm.hardware.rev.RevTouchSensor;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.TelemetryTable;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;

@Config
public class Sensors {
    private final TelemetryTable table = Telemetry.table("Sensors");

    private final RevTouchSensor pollenSensor1;
    private final RevTouchSensor pollenSensor4;
    private final RevTouchSensor pollenSensor2;
    private final RevTouchSensor pollenSensor3;
    private long lastSeen4 = 0L;
    public static int hasAllDelayMs = 100;

    public Sensors(Context context) {
        pollenSensor1 = context.hardwareMap.get(RevTouchSensor.class, "pollenSensor1");
        pollenSensor4 = context.hardwareMap.get(RevTouchSensor.class, "pollenSensor4");
        pollenSensor2 = context.hardwareMap.get(RevTouchSensor.class, "pollenSensor2");
        pollenSensor3 = context.hardwareMap.get(RevTouchSensor.class, "pollenSensor3");
    }

    public Command periodic() {
        return infinite(() -> {
            lastSeen4 = ballCount() == 4 ? System.nanoTime() : 0;

            table.log("Transfer/1/Active", hasBall1());
            table.log("Transfer/4/Active", hasBall2());
            table.log("Transfer/2/Active", hasBall3());
            table.log("Transfer/3/Active", hasBall4());
        });
    }

    public boolean hasBall1() {
        return !pollenSensor1.isPressed();
    }

    public boolean hasBall2() {
        return !pollenSensor2.isPressed();
    }

    public boolean hasBall3() {
        return !pollenSensor3.isPressed();
    }

    public boolean hasBall4() {
        return !pollenSensor4.isPressed();
    }

    public int ballCount() {
        return (hasBall1() ? 1 : 0) + (hasBall2() ? 1 : 0) + (hasBall3() ? 1 : 0) + (hasBall4() ? 1 : 0);
    }

    public boolean hasAllBalls() {
        return ballCount() == 4 && System.nanoTime() - lastSeen4 >= hasAllDelayMs * 1e3;
    }
}
