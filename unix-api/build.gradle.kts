plugins {
    kotlin("jvm")
}

group = "net.unix.cloud"
version = "1.0"

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":unix-network"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}