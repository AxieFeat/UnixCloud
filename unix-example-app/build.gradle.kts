import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.2.2"
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":unix-driver"))
    implementation(project(":unix-network"))
    implementation(project(":unix-scheduler"))
    implementation(project(":unix-api"))
    //implementation(project(":unix-node"))
    implementation(project(":unix-command-api"))
}

tasks.withType<ShadowJar> {
    archiveFileName.set("example-app.jar")

    manifest {
        attributes["Main-Class"] = "net.unix.example.app.ExampleKt"
    }
}