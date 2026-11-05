package org.firstinspires.ftc.teamcode.util;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx;
import dev.frozenmilk.dairy.cachinghardware.CachingServo;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.telemetry.TelemetryBackend;
import dev.nextftc.telemetry.ftc.DriverStationBackend;

@Config("Settings")
public final class Context implements AutoCloseable {
    public final HardwareMap hardwareMap;
    private final TelemetryBackend dashboardBackend;

    public Context(OpMode opMode) {
        hardwareMap = opMode.hardwareMap;

        dashboardBackend = new DriverStationBackend(FtcDashboard.getInstance().getTelemetry());

        Telemetry.getRegistry().register(dashboardBackend);
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
        Telemetry.getRegistry().unregister(dashboardBackend);
    }
}