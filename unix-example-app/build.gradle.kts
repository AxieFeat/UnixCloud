import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.unix.cloud"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":unix-driver"))
    implementation(project(":unix-network"))
    implementation(project(":unix-scheduler"))
    implementation(project(":unix-api"))
    implementation(project(":unix-node"))
    implementation(project(":unix-command-api"))
    implementation(project(":unix-modifications:unix-modules:unix-module-test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    archiveFileName.set("example-app.jar")

    manifest {
        attributes["Main-Class"] = "net.unix.example.app.ExampleKt"
    }
}

kotlin {
    jvmToolchain(11)
}