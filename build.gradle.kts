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
    fateWeaver {
        implementation(ftc("0.5.1"))
    }
}

dependencies {
    implementation("com.pedropathing:revhub:3.0.2")
    implementation("com.pedropathing:tuning:1.0.3-beta.1")
    implementation("com.pedropathing.ivy:pedro:1.1.1")
    implementation("dev.frozenmilk.dairy:CachingHardware:1.0.0")

    testImplementation("org.codeblooded.ftcodesim:ftcodesim:0.2.1-beta")
    testImplementation("junit:junit:4.13.2")
}