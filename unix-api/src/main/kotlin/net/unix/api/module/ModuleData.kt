package net.unix.api.module

/**
 * Some information of module
 *
 * @param main Main class path
 * @param name Module name
 * @param version Module version
 * @param description Module description
 * @param website Module website
 * @param authors Module author's
 * @param depends Module required dependencies
 * @param softDepends Module optional dependencies
 */
data class ModuleData(
    val main: String? = null,
    val name: String? = null,
    val version: String? = null,
    val description: String? = null,
    val website: String? = null,
    val authors: List<String>? = null,
    val depends: List<String>? = null,
    val softDepends: List<String>? = null
) {
    companion object
}