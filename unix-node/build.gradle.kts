group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":unix-api"))
    implementation(project(":unix-network"))
    implementation(project(":unix-scheduler"))
    implementation(project(":unix-event-system"))
    implementation(project(":unix-command-api"))

    implementation("org.fusesource.jansi:jansi:2.4.1")

    implementation("org.yaml:snakeyaml:2.2")

    implementation("org.jline:jline:3.26.1")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-ansi:4.17.0")

    implementation("com.google.code.gson:gson:2.11.0")

    implementation("org.slf4j:slf4j-api:1.8.0-beta4")
    implementation("org.slf4j:slf4j-simple:1.8.0-beta4")

    implementation("io.javalin:javalin:3.13.7")
    implementation("com.auth0:java-jwt:3.16.0")
    implementation("com.github.kmehrunes:javalin-jwt:v0.2")

    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
    implementation("com.mysql:mysql-connector-j:9.1.0")

    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.jdbi:jdbi3-core:3.38.0-rc3")
    implementation("org.jdbi:jdbi3-caffeine-cache:3.38.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.6")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject:3.38.0-rc3")
}