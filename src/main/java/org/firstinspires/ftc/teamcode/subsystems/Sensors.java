package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.ivy.Command;
import com.qualcomm.hardware.rev.RevTouchSensor;
import gay.zharel.fateweaver.telemetry.Fate;
import gay.zharel.fateweaver.telemetry.FateTable;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;

public class Sensors {
    private final FateTable table = Fate.table("Sensors");

    private final RevTouchSensor pollenSensor1;
    private final RevTouchSensor pollenSensor2;
    private final RevTouchSensor pollenSensor3;
    private final RevTouchSensor pollenSensor4;

    public Sensors(Context context) {
        pollenSensor1 = context.hardwareMap.get(RevTouchSensor.class, "pollenSensor1");
        pollenSensor2 = context.hardwareMap.get(RevTouchSensor.class, "pollenSensor2");
        pollenSensor3 = context.hardwareMap.get(RevTouchSensor.class, "pollenSensor3");
        pollenSensor4 = context.hardwareMap.get(RevTouchSensor.class, "pollenSensor4");
    }

    public Command periodic() {
        return infinite(() -> {
            table.log("pollenSensor1", pollenSensor1.isPressed());
            table.log("pollenSensor2", pollenSensor2.isPressed());
            table.log("pollenSensor3", pollenSensor3.isPressed());
            table.log("pollenSensor4", pollenSensor4.isPressed());
        });
    }
}
