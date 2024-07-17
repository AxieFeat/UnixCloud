package net.unix.api.module.annotation

/**
 * Annotation for creation modules
 *
 * @param name Module name
 * @param version Module version
 * @param description Module description
 * @param website Module website
 * @param authors Module authors
 * @param depends Module required dependencies
 * @param softDepends Module optional dependencies
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class ModuleInfo(
    val name: String,
    val version: String,
    val description: String = "Not set",
    val website: String= "Not set",
    val authors: Array<out String> = [],
    val depends: Array<out String> = [],
    val softDepends: Array<out String> = []
)