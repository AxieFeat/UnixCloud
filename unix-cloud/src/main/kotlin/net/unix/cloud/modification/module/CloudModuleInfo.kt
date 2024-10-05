package net.unix.cloud.modification.module

import net.unix.api.modification.module.ModuleInfo
import kotlin.jvm.Throws

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
        @Throws(IllegalArgumentException::class)
        fun deserialize(serialized: Map<String, Any>): CloudModuleInfo {
            val ex = IllegalArgumentException("Incorrect data, check your serialized data!")

            val main = serialized["main"] as String? ?: throw ex
            val name = serialized["name"] as String? ?: throw ex
            val version = serialized["version"] as String? ?: throw ex
            val description = serialized["description"] as String? ?: throw ex
            val website = serialized["website"] as String? ?: throw ex
            val authors = serialized["authors"] as List<String>?  ?: throw ex
            val depends = serialized["depends"] as List<String>? ?: throw ex
            val soft = serialized["soft"] as List<String>? ?: throw ex

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