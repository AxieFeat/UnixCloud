group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":unix-scheduler"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

    implementation("kryonet:kryonet:2.21")
    implementation("com.esotericsoftware:kryo:5.6.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
}