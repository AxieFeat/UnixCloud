import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.2.2"
}

group = rootProject.group
version = rootProject.version

dependencies {
    compileOnly(project(":unix-node"))
    compileOnly(project(":unix-api"))
    compileOnly(project(":unix-network"))
    compileOnly(project(":unix-scheduler"))
    compileOnly(project(":unix-event-system"))

    implementation("io.javalin:javalin:7.2.3")
    implementation("com.github.kmehrunes:javalin-jwt:6.0.0")
    implementation("org.apache.commons:commons-lang3:3.21.0")
    implementation("com.google.code.gson:gson:2.14.0")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("unix-rest.jar")
}