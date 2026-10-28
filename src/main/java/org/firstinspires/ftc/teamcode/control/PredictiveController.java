package org.firstinspires.ftc.teamcode.control;

public class PredictiveController {
    double kP;
    double kL;
    double kQ;

    public PredictiveController(double kP, double kLinear, double kQuadratic) {
        this.kP = kP;
        this.kL = kLinear;
        this.kQ = kQuadratic;
    }

    public double getOutput(double error, double velocity) {
        return kP * (error - velocity * kL - velocity * velocity * kQ);
    }

    public void setGains(double kP, double kL, double kQ) {
        this.kP = kP;
        this.kL = kL;
        this.kQ = kQ;
    }
}
