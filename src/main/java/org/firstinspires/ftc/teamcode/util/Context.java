package org.firstinspires.ftc.teamcode.util;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx;
import dev.frozenmilk.dairy.cachinghardware.CachingServo;
import gay.zharel.fateweaver.flight.FtcTelemetryBackend;
import gay.zharel.fateweaver.telemetry.Fate;
import gay.zharel.fateweaver.telemetry.FateBackend;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config("Settings")
public final class Context implements AutoCloseable {
    public final HardwareMap hardwareMap;
    public final Telemetry telemetry;
    private final FateBackend telemetryBackend;

    public Context(OpMode opMode) {
        hardwareMap = opMode.hardwareMap;

        telemetry = new MultipleTelemetry(
                opMode.telemetry,
                FtcDashboard.getInstance().getTelemetry()
        );

        telemetryBackend = new FtcTelemetryBackend(telemetry);
        Fate.getRegistry().register(telemetryBackend);
    }
    public VoltageSensor voltageSensor() {
        return hardwareMap.voltageSensor.iterator().next();
    }

    public Servo servo(String name) {
        return new CachingServo(hardwareMap.get(Servo.class, name));
    }

    public DcMotorEx motor(String name) {
        return new CachingDcMotorEx(hardwareMap.get(DcMotorEx.class, name));
    }

    @Override
    public void close() {
        Fate.getRegistry().unregister(telemetryBackend);
    }
}