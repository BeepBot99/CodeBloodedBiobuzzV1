package org.firstinspires.ftc.teamcode.utils;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx;
import dev.frozenmilk.dairy.cachinghardware.CachingServo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config("Settings")
public final class Context {
    public static boolean useAdvantageScopeNotation = false;
    public final HardwareMap hardwareMap;
    public final Telemetry telemetry;

    public Context(OpMode opMode) {
        hardwareMap = opMode.hardwareMap;
        telemetry = new MultipleTelemetry(
                opMode.telemetry,
                FtcDashboard.getInstance().getTelemetry()
        );
    }

    public void addPose(String name, Pose pose) {
        if (useAdvantageScopeNotation) {
            telemetry.addData(name + " x", 144 - pose.x());
            telemetry.addData(name + " y", 144 - pose.y());
            telemetry.addData(name + " heading (deg)", 180 + Math.toDegrees(pose.heading()));
        } else {
            telemetry.addData(name + " x", pose.x());
            telemetry.addData(name + " y", pose.y());
            telemetry.addData(name + " heading (deg)", Math.toDegrees(pose.heading()));
        }
    }

    public VoltageSensor voltageSensor() {
        return hardwareMap.voltageSensor.iterator().next();
    }

    public Servo servo(String name) {
        return new CachingServo(hardwareMap.servo.get(name));
    }

    public DcMotorEx motor(String name) {
        return new CachingDcMotorEx(hardwareMap.get(DcMotorEx.class, name));
    }
}