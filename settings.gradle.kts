plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "UnixCloud"
include("unix-api")
include("unix-cloud")
include("unix-annotation-processor")
include("unix-network")
include("unix-modifications")
include("unix-modifications:unix-modules")
include("unix-modifications:unix-modules:unix-module-test")
include("unix-modifications:unix-extensions")
include("unix-modifications:unix-extensions:unix-extension-test")
include("unix-example-app")
include("unix-scheduler")
include("unix-event-system")
include("unix-service-api")
