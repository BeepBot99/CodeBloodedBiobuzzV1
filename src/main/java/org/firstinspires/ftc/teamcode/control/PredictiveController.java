package org.firstinspires.ftc.teamcode.control;

public class PredictiveController {
    double kP;
    double kLinear;
    double kQuadratic;

    public PredictiveController(double kP, double kLinear, double kQuadratic) {
        this.kP = kP;
        this.kLinear = kLinear;
        this.kQuadratic = kQuadratic;
    }

    public double getOutput(double error, double velocity) {
        return kP * (error - velocity * kLinear - velocity * velocity * kQuadratic);
    }
}
