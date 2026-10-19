plugins {
    id("dev.frozenmilk.teamcode") version "12.0.0-1.2.3"
    id("dev.frozenmilk.sinister.sloth.load") version "0.3.2"
}

repositories {
    maven("https://www.jitpack.io")
    maven("https://central.sonatype.com/repository/maven-snapshots")
    maven("https://maven.brott.dev/")
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
    implementation("com.pedropathing:tuning:1.0.2")
    implementation("com.pedropathing:ivy:1.0.0")
    implementation("dev.frozenmilk.dairy:CachingHardware:1.0.0")

    testImplementation("org.codeblooded.ftcodesim:ftcodesim:0.2.1-beta")
    testImplementation("junit:junit:4.13.2")
}