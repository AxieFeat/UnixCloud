group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":unix-network"))
    implementation(project(":unix-scheduler"))
    implementation(project(":unix-api"))
    implementation(project(":unix-node"))

    implementation("kryonet:kryonet:2.21")
    implementation("com.esotericsoftware:kryo:5.6.0")
}