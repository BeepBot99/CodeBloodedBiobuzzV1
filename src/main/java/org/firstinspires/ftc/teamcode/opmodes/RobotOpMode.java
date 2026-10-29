package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.ivy.Scheduler;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import gay.zharel.fateweaver.telemetry.Fate;
import org.firstinspires.ftc.teamcode.subsystems.*;
import org.firstinspires.ftc.teamcode.util.Context;

import java.util.List;

import static com.pedropathing.ivy.Scheduler.schedule;

public abstract class RobotOpMode extends OpMode {
    private final ElapsedTime loopTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    protected Context context;
    protected Drivetrain drivetrain;
    protected Intake intake;
    protected Flywheel flywheel;
    protected Blocker blocker;
    protected Corbicula corbicula;
    protected Sensors sensors;
    private List<LynxModule> hubs;

    @Override
    public void init() {
        Scheduler.reset();
        context = new Context(this);

        drivetrain = new Drivetrain(context);
        intake = new Intake(context);
        flywheel = new Flywheel(context);
        blocker = new Blocker(context);
        corbicula = new Corbicula(context);
        sensors = new Sensors(context);

        schedule(
                drivetrain.periodic(),
                intake.periodic(),
                flywheel.periodic(),
                blocker.periodic(),
                corbicula.periodic(),
                sensors.periodic()
        );

        hubs = hardwareMap.getAll(LynxModule.class);
        hubs.forEach(hub -> hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL));
    }

    @Override
    public void init_loop() {
        Fate.log("Robot/Loop/Time (ms)", loopTimer.milliseconds());
        loopTimer.reset();

        Scheduler.execute();
        context.telemetry.update();
    }

    protected final void wrapLoop(Runnable runnable) {
        Fate.log("Robot/Loop/Time (ms)", loopTimer.milliseconds());
        loopTimer.reset();
        hubs.forEach(LynxModule::clearBulkCache);

        runnable.run();

        Scheduler.execute();
        context.telemetry.update();
    }

    @Override
    public void stop() {
        context.close();
    }
}