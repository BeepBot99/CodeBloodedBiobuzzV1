package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.ivy.Command;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.AnalogInput;
import gay.zharel.fateweaver.telemetry.Fate;
import gay.zharel.fateweaver.telemetry.FateTable;
import org.firstinspires.ftc.teamcode.util.Context;

import static com.pedropathing.ivy.commands.Commands.infinite;

public class Sensors {
    private final FateTable table = Fate.table("Sensors");

    private final RevTouchSensor pollenSensor1;
    private final RevTouchSensor pollenSensor4;
    private final AnalogInput pollenSensor2;
    private final AnalogInput pollenSensor3;

    public Sensors(Context context) {
        pollenSensor1 = context.hardwareMap.get(RevTouchSensor.class, "pollenSensor1");
        pollenSensor4 = context.hardwareMap.get(RevTouchSensor.class, "pollenSensor4");
        pollenSensor2 = context.hardwareMap.get(AnalogInput.class, "pollenSensor2");
        pollenSensor3 = context.hardwareMap.get(AnalogInput.class, "pollenSensor3");
    }

    public Command periodic() {
        return infinite(() -> {
            table.log("Transfer/1/Active", !pollenSensor1.isPressed());
            table.log("Transfer/4/Active", !pollenSensor4.isPressed());
            table.log("Transfer/2/Voltage", pollenSensor2.getVoltage());
            table.log("Transfer/3/Voltage", pollenSensor3.getVoltage());
            table.log("Transfer/2/Distance", pollenSensor2.getVoltage() / 3.3 * 100);
            table.log("Transfer/3/Distance", pollenSensor3.getVoltage() / 3.3 * 100);
        });
    }
}
