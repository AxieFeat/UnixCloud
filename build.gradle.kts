import java.net.URI

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

group = "net.unix.cloud"
version = "1.0"

allprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    repositories {
        mavenCentral()

        maven {
            url = URI("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            url = URI("https://libraries.minecraft.net")
        }

        maven {
            url = URI("https://clojars.org/repo/")
        }
    }

    dependencies {
        implementation("kryonet:kryonet:2.21")
        implementation("com.esotericsoftware:kryo:5.6.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
        implementation("org.fusesource.jansi:jansi:2.4.1")
        implementation("org.yaml:snakeyaml:2.2")
        implementation("com.mojang:brigadier:1.0.18")
        implementation("org.jline:jline:3.26.1")

        implementation("com.google.guava:guava:33.2.1-jre")

        implementation("org.ow2.asm:asm-tree:9.0")
        implementation("org.ow2.asm:asm-commons:9.0")
        implementation("org.ow2.asm:asm:9.0")

        implementation("org.apache.logging.log4j:log4j-core:2.23.1")
        implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1")

        implementation("net.kyori:adventure-api:4.17.0")
        implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
        implementation("net.kyori:adventure-text-minimessage:4.17.0")
        implementation("net.kyori:adventure-text-serializer-ansi:4.17.0")
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