import java.net.URI
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("maven-publish")
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "net.unix.cloud"
version = "1.0"
description = "Automatic service management system"

allprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")

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

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["kotlin"])

                groupId = "net.unix.cloud"
                artifactId = project.name

                pom {
                    name.set(project.properties["POM_NAME"].toString())
                    description.set(project.description)

                    scm {
                        url.set("https://github.com/AxieFeat/UnixCloud")
                    }

                    developers {
                        developer {
                            id.set("AxieFeat")
                            name.set("Kirill Arial")
                            email.set("kirill@arial.su")
                            url.set("https://arial.su")
                        }
                    }
                }
            }
        }
    }

    tasks.withType<DokkaTask>().configureEach {
        moduleName.set(project.name)
        moduleVersion.set(project.version.toString())
        outputDirectory.set(layout.buildDirectory.dir("dokka/$name"))
        failOnWarning.set(false)
        suppressObviousFunctions.set(true)
        suppressInheritedMembers.set(false)
        offlineMode.set(false)
    }

    tasks.test {
        useJUnitPlatform()
    }
    kotlin {
        jvmToolchain(11)
    }
}