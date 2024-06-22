package net.unix.api.module

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
    companion object {}
}