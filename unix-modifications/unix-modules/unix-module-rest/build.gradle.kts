import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly(project(":unix-node"))
    compileOnly(project(":unix-api"))
    compileOnly(project(":unix-network"))
    compileOnly(project(":unix-scheduler"))
    compileOnly(project(":unix-event-system"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    archiveFileName.set("unix-rest.jar")
}

kotlin {
    jvmToolchain(11)
}