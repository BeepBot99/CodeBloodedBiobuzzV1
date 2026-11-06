package org.firstinspires.ftc.teamcode.opmodes.tuning;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Utility;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.opmodes.RobotOpMode;

@Utility(name = "Manual Turret Tuner")
@Config("Turret Tuner")
public class TurretTunerPID extends RobotOpMode {
    public static boolean on = false;
    public static double target = 0;
    public static boolean waveEnabled = false;
    public static boolean sineWave = false;
    public static double wavePeriod = 1;
    public static double waveHigh = 90;
    public static double waveLow = 0;
    private final ElapsedTime timer = new ElapsedTime();

    @Override
    public void init() {
        super.init();
        turret.setStartingAngle(0);
    }

    @Override
    public void loop() {
        wrapLoop(() -> {
            if (on) {
                turret.on();
                if (waveEnabled) {
                    if (sineWave) {
                        target = waveLow + (waveHigh - waveLow) / 2 * (Math.sin(2 * Math.PI * timer.seconds() / wavePeriod) + 1);
                    } else {
                        if (timer.seconds() > wavePeriod / 2) {
                            timer.reset();
                            target = turret.getTargetDegrees() == waveLow ? waveHigh : waveLow;
                        }
                    }
                }
                turret.setTargetDegrees(target);
            } else {
                turret.off();
            }
        });
    }
}