plugins {
    id("dev.frozenmilk.teamcode") version "12.0.0-1.2.1"
    id("dev.frozenmilk.sinister.sloth.load") version "0.3.1"
}

ftc {
    sdk.TeamCode()
    dairy {
        implementation(Sloth)
        implementation(slothboard)
    }
}

dependencies {
    implementation("com.pedropathing:revhub:3.0.1")
    implementation("com.pedropathing:tuning:1.0.2")
    implementation("com.pedropathing:ivy:1.0.0")
    implementation("dev.frozenmilk.dairy:CachingHardware:1.0.0")
}