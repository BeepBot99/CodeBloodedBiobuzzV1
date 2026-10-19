package org.firstinspires.ftc.teamcode.opmodes.base;

import android.annotation.SuppressLint;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.util.Menu;
import org.firstinspires.ftc.teamcode.util.Alliance;
import org.firstinspires.ftc.teamcode.util.RobotDataStore;

import java.util.List;

// or RobotOpMode
public abstract class BaseOpMode extends OpMode {
    private Menu initMenu;

    protected VoltageSensor voltageSensor;
    public Follower follower;
    private List<LynxModule> hubs;
    public Alliance alliance;

    public boolean isAuto = false;
    public long lastTime;
    private double deltaTime;

    @Override
    public void init() {
        initOpMode();
        initHardware();
        initMenus();
        loadRobotData();
        Scheduler.reset();
    }

    public void initOpMode() {
        isAuto = getClass().isAnnotationPresent(Autonomous.class);
        telemetry = new MultipleTelemetry(
                telemetry,
                FtcDashboard.getInstance().getTelemetry()
        );
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
    }

    public void initHardware() {
        follower = Constants.create(hardwareMap);

        hubs = hardwareMap.getAll(LynxModule.class);
        hubs.forEach(hub -> hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));

        voltageSensor = hardwareMap.voltageSensor.iterator().next();
    }

    public void initMenus() {
        initMenu = new Menu(gamepad1, "Select Alliance: ");
        initMenu.addOption("Red Alliance", () -> alliance = Alliance.RED);
        initMenu.addOption("Blue Alliance", () -> alliance = Alliance.BLUE);
    }

    public void loadRobotData() {
        RobotDataStore.loadData();
        String allianceName = RobotDataStore.get("alliance");
        if (allianceName != null) {
            alliance = Alliance.valueOf(allianceName);
            initMenu.confirmOption(alliance.ordinal());
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void init_loop() {
        hubs.forEach(LynxModule::clearBulkCache);

        initMenu.update();

        double voltage = voltageSensor.getVoltage();

        telemetry.addLine(initMenu.getDisplay());
        telemetry.addLine(String.format(
                "Battery: %s %.1f%% (%.2fV)",
                getBatteryStatus(voltage),
                voltage / 14.5 * 100,
                voltage
        ));
        logLoopTime();

        Scheduler.execute();

        telemetry.update();
    }

    public void logLoopTime() {
        deltaTime = (System.nanoTime() - lastTime) * 1e-9;
        telemetry.addData("Loop/Time (ms)", Math.round(deltaTime * 1000));
        telemetry.addData("Loop/Frequency (hz)", Math.round(1.0 / deltaTime * 100) / 100);
    }

    @Override
    public void start() {
        if (isAuto) {
            RobotDataStore.put("alliance", alliance.name());
        }
    }

    @Override
    public void loop() {
        follower.update();

        hubs.forEach(LynxModule::clearBulkCache);

        logLoopTime();
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public String getBatteryStatus(double voltage) {
        if (voltage < 10.0) {
            return "CRITICAL";
        } else if (voltage < 12.0) {
            return "LOW";
        } else if (voltage < 13.0) {
            return "OK";
        } else if (voltage < 14.0) {
            return "HIGH";
        } else {
            return "SUPERCHARGED";
        }
    }
}