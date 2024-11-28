plugins {
    kotlin("jvm")
}

group = "net.unix.cloud"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":unix-network"))
    implementation(project(":unix-scheduler"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}