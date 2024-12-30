plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "UnixCloud"
include("unix-api")
include("unix-network")
include("unix-modifications")
include("unix-modifications:unix-modules")
include("unix-modifications:unix-modules:unix-module-test")
include("unix-modifications:unix-extensions")
include("unix-modifications:unix-extensions:unix-extension-test")
include("unix-example-app")
include("unix-scheduler")
include("unix-event-system")
include("unix-command-api")
include("unix-driver")
include("unix-node")
include("unix-launcher")
include("unix-manager")
include("unix-modifications:unix-modules:unix-module-rest")
findProject(":unix-modifications:unix-modules:unix-module-rest")?.name = "unix-module-rest"
include("unix-modifications:unix-extensions:unix-extension-database")
findProject(":unix-modifications:unix-extensions:unix-extension-database")?.name = "unix-extension-database"
