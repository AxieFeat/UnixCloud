plugins {
    kotlin("jvm")
}

group = rootProject.group
version = rootProject.version

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":unix-network"))
    implementation(project(":unix-command-api"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}