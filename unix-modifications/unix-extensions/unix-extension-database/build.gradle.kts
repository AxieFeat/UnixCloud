group = rootProject.group
version = rootProject.version

dependencies {
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.jdbi:jdbi3-core:3.38.0-rc3")
    implementation("org.jdbi:jdbi3-caffeine-cache:3.38.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.6")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject:3.38.0-rc3")
}