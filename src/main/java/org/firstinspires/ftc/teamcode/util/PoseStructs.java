package org.firstinspires.ftc.teamcode.util;

import android.content.Context;
import androidx.annotation.NonNull;
import com.pedropathing.math.Pose;
import com.qualcomm.ftccommon.FtcEventLoop;
import dev.frozenmilk.sinister.sdk.apphooks.OnCreateEventLoop;
import dev.nextftc.telemetry.Telemetry;
import dev.nextftc.util.struct.StructSchema;
import kotlin.jvm.JvmClassMappingKt;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import static dev.nextftc.util.struct.StructSchemaKt.structSchema;

public final class PoseStructs implements OnCreateEventLoop {
    public static final PoseStructs INSTANCE = new PoseStructs();
    private static final double halfField = 141.5 / 2;
    private static final StructSchema<Pose> translationSchema = structSchema("Translation2d", b -> {
        b.float64("x", pose -> DistanceUnit.INCH.toMeters(-pose.y() + halfField));
        b.float64("y", pose -> DistanceUnit.INCH.toMeters(pose.x() - halfField));
        return null;
    });
    private static final StructSchema<Pose> headingSchema = structSchema("Rotation2d", b -> {
        b.float64("value", pose -> pose.heading() + Math.PI / 2);
        return null;
    });
    private static final StructSchema<Pose> poseSchema = structSchema("Pose2d", b -> {
        b.struct("translation", translationSchema, pose -> pose);
        b.struct("rotation", headingSchema, pose -> pose);
        return null;
    });

    private PoseStructs() {
    }

    @Override
    public void onCreateEventLoop(@NonNull Context context, @NonNull FtcEventLoop ftcEventLoop) {
        Telemetry.getRegistry().getStructs().register(JvmClassMappingKt.getKotlinClass(Pose.class), poseSchema);
    }
}
