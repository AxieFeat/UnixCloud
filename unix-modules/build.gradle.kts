plugins {
    kotlin("jvm")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

subprojects {

    dependencies {
        compileOnly(project(":unix-cloud"))
        compileOnly(project(":unix-api"))
        compileOnly(project(":unix-network"))
    }

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