package net.unix.cloud.modification.module

import net.unix.api.modification.module.ModuleInfo

@Suppress("UNCHECKED_CAST")
data class CloudModuleInfo(
    override val main: String,
    override val name: String,
    override val version: String,
    override val description: String,
    override val website: String,
    override val authors: List<String>,
    override val depends: List<String>,
    override val soft: List<String>,
) : ModuleInfo {

    companion object {
        fun deserialize(serialized: Map<String, Any>): CloudModuleInfo {

            val main = serialized["main"].toString()
            val name = serialized["name"].toString()
            val version = serialized["version"].toString()
            val description = serialized["description"].toString()
            val website = serialized["website"].toString()
            val authors = serialized["authors"] as List<String>?  ?: listOf()
            val depends = serialized["depends"] as List<String>? ?: listOf()
            val soft = serialized["soft"] as List<String>? ?: listOf()

            return CloudModuleInfo(main, name, version, description, website, authors, depends, soft)
        }
    }

    override fun serialize(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()

        result["main"] = main
        result["name"] = name
        result["version"] = version
        result["description"] = description
        result["website"] = website
        result["authors"] = authors

        result["depends"] = depends
        result["soft"] = soft

        return result
    }

}