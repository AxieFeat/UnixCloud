import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":unix-driver"))
    implementation(project(":unix-network"))
    implementation(project(":unix-scheduler"))
    implementation(project(":unix-api"))
    implementation(project(":unix-node"))
    implementation(project(":unix-command-api"))
    implementation(project(":unix-modifications:unix-modules:unix-module-test"))
}

tasks.withType<ShadowJar> {
    archiveFileName.set("example-app.jar")

    manifest {
        attributes["Main-Class"] = "net.unix.example.app.ExampleKt"
    }
}