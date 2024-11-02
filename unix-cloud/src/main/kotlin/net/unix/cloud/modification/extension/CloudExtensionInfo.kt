package net.unix.cloud.modification.extension

import net.unix.api.modification.extension.ExtensionInfo
import kotlin.jvm.Throws

@Suppress("UNCHECKED_CAST")
data class CloudExtensionInfo(
    override val main: String,
    override val priority: Double,
    override val name: String,
    override val version: String,
    override val description: String,
    override val website: String,
    override val authors: List<String>
) : ExtensionInfo {
    
    companion object {
        @Throws(IllegalArgumentException::class)
        fun deserialize(serialized: Map<String, Any>): CloudExtensionInfo {

            val main = serialized["main"].toString()
            val priority = serialized["priority"].toString().toDoubleOrNull() ?: 0.0
            val name = serialized["name"].toString()
            val version = serialized["version"].toString()
            val description = serialized["description"].toString()
            val website = serialized["website"].toString()
            val authors = serialized["authors"] as List<String>?  ?: listOf()
            
            return CloudExtensionInfo(main, priority, name, version, description, website, authors)
        }
    }
    
    override fun serialize(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        
        result["main"] = main
        result["priority"] = priority
        result["name"] = name
        result["version"] = version
        result["description"] = description
        result["website"] = website
        result["authors"] = authors
        
        return result
    }
    
}