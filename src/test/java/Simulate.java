import org.codeblooded.ftcodesim.ascope.SeasonField;
import org.codeblooded.ftcodesim.ascope.SourceType;
import org.codeblooded.ftcodesim.ascope.boundaries.RobotGeometry;
import org.codeblooded.ftcodesim.hardware.SimHardwareMap;
import org.codeblooded.ftcodesim.hardware.devices.*;
import org.codeblooded.ftcodesim.hardware.drivetrain.SimulatedDrivetrain;
import org.codeblooded.ftcodesim.hardware.drivetrain.SimulatedMecanum;
import org.codeblooded.ftcodesim.simulator.SimConfig;
import org.codeblooded.ftcodesim.hardware.drivetrain.SimMecanumConfig;
import org.codeblooded.ftcodesim.input.DefaultKeybinds;
import org.codeblooded.ftcodesim.simulator.FTCodeSim;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.junit.Test;

import java.io.IOException;

public class Simulate {
    @Test
    public void test() throws IOException, InterruptedException {
        SimMecanumConfig config = new SimMecanumConfig();
        config.frontLeftMotorName = "frontLeft";
        config.frontRightMotorName = "frontRight";
        config.backLeftMotorName = "backLeft";
        config.backRightMotorName = "backRight";
        config.wheelbase = 16;
        config.trackWidth = 16;
        config.wheelRadius = 1.889765;
        config.staticVelocityRegion = 4;
        config.staticFriction = 30;

        config.maxAcceleration = 250;
        config.turnNaturalDeceleration = 2.5;
        config.maxTurnAccel = 10;
        config.maxAngularVelocity = 8;
        config.field = SeasonField.BIOBUZZ;

        config.robotGeometry = new RobotGeometry(6.15 * 2, 7.28 * 2, 6, 0);
        config.robotModel = SourceType.ROBOT_MECANUM_BASE;

        config.foresightConfig = Constants.foresightConfig;

        SimulatedDrivetrain drivetrain = new SimulatedMecanum(config);

        SimHardwareMap simHardwareMap = new SimHardwareMap();
        simHardwareMap.register(drivetrain);
        simHardwareMap.register("octoquad", new SimOctoquad(drivetrain));
        simHardwareMap.register("pinpoint", new SimPinpoint(drivetrain));
        simHardwareMap.register("lowerIntake", new SimMotor(SimMotorConfig.gobilda5000("lowerIntake", new SimVoltageSensor())));
        simHardwareMap.register("upperIntake", new SimMotor(SimMotorConfig.gobilda5000("upperIntake", new SimVoltageSensor())));
        simHardwareMap.register("flywheel", new SimMotor(SimMotorConfig.gobilda5000("flywheel", new SimVoltageSensor())));
        simHardwareMap.register("turret", new SimMotor(SimMotorConfig.gobilda5000("turret", new SimVoltageSensor())));
        simHardwareMap.register("blocker", new SimServo());
        simHardwareMap.register("corbicula", new SimServo());
        simHardwareMap.register("pollenSensor1", new SimTouchSensor());
        simHardwareMap.register("pollenSensor2", new SimTouchSensor());
        simHardwareMap.register("pollenSensor3", new SimTouchSensor());
        simHardwareMap.register("pollenSensor4", new SimTouchSensor());

        SimConfig simConfig = new SimConfig();
        simConfig.gamepad1Keybinds = new DefaultKeybinds();
        simConfig.gamepad2Keybinds = new DefaultKeybinds();
        simConfig.simHardwareMap = simHardwareMap;
        simConfig.loopTimeMs = 10;
        simConfig.field = SeasonField.BIOBUZZ;
        simConfig.autoConfigureAscope = true;

        FTCodeSim sim = new FTCodeSim(simConfig);
        sim.run();
    }
}