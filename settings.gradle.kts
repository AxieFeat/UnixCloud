plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "UnixCloud"
include("unix-api")
include("unix-cloud")
include("unix-annotation-processor")
include("unix-network")
