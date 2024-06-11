import java.net.URI

plugins {
    kotlin("jvm") version "1.9.23"
}

group = "net.unix.cloud"
version = "1.0"

allprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()

        maven {
            url = URI("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            url = URI("https://libraries.minecraft.net")
        }
    }

    dependencies {
        implementation("com.mojang:brigadier:1.0.18")
        implementation("org.jline:jline:3.26.1")
        implementation("com.google.guava:guava:33.2.1-jre")
        implementation("org.ow2.asm:asm-tree:9.0")
        implementation("org.ow2.asm:asm-commons:9.0")
        implementation("org.ow2.asm:asm:9.0")
        implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}