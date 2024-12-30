import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = rootProject.group
version = rootProject.version

tasks.withType<ShadowJar> {
    archiveFileName.set("unix-module-test.jar")
}