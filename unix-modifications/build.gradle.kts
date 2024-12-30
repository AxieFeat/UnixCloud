group = rootProject.group
version = rootProject.version

subprojects {

    dependencies {
        compileOnly(project(":unix-node"))
        compileOnly(project(":unix-api"))
        compileOnly(project(":unix-network"))
        compileOnly(project(":unix-scheduler"))
        compileOnly(project(":unix-event-system"))
    }

}