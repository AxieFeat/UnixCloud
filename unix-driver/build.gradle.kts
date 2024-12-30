group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":unix-network"))
    implementation(project(":unix-scheduler"))
    implementation(project(":unix-api"))
    implementation(project(":unix-node"))
}