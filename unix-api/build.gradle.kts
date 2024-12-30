group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":unix-network"))
    implementation(project(":unix-command-api"))

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("org.fusesource.jansi:jansi:2.4.1")
}