package net.unix.api.modification.extension.annotation

/**
 * Annotation for creation extensions
 *
 * @param name Extension name
 * @param version Extension version
 * @param description Extension description
 * @param website Extension website
 * @param authors Extension authors
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtensionInfo(
    val name: String,
    val version: String,
    val description: String = "Not set",
    val website: String= "Not set",
    val authors: Array<out String> = []
)
