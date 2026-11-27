plugins {
    id("dev.frozenmilk.teamcode") version "12.0.0-1.2.3"
    id("dev.frozenmilk.sinister.sloth.load") version "0.3.2"
}

repositories {
    maven("https://jitpack.io/")
}

ftc {
    sdk.TeamCode()
    dairy {
        implementation(Sloth)
        implementation(slothboard)
    }
}

dependencies {
    implementation("com.pedropathing:revhub:3.0.2")
    implementation("com.pedropathing:tuning:1.0.3")
    implementation("com.pedropathing.ivy:pedro:1.1.1")
    implementation("dev.frozenmilk.dairy:CachingHardware:1.0.0")

    implementation("com.github.haifengl:smile-interpolation:2.6.0") {
        exclude(group = "org.slf4j")
    }
    implementation("dev.nextftc.v2:telemetry-ftc:0.4.0")

    testImplementation("org.codeblooded.ftcodesim:ftcodesim:0.3.1-beta")
    testImplementation("junit:junit:4.13.2")
}