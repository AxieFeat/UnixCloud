plugins {
    kotlin("jvm")
}

group = rootProject.group
version = rootProject.version

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