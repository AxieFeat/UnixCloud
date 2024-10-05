package net.unix.cloud.modification.extension

import net.unix.api.modification.extension.ExtensionInfo
import kotlin.jvm.Throws

@Suppress("UNCHECKED_CAST")
data class CloudExtensionInfo(
    override val main: String,
    override val name: String,
    override val version: String,
    override val description: String,
    override val website: String,
    override val authors: List<String>
) : ExtensionInfo {
    
    companion object {
        @Throws(IllegalArgumentException::class)
        fun deserialize(serialized: Map<String, Any>): CloudExtensionInfo {
            val ex = IllegalArgumentException("Incorrect data, check your serialized data!")
            
            val main = serialized["main"] as String? ?: throw ex
            val name = serialized["name"] as String? ?: throw ex
            val version = serialized["version"] as String? ?: throw ex
            val description = serialized["description"] as String? ?: throw ex
            val website = serialized["website"] as String? ?: throw ex
            val authors = serialized["authors"] as List<String>?  ?: throw ex
            
            return CloudExtensionInfo(main, name, version, description, website, authors)
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
        
        return result
    }
    
}