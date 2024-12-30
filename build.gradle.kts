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

        maven {
            url = URI("https://jitpack.io")
        }
    }

    dependencies {
        testImplementation(kotlin("test"))
        implementation(kotlin("reflect"))

        implementation("io.insert-koin:koin-core:4.0.0")
        implementation("org.jetbrains:annotations:26.0.1")

    }

    tasks.test {
        useJUnitPlatform()
    }
    kotlin {
        jvmToolchain(11)
    }
}