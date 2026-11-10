package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.follower.Follower;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.OctoQuadLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.*;

public class Tuning {
    @Tuner(name = "OctoQuad Tuner")
    public static Procedure octoQuadTuner() {
        return new OctoQuadTuner();
    }

    @Tuner(name = "Foresight Tuner")
    public static Procedure foresightTuner() {
        return new ForesightTuner(h -> new OctoQuadLocalizer(h, Constants.localizerConfig), h -> new Mecanum(h, Constants.drivetrainConfig));
    }

    @Tuner(name = "Tests")
    public static Procedure tests() {
        return new Tests(h -> new Follower(new OctoQuadLocalizer(h, Constants.localizerConfig), new Mecanum(h, Constants.drivetrainConfig), new Foresight(Constants.foresightConfig)));
    }
}
