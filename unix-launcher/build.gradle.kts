import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":unix-api"))
    implementation(project(":unix-network"))
    implementation(project(":unix-scheduler"))
    implementation(project(":unix-event-system"))
    implementation(project(":unix-command-api"))
    implementation(project(":unix-node"))
    implementation(project(":unix-manager"))
}

tasks.withType<ShadowJar> {
    archiveFileName.set("unixcloud.jar")

    manifest {
        attributes["Main-Class"] = "net.unix.launcher.CloudInstallerKt"
    }
}